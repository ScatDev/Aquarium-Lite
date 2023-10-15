package dev.scat.aquarium.check.impl.badpackets;

import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.protocol.player.DiggingAction;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerDigging;
import dev.scat.aquarium.check.Check;
import dev.scat.aquarium.data.PlayerData;



/**
 * @author Salers
 * made on dev.scat.aquarium.check.impl.autoclicker
 */
public class BadPacketsD extends Check {

    private boolean placing = false;

    public BadPacketsD(PlayerData data) {
        super(data, "BadPackets", "D");
    }

    @Override
    public void handle(PacketReceiveEvent event) {
        if (event.getPacketType() == PacketType.Play.Client.PLAYER_BLOCK_PLACEMENT) {
            placing = true;
        } else if (event.getPacketType() == PacketType.Play.Client.PLAYER_DIGGING) {
           final WrapperPlayClientPlayerDigging wrapper = new WrapperPlayClientPlayerDigging(event);
            if (wrapper.getAction() == DiggingAction.RELEASE_USE_ITEM) {
                if (!placing) flag("Invalid Place Order");

                placing = false;
            }
        }
    }
}
