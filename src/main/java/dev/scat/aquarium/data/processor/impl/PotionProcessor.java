package dev.scat.aquarium.data.processor.impl;

import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.protocol.potion.PotionType;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityEffect;
import dev.scat.aquarium.data.PlayerData;
import dev.scat.aquarium.data.processor.Processor;

import java.util.HashMap;
import java.util.Map;

public class PotionProcessor extends Processor {

    /**
     * @key Effect type
     * @value Amplifier
     */
    private final Map<PotionType, Integer> potionMap = new HashMap<>();

    public PotionProcessor(PlayerData data) {
        super(data);
    }

    @Override
    public void handlePre(PacketSendEvent event) {
        if(event.getPacketType() == PacketType.Play.Server.ENTITY_EFFECT) {
            final WrapperPlayServerEntityEffect wrapper = new WrapperPlayServerEntityEffect(event);

            if(wrapper.getEntityId() != data.getPlayer().getEntityId()) return;

            data.getPledgeProcessor().confirmPost(() -> potionMap.putIfAbsent(wrapper.getPotionType(), wrapper.getEffectAmplifier() + 1));

        } else if(event.getPacketType() == PacketType.Play.Server.REMOVE_ENTITY_EFFECT) {
            final WrapperPlayServerEntityEffect wrapper = new WrapperPlayServerEntityEffect(event);

            if(wrapper.getEntityId() != data.getPlayer().getEntityId()) return;

            data.getPledgeProcessor().confirmPost(() -> potionMap.remove(wrapper.getPotionType()));
        }

    }

    public int getAmplifier(final PotionType type) {
       return potionMap.getOrDefault(type, 0);
    }

    public boolean hasEffect(final PotionType type) {
        return getAmplifier(type) != 0;
    }
}
