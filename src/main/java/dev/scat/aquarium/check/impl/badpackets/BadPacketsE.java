package dev.scat.aquarium.check.impl.badpackets;

import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.protocol.player.ClientVersion;
import com.github.retrooper.packetevents.protocol.player.DiggingAction;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientInteractEntity;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerDigging;
import dev.scat.aquarium.check.Check;
import dev.scat.aquarium.data.PlayerData;

/**
 * @author Salers
 * made on dev.scat.aquarium.check.impl.badpackets
 */
public class BadPacketsE extends Check {

    private boolean blocking = false, attacked = false;

    public BadPacketsE(PlayerData data) {
        super(data, "BadPackets", "E");
    }

    @Override
    public void handle(PacketReceiveEvent event) {

        if (event.getPacketType() == PacketType.Play.Client.INTERACT_ENTITY) {
            final WrapperPlayClientInteractEntity wrapper = new WrapperPlayClientInteractEntity(event);
            if (wrapper.getAction() != WrapperPlayClientInteractEntity.InteractAction.ATTACK) return;

            attacked = true;

        } else if (event.getPacketType() == PacketType.Play.Client.PLAYER_BLOCK_PLACEMENT ||
                event.getPacketType() == PacketType.Play.Client.USE_ITEM) {
            blocking = true;
            attacked = false;
        } else if (event.getPacketType() == PacketType.Play.Client.PLAYER_DIGGING) {
            if (data.getVersion().isNewerThanOrEquals(ClientVersion.V_1_9)) return;
            final WrapperPlayClientPlayerDigging diggingWrapper = new WrapperPlayClientPlayerDigging(event);
            if (diggingWrapper.getAction() != DiggingAction.RELEASE_USE_ITEM) return;

            if (blocking && attacked) flag("Invalid block order on dig");

            blocking = attacked = false;

        }
    }
}
