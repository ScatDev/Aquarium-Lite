package dev.scat.aquarium.data.processor.impl;

import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.util.Vector3d;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityVelocity;
import dev.scat.aquarium.data.PlayerData;
import dev.scat.aquarium.data.processor.Processor;
import dev.scat.aquarium.util.PacketUtil;

import java.util.ArrayDeque;

public class VelocityProcessor extends Processor {

    private final ArrayDeque<Vector3d> velocities = new ArrayDeque<>();

    public VelocityProcessor(PlayerData data) {
        super(data);
    }

    @Override
    public void handlePost(PacketReceiveEvent event) {
        if (!PacketUtil.isFlying(event.getPacketType())) return;

        velocities.clear();
    }

    @Override
    public void handlePre(PacketSendEvent event) {
        if (event.getPacketType() != PacketType.Play.Server.ENTITY_VELOCITY) return;

        final WrapperPlayServerEntityVelocity wrapper = new WrapperPlayServerEntityVelocity(event);

        if (wrapper.getEntityId() != data.getPlayer().getEntityId()) return;

        final Vector3d vel = wrapper.getVelocity();

        data.getPledgeProcessor().confirmPre(() -> velocities.add(wrapper.getVelocity()));
        data.getPledgeProcessor().confirmPost(() -> {
            if (velocities.size() > 1)
                velocities.removeFirst();
        });

    }
}
