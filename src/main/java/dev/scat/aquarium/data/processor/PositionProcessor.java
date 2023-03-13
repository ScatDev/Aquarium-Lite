package dev.scat.aquarium.data.processor;

import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.util.Vector3d;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerFlying;
import dev.scat.aquarium.data.PlayerData;
import dev.scat.aquarium.util.PacketUtil;
import lombok.Getter;

@Getter
public class PositionProcessor extends Processor {

    private double x, y, z, lastX, lastY, lastZ,
            deltaX, deltaY, deltaZ, deltaXZ,
            lastDeltaX, lastDeltaY, lastDeltaZ, lastDeltaXZ;

    public PositionProcessor(PlayerData data) {
        super(data);
    }

    @Override
    public void handle(PacketReceiveEvent event) {
        if (PacketUtil.isPosition(event.getPacketType())) {
            WrapperPlayClientPlayerFlying flying = new WrapperPlayClientPlayerFlying(event);

            Vector3d loc = flying.getLocation().getPosition();

            lastX = x;
            lastY = y;
            lastZ = z;
            lastDeltaX = deltaX;
            lastDeltaY = deltaY;
            lastDeltaZ = deltaZ;
            lastDeltaXZ = deltaXZ;

            x = loc.getX();
            y = loc.getY();
            z = loc.getZ();

            deltaX = x - lastX;
            deltaY = y - lastY;
            deltaZ = z - lastZ;

            deltaXZ = Math.hypot(deltaX, deltaZ);
        }
    }
}
