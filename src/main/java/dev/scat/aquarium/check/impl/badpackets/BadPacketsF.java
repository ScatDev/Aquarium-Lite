package dev.scat.aquarium.check.impl.badpackets;

import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.protocol.player.ClientVersion;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerBlockPlacement;
import dev.scat.aquarium.check.Check;
import dev.scat.aquarium.data.PlayerData;

/**
 * @author Salers
 * made on dev.scat.aquarium.check.impl.badpackets
 */
public class BadPacketsF extends Check {

    public BadPacketsF(PlayerData data) {
        super(data, "BadPackets", "F");
    }

    @Override
    public void handle(PacketReceiveEvent event) {
        if (event.getPacketType() != PacketType.Play.Client.PLAYER_BLOCK_PLACEMENT || data.getVersion().isNewerThanOrEquals(ClientVersion.V_1_9))
            return;

        final WrapperPlayClientPlayerBlockPlacement wrapper = new WrapperPlayClientPlayerBlockPlacement(event);

        final float x = wrapper.getCursorPosition().getX();
        final float y = wrapper.getCursorPosition().getY();
        final float z = wrapper.getCursorPosition().getZ();


        // the minimum value for a cursor pos is 0 and the maximum value is 1. Vanilla clients cannot breach this.

        for (float f : new float[]{x, y, z}) {
            if (f > 1f || f < 0f) flag("Invalid cursor position, (cp=" + f + ")");
        }

    }
}
