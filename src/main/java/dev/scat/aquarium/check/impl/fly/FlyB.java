package dev.scat.aquarium.check.impl.fly;

import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.potion.PotionTypes;
import dev.scat.aquarium.check.Check;
import dev.scat.aquarium.data.PlayerData;
import dev.scat.aquarium.util.PacketUtil;

public class FlyB extends Check {

    public FlyB(PlayerData data) {
        super(data, "Fly", "B");
    }

    @Override
    public void handle(PacketReceiveEvent event) {
        if (PacketUtil.isPosition(event.getPacketType())) {
            if (data.getCollisionProcessor().getClientAirTicks() != 1
                    || !data.getPositionProcessor().isSentMotion())
                return;

            double deltaY = data.getPositionProcessor().getDeltaY();
            boolean bonking = data.getCollisionProcessor().isBonking();
            boolean serverGround = data.getCollisionProcessor().isServerGround();

            boolean valid = true;

            if (deltaY >= 0) {
                double jumpMotion = 0.42F;

                if (data.getPotionProcessor().hasEffect(PotionTypes.JUMP_BOOST)) {
                    jumpMotion += ((float) (data.getPotionProcessor().getAmplifier(PotionTypes.JUMP_BOOST) + 1) * 0.1F);
                }

                if (Math.abs(deltaY - jumpMotion) > 1E-8) {
                    if (!bonking || deltaY > jumpMotion) valid = false;
                }
            } else {
                double expected = -0.08 * 0.9800000190734863D;

                if (Math.abs(deltaY - expected) > 1E-8) {
                    if (!serverGround || deltaY < expected) valid = false;
                }
            }

            boolean exempt = data.getCollisionProcessor().isLastLastInWater()
                    || data.getCollisionProcessor().isLastLastInLava()
                    || data.getCollisionProcessor().isLastInWater()
                    || data.getCollisionProcessor().isLastInLava()
                    || data.getCollisionProcessor().isOnSlime()
                    || data.getCollisionProcessor().isLastInWeb()
                    || data.getAbilitiesProcessor().getLastAbilities().isFlightAllowed()
                    || data.getAbilitiesProcessor().getLastAbilities().isFlying()
                    || data.getCollisionProcessor().isNearPiston()
                    || data.getCollisionProcessor().isLastLastOnClimbable()
                    || data.getPositionProcessor().getTicksSinceTeleport() < 3;

            if (!valid && !exempt) {
                if (++buffer > 1) {
                    flag("dy=" + deltaY);

                    buffer = Math.min(10, buffer);
                }
            } else {
                buffer = Math.max(0, buffer - 0.025);
            }
        }
    }
}
