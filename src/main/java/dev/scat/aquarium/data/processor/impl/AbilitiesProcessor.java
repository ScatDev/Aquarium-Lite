package dev.scat.aquarium.data.processor.impl;

import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerPlayerAbilities;
import dev.scat.aquarium.data.PlayerData;
import dev.scat.aquarium.data.processor.Processor;
import dev.scat.aquarium.util.lag.ConfirmedAbilities;
import lombok.Getter;

@Getter
public class AbilitiesProcessor extends Processor {

    /**
     * Default abilities
     * @see dev.scat.aquarium.util.lag.ConfirmedAbilities
     */
    private ConfirmedAbilities abilities = new ConfirmedAbilities();

    public AbilitiesProcessor(PlayerData data) {
        super(data);
    }

    @Override
    public void handlePre(PacketSendEvent event) {
        if(event.getPacketType() == PacketType.Play.Server.PLAYER_ABILITIES) {
            final WrapperPlayServerPlayerAbilities wrapper = new WrapperPlayServerPlayerAbilities(event);

            data.getPledgeProcessor().confirmPost(() -> abilities = new ConfirmedAbilities(
                    wrapper.isInGodMode(),
                    wrapper.isFlying(),
                    wrapper.isFlightAllowed(),
                    wrapper.isInCreativeMode(),
                    wrapper.getFOVModifier(),
                    wrapper.getFlySpeed()
            ));

        }
    }
}
