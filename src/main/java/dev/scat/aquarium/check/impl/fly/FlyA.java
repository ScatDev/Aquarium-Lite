package dev.scat.aquarium.check.impl.fly;

import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.player.ClientVersion;
import dev.scat.aquarium.check.Check;
import dev.scat.aquarium.data.PlayerData;
import dev.scat.aquarium.util.PacketUtil;

import java.util.ArrayList;
import java.util.List;

public class FlyA extends Check {

    private int lastDesyncTicks;

    public FlyA(PlayerData data) {
        super(data, "Fly", "A", true);
    }

    @Override
    public void handle(PacketReceiveEvent event) {
        if (PacketUtil.isPosition(event.getPacketType())) {
            if (data.getCollisionProcessor().getClientAirTicks() <= 1)
                return;

            double deltaY = data.getPositionProcessor().getDeltaY();
            double lastDeltaY = data.getPositionProcessor().getLastDeltaY();

            List<Double> lastDeltaYs = new ArrayList<>();
            lastDeltaYs.add(data.getPositionProcessor().getLastDeltaY());

            data.getVelocityProcessor().getPossibleVelocities().forEach(velocity
                    -> lastDeltaYs.add(velocity.getY()));

            // Don't know how many ticks 1.9 players had between pos, although it is maxed at 20
            int desyncTicks = data.getPositionProcessor().getTicksSincePosition();

            int maxTicks = data.getVersion().isNewerThanOrEquals(ClientVersion.V_1_9)
                    ? 20 : desyncTicks;

            boolean sentMotion = data.getPositionProcessor().isSentMotion();
            boolean lastBonking = data.getCollisionProcessor().isLastBonking();
            boolean bonking = data.getCollisionProcessor().isBonking();

            double closestDeltaY = 0, lowestOffset = 1000;
            boolean found = false;

            prediction:
            {
                for (double motionY : lastDeltaYs) {
                    for (int i = 0; i <= maxTicks; i++) {
//                        if (i == 0 && motionY != lastDeltaY) {
//                            if (Math.abs(motionY) < 0.005) motionY = 0;
//
//                            double offset = Math.abs(motionY - deltaY);
//
//                            if (offset < lowestOffset) {
//                                lowestOffset = offset;
//                                closestDeltaY = motionY;
//
//                                if (bonking && deltaY < motionY) {
//                                    found = true;
//                                    break prediction;
//                                } else if (offset < 1E-8) {
//                                    found = true;
//                                    break prediction;
//                                }
//                            }
//
//                            continue;
//                        }

                        if (i == 0 && lastBonking && deltaY < 0)
                            motionY = 0;

                        if (Math.abs(motionY) < 0.005) motionY = 0;

                        if (data.getPositionProcessor().isSentMotion()) {
                            motionY -= 0.08;
                        } else {
                            motionY = -0.1;
                        }

                        motionY *= 0.9800000190734863D;

                        double offset = Math.abs(motionY - deltaY);

                        if (offset < lowestOffset) {
                            lowestOffset = offset;
                            closestDeltaY = motionY;

                            if (bonking && deltaY < motionY) {
                                found = true;
                                break prediction;
                            } else if (offset < 1E-8) {
                                found = true;
                                break prediction;
                            }
                        }

                        // To do: collisions on 0.03 ticks
                    }
                }
            }

            boolean exempt = (deltaY > closestDeltaY && data.getCollisionProcessor().isServerGround())
                    || data.getCollisionProcessor().isLastLastInWater()
                    || data.getCollisionProcessor().isLastLastInLava()
                    || data.getCollisionProcessor().isLastInWater()
                    || data.getCollisionProcessor().isLastInLava()
                    || data.getCollisionProcessor().isOnSlime()
                    || data.getCollisionProcessor().isLastInWeb()
                    || data.getAbilitiesProcessor().getLastAbilities().isFlightAllowed()
                    || data.getAbilitiesProcessor().getLastAbilities().isFlying()
                    || data.getCollisionProcessor().isNearPiston()
                    || data.getCollisionProcessor().isLastOnClimbable()
                    || data.getPositionProcessor().getTicksSinceTeleport() < 3
                    || !sentMotion;
            // the sent motion exempt isnt very abusable because it checks if they send valid chunk motion
            // however if someone goes in an unloaded chunk and keeps sending invalid chunk motion they can send it as much as they want
            // once they stop sending chunk motion it doesn't exempt anymore

            double threshold = 1E-8 + lastDesyncTicks > 0 ? 0.03 : 0;

            if (!found && lowestOffset < threshold)
                return;

            if (!found && !exempt) {
                flag("o=" + lowestOffset + " dy=" + deltaY + " cdy=" + closestDeltaY + " b=" + bonking + " lb=" + lastBonking + " d=" + desyncTicks +" l=" + lastDesyncTicks);
            }
        } else if (PacketUtil.isFlying(event.getPacketType())) {
            lastDesyncTicks = data.getPositionProcessor().getTicksSincePosition();
        }
    }
}
