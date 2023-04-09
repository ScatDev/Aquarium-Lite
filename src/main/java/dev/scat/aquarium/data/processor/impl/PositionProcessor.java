package dev.scat.aquarium.data.processor.impl;

import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.util.Vector3d;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerFlying;
import dev.scat.aquarium.data.PlayerData;
import dev.scat.aquarium.data.processor.Processor;
import dev.scat.aquarium.util.PacketUtil;
import lombok.Getter;

@Getter
public class PositionProcessor extends Processor {

    private double x, y, z, lastX, lastY, lastZ,
            deltaX, deltaY, deltaZ, deltaXZ,
            lastDeltaX, lastDeltaY, lastDeltaZ, lastDeltaXZ;

    private boolean sentPosition;

    public PositionProcessor(PlayerData data) {
        super(data);
    }

    @Override
    public void handlePre(PacketReceiveEvent event) {
        if (PacketUtil.isFlying(event.getPacketType())) {
            sentPosition = false;
        }

        if (PacketUtil.isPosition(event.getPacketType())) {
            WrapperPlayClientPlayerFlying flying = new WrapperPlayClientPlayerFlying(event);

            lastX = x;
            lastY = y;
            lastZ = z;
            lastDeltaX = deltaX;
            lastDeltaY = deltaY;
            lastDeltaZ = deltaZ;
            lastDeltaXZ = deltaXZ;

            x = flying.getLocation().getX();
            y = flying.getLocation().getY();
            z = flying.getLocation().getZ();

            deltaX = x - lastX;
            deltaY = y - lastY;
            deltaZ = z - lastZ;

            deltaXZ = Math.hypot(deltaX, deltaZ);

            sentPosition = true;
        }
    }
}
