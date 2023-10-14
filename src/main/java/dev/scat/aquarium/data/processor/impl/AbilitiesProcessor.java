package dev.scat.aquarium.data.processor.impl;

import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerPlayerAbilities;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerUpdateAttributes;
import dev.scat.aquarium.data.PlayerData;
import dev.scat.aquarium.data.processor.Processor;
import dev.scat.aquarium.util.PacketUtil;
import dev.scat.aquarium.util.lag.ConfirmedAbilities;
import lombok.Getter;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

@Getter
public class AbilitiesProcessor extends Processor {

    // Stole some movementspeed stuff fomr incognito but is cool
    private static final UUID SPRINTING_MODIFIER_UUID = UUID.fromString("662A6B8D-DA3E-4C1C-8813-96EA6097278D");

    private ConfirmedAbilities abilities = new ConfirmedAbilities();
    private ConfirmedAbilities lastAbilities = new ConfirmedAbilities();

    private double movementSpeed = 0.10000000149011612D;

    public AbilitiesProcessor(PlayerData data) {
        super(data);
    }

    @Override
    public void handlePre(PacketSendEvent event) {
        if (event.getPacketType() == PacketType.Play.Server.PLAYER_ABILITIES) {
            final WrapperPlayServerPlayerAbilities wrapper = new WrapperPlayServerPlayerAbilities(event);

            data.getTransactionProcessor().confirmPre(() -> abilities = new ConfirmedAbilities(
                    wrapper.isInGodMode(),
                    wrapper.isFlying(),
                    wrapper.isFlightAllowed(),
                    wrapper.isInCreativeMode(),
                    wrapper.getFOVModifier(),
                    wrapper.getFlySpeed()
            ));
        } else if (event.getPacketType() == PacketType.Play.Server.UPDATE_ATTRIBUTES) {
            WrapperPlayServerUpdateAttributes updateAttributes = new WrapperPlayServerUpdateAttributes(event);

            // Thanks incognito for giving me this without me asking <3
            for (WrapperPlayServerUpdateAttributes.Property property : updateAttributes.getProperties()) {
                if (property.getKey().equals("generic.movementSpeed")) {
                    movementSpeed = property.getValue();
                }
            }
        }
    }

    @Override
    public void handlePre(PacketReceiveEvent event) {
        if (PacketUtil.isFlying(event.getPacketType())) {
            lastAbilities = abilities.clone();
        }
    }
}
