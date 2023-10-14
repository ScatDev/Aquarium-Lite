package dev.scat.aquarium.data.processor.impl;

import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientEntityAction;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerBlockPlacement;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerDigging;
import dev.scat.aquarium.data.PlayerData;
import dev.scat.aquarium.data.processor.Processor;
import dev.scat.aquarium.util.PacketUtil;
import lombok.Getter;

/**
 * @author Salers
 * made on dev.scat.aquarium.data.processor.impl
 */

@Getter
public class ActionProcessor extends Processor {

    private boolean digging, sentSprint, sentSneak, sprinting, sneaking;
    private int placeTicks;

    public ActionProcessor(PlayerData data) {
        super(data);
    }

    @Override
    public void handlePre(PacketReceiveEvent event) {
        if(event.getPacketType() == PacketType.Play.Client.PLAYER_DIGGING) {
            final WrapperPlayClientPlayerDigging wrapper = new WrapperPlayClientPlayerDigging(event);

            switch (wrapper.getAction()) {
                case START_DIGGING:
                    digging = true;
                    break;
                case CANCELLED_DIGGING:
                case FINISHED_DIGGING:
                    digging = false;
                    break;
            }

        } else if(event.getPacketType() == PacketType.Play.Client.PLAYER_BLOCK_PLACEMENT) {
            final WrapperPlayClientPlayerBlockPlacement wrapper = new WrapperPlayClientPlayerBlockPlacement(event);

            placeTicks = 0;

        } else if(event.getPacketType() == PacketType.Play.Client.ENTITY_ACTION) {
            final WrapperPlayClientEntityAction wrapper = new WrapperPlayClientEntityAction(event);
            switch (wrapper.getAction()) {
                case START_SPRINTING:
                    sprinting = true;
                    sentSprint = true;
                    break;
                case STOP_SPRINTING:
                    sprinting = false;
                    sentSprint = true;
                    break;
                case START_SNEAKING:
                    sneaking = true;
                    sentSneak = true;
                    break;
                case STOP_SNEAKING:
                    sneaking = false;
                    sentSneak = true;
                    break;
            }
        } else if(PacketUtil.isFlying(event.getPacketType())) {
            placeTicks++;
            sentSprint = sentSneak = false;
        }
    }
}
