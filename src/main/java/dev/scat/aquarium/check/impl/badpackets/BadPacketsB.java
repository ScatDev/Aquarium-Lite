package dev.scat.aquarium.check.impl.badpackets;

import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientHeldItemChange;
import dev.scat.aquarium.check.Check;
import dev.scat.aquarium.data.PlayerData;

/**
 * @author Salers
 * made on dev.scat.aquarium.check.impl.badpackets
 */
public class BadPacketsB extends Check {

    private int lastSlot = -666;

    public BadPacketsB(PlayerData data) {
        super(data, "BadPackets", "B");
    }

    @Override
    public void handle(PacketReceiveEvent event) {
        if(event.getPacketType() != PacketType.Play.Client.HELD_ITEM_CHANGE) return;
        final WrapperPlayClientHeldItemChange wrapper = new WrapperPlayClientHeldItemChange(event);

        if(wrapper.getSlot() == lastSlot) {
            if(++buffer > 5)
                flag("Same slot, (slot=" + wrapper.getSlot() + ")");
        } else buffer = Math.max(0, buffer - 1E-2);

        lastSlot = wrapper.getSlot();
    }
}
