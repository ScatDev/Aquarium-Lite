package dev.scat.aquarium.check.impl.velocity;

import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.player.ClientVersion;
import com.github.retrooper.packetevents.protocol.potion.PotionTypes;
import com.github.retrooper.packetevents.util.Vector3d;
import dev.scat.aquarium.check.Check;
import dev.scat.aquarium.data.PlayerData;
import dev.scat.aquarium.data.processor.impl.AbilitiesProcessor;
import dev.scat.aquarium.data.processor.impl.CollisionProcessor;
import dev.scat.aquarium.data.processor.impl.PositionProcessor;
import dev.scat.aquarium.data.processor.impl.VelocityProcessor;
import dev.scat.aquarium.util.PacketUtil;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Salers
 * made on dev.scat.aquarium.check.impl.velocity
 */


public class VelocityA extends Check {

    public VelocityA(PlayerData data) {
        super(data, "Velocity", "A");
    }

    @Override
    public void handle(PacketReceiveEvent event) {
        if (!PacketUtil.isFlying(event.getPacketType())) return;

        final VelocityProcessor velocityProcessor = data.getVelocityProcessor();
        final CollisionProcessor collisionProcessor = data.getCollisionProcessor();

        final AbilitiesProcessor abilitiesProcessor = data.getAbilitiesProcessor();
        final PositionProcessor positionProcessor = data.getPositionProcessor();
        // exempt when the vertical movement could be altered
        if (collisionProcessor.isInLava() ||
                collisionProcessor.isInWater() ||
                collisionProcessor.isInWeb() ||
                !velocityProcessor.isTakingVelocity() ||
                collisionProcessor.isOnClimbable() ||
                abilitiesProcessor.getAbilities().isFlightAllowed() ||
                abilitiesProcessor.getAbilities().isFlying()) {

            buffer = Math.max(0, buffer - 0.1);
            return;
        }

        final List<Double> velocities = data.getVelocityProcessor().
                getVelocities().stream().mapToDouble(Vector3d::getY).boxed().collect(Collectors.toList());

        final double delta = positionProcessor.getDeltaY();
        final double min = data.getVersion().isOlderThanOrEquals(ClientVersion.V_1_8) ? 0.005D : 0.003D;

        final double jumpMotion = 0.42F + (data.getPotionProcessor().getAmplifier(PotionTypes.JUMP_BOOST) * 0.1F);

        if (Math.abs(delta - jumpMotion) < 0.03 || Math.abs(delta) < min || velocities.stream().anyMatch(velocity -> Math.abs(velocity) < min)) {
            buffer *= 0.75D;
            return;
        }

        final double offset = velocities.stream().mapToDouble(velocity -> {
            double diff = Math.abs(velocity - delta);

            if (diff > 1E-6) {
                double fixedVelocity = (velocity - 0.08) * 0.98F;
                if (Math.abs(fixedVelocity - delta) < diff) {
                    diff = Math.abs(fixedVelocity - delta);
                }
            }

            return diff;

        }).min().orElse(0);

        if (offset > 1E-10) {
            if (++buffer > 1)
                flag("offset=" + offset);
        } else buffer = Math.max(0, buffer - 1E-4);


    }
}
