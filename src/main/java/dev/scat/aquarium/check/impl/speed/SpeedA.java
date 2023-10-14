package dev.scat.aquarium.check.impl.speed;

import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.potion.PotionTypes;
import dev.scat.aquarium.check.Check;
import dev.scat.aquarium.data.PlayerData;
import dev.scat.aquarium.util.PacketUtil;

import java.util.List;
import java.util.stream.Collectors;

public class SpeedA extends Check {

    public SpeedA(PlayerData data) {
        super(data, "Speed", "A", true);
    }

    @Override
    public void handle(PacketReceiveEvent event) {
        if (PacketUtil.isPosition(event.getPacketType())) {
            double deltaXZ = data.getPositionProcessor().getDeltaXZ();
            double lastDeltaXZ = data.getPositionProcessor().getLastDeltaXZ();

            boolean ground = data.getCollisionProcessor().isClientGround();
            boolean lastGround = data.getCollisionProcessor().isLastClientGround();
            boolean lastLastGround = data.getCollisionProcessor().isLastLastClientGround();
            int desyncTicks = data.getPositionProcessor().getTicksSincePosition();

            float friction = 0.91F;

            if (lastGround) {
                friction *= data.getCollisionProcessor().getLastFriction();
            }

            float lastFriction = 0.91F;

            if (lastLastGround) {
                lastFriction *= data.getCollisionProcessor().getLastLastFriction();
            }

            float f = 0.16277136F / (friction * friction * friction);

            float movementSpeed;

            if (lastGround) {
                double aiMoveSpeed = data.getAbilitiesProcessor().getMovementSpeed();

                aiMoveSpeed += aiMoveSpeed * 0.30000001192092896D;

                if (data.getPotionProcessor().hasEffect(PotionTypes.SPEED)) {
                    aiMoveSpeed += aiMoveSpeed * (data.getPotionProcessor().getAmplifier(PotionTypes.SPEED) + 1) * 0.20000000298023224D;
                }

                if (data.getPotionProcessor().hasEffect(PotionTypes.SLOWNESS)) {
                    aiMoveSpeed += aiMoveSpeed * (data.getPotionProcessor().getAmplifier(PotionTypes.SLOWNESS) + 1) * -0.15000000596046448D;
                }

                movementSpeed = (float) aiMoveSpeed;

                movementSpeed *= f;
            } else {
                // you can thank mojang for this line <3

                movementSpeed = (float) ((double) 0.02F + (double) 0.02F * 0.3D);
            }

            List<Double> movements = data.getVelocityProcessor().getPossibleVelocities().stream().map(velocity
                    -> Math.hypot(velocity.getX(), velocity.getZ())).collect(Collectors.toList());

            movements.add(lastDeltaXZ * lastFriction);

            double minOffset = Double.MAX_VALUE;

            for (double movement : movements) {
                if (!ground && lastGround) {
                    movement += 0.2F;
                }

                movement += movementSpeed;

                double offset = deltaXZ - movement;

                minOffset = Math.min(minOffset, offset);

                // Found legit movement
                if (minOffset < 0)
                    break;
            }

            boolean exempt = data.getCollisionProcessor().isNearPiston()
                    || data.getPositionProcessor().getTicksSinceTeleport() < 2
                    || data.getAbilitiesProcessor().getLastAbilities().isFlightAllowed()
                    || data.getAbilitiesProcessor().getLastAbilities().isFlying()
                    || data.getCollisionProcessor().isLastLastInWater()
                    || data.getCollisionProcessor().isLastLastInLava();

            if (minOffset > (desyncTicks > 0 ? 0.03 + 1E-5 : 1E-5) && !exempt && deltaXZ >= 0.2D) {
                if ((buffer += Math.max(1, minOffset / 0.05)) > 1) {
                    flag(String.format("offset=%s, ms=%.4f, g=%s, lg=%s, llg=%s, dt=%s",
                            minOffset, movementSpeed, ground, lastGround, lastLastGround, desyncTicks));

                    buffer = Math.min(10, buffer);
                }
            } else {
                buffer = Math.max(0, buffer - 0.025);
            }
        }
    }
}
