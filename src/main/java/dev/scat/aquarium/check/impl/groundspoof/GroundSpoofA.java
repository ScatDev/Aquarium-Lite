package dev.scat.aquarium.check.impl.groundspoof;

import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.world.states.WrappedBlockState;
import dev.scat.aquarium.check.Check;
import dev.scat.aquarium.data.PlayerData;
import dev.scat.aquarium.util.PacketUtil;

public class GroundSpoofA extends Check {

    public GroundSpoofA(PlayerData data) {
        super(data, "GroundSpoof", "A", true);
    }

    @Override
    public void handle(PacketReceiveEvent event) {
        if (PacketUtil.isPosition(event.getPacketType())) {
            boolean serverGround = data.getCollisionProcessor().isServerGround();
            boolean clientGround = data.getCollisionProcessor().isClientGround();

            double deltaY = data.getPositionProcessor().getDeltaY();
            double lastDeltaY = data.getPositionProcessor().getLastDeltaY();

            boolean invalid = clientGround && !serverGround;

            boolean exempt = data.getVehicleProcessor().isInVehicle()
                    || data.getPositionProcessor().getTicksSinceTeleport() == 0
                    || data.getAbilitiesProcessor().getLastAbilities().isFlightAllowed()
                    || data.getAbilitiesProcessor().getLastAbilities().isFlying();

            WrappedBlockState below = data.getWorldProcessor().getBlock(data.getPositionProcessor().getX(), data.getPositionProcessor().getY() - 1, data.getPositionProcessor().getZ());

            if (invalid && !exempt) {
                if (++buffer > 1) {
                    flag("sg=" + serverGround + " cg=" + clientGround + " dy=" + deltaY + " ldy=" + lastDeltaY + " b=" + below.getType().getName());

                    buffer = Math.min(10, buffer);
                }
            } else {
                buffer = Math.max(0, buffer - 0.025);
            }
        }
    }
}
