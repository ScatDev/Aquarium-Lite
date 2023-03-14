package dev.scat.aquarium.data.processor.impl;

import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerFlying;
import dev.scat.aquarium.data.PlayerData;
import dev.scat.aquarium.data.processor.Processor;
import dev.scat.aquarium.util.MathUtil;
import dev.scat.aquarium.util.PacketUtil;
import lombok.Getter;

@Getter
public class RotationProcessor extends Processor {

    private float yaw, pitch, lastYaw, lastPitch, deltaYaw,
            deltaPitch, lastDeltaYaw, lastDeltaPitch;

    private boolean cinematic;

    public RotationProcessor(PlayerData data) {
        super(data);
    }

    public void handle(PacketReceiveEvent event) {
        if (PacketUtil.isRotation(event.getPacketType())) {
            WrapperPlayClientPlayerFlying flying = new WrapperPlayClientPlayerFlying(event);

            lastYaw = yaw;
            lastPitch = pitch;
            lastDeltaYaw = deltaYaw;
            lastDeltaPitch = deltaPitch;

            yaw = flying.getLocation().getYaw();
            pitch = flying.getLocation().getPitch();

            deltaYaw = MathUtil.angleDistance(yaw, lastYaw);
            deltaPitch = MathUtil.angleDistance(pitch, lastPitch);

            // Basic cinematic processing here
        }
    }
}
