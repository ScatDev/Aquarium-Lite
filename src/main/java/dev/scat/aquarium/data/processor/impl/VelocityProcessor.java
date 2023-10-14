package dev.scat.aquarium.data.processor.impl;

import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityVelocity;
import dev.scat.aquarium.check.Check;
import dev.scat.aquarium.data.PlayerData;
import dev.scat.aquarium.data.processor.Processor;
import dev.scat.aquarium.util.PacketUtil;
import dev.scat.aquarium.util.lag.ConfirmedVelocity;
import lombok.Getter;
import org.bukkit.Bukkit;

import java.util.ArrayDeque;


@Getter
public class VelocityProcessor extends Processor {

    private Check velocityB;

    private final ArrayDeque<ConfirmedVelocity> possibleVelocities = new ArrayDeque<>();
    private ConfirmedVelocity lastRemovedVelocity = new ConfirmedVelocity(666.666171221, 0, 0);

    public VelocityProcessor(PlayerData data) {
        super(data);
    }

    @Override
    public void handlePost(PacketReceiveEvent event) {
        if (PacketUtil.isFlying(event.getPacketType())) {
            possibleVelocities.removeIf(ConfirmedVelocity::isConfirmed);
        }
    }

    @Override
    public void handlePre(PacketSendEvent event) {
        if (event.getPacketType() != PacketType.Play.Server.ENTITY_VELOCITY) return;

        WrapperPlayServerEntityVelocity wrapper = new WrapperPlayServerEntityVelocity(event);

        if (wrapper.getEntityId() != data.getPlayer().getEntityId()) return;

        final ConfirmedVelocity confirmedVelocity = new ConfirmedVelocity(wrapper.getVelocity());

        data.getTransactionProcessor().confirmPre(()
                -> possibleVelocities.add(confirmedVelocity));

        data.getTransactionProcessor().confirmPost(() -> {
            possibleVelocities.stream().filter(velocity -> velocity.equals(confirmedVelocity)).
                    findAny().
                    ifPresent(velocity -> velocity.setConfirming(false));

            if (possibleVelocities.size() > 1)
                this.lastRemovedVelocity = possibleVelocities.removeFirst();
        });

    }
}
