package dev.scat.aquarium.data.processor.impl;

import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.protocol.potion.PotionType;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityEffect;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerRemoveEntityEffect;
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
            WrapperPlayServerEntityEffect wrapper = new WrapperPlayServerEntityEffect(event);

            if (wrapper.getEntityId() != data.getPlayer().getEntityId()) return;

            data.getTransactionProcessor().confirmPre(() ->
                potionMap.put(wrapper.getPotionType(), wrapper.getEffectAmplifier())
            );
        } else if(event.getPacketType() == PacketType.Play.Server.REMOVE_ENTITY_EFFECT) {
            WrapperPlayServerRemoveEntityEffect wrapper = new WrapperPlayServerRemoveEntityEffect(event);

            if (wrapper.getEntityId() != data.getPlayer().getEntityId()) return;

            data.getTransactionProcessor().confirmPost(()
                    -> potionMap.remove(wrapper.getPotionType()));
        }

    }

    public int getAmplifier(PotionType type) {
       return potionMap.getOrDefault(type, -1);
    }

    public boolean hasEffect(PotionType type) {
        return potionMap.containsKey(type);
    }
}
