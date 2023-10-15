package dev.scat.aquarium.check.impl.badpackets;

import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerDigging;
import dev.scat.aquarium.check.Check;
import dev.scat.aquarium.data.PlayerData;

/**
 * @author Salers
 * made on dev.scat.aquarium.check.impl.badpackets
 */
public class BadPacketsG extends Check {

    private boolean started = true, lastCanceled = false;

    public BadPacketsG(PlayerData data) {
        super(data, "BadPackets", "G");
    }

    @Override
    public void handle(PacketReceiveEvent event) {
       if(event.getPacketType() != PacketType.Play.Client.PLAYER_DIGGING) return;
       final WrapperPlayClientPlayerDigging wrapper = new WrapperPlayClientPlayerDigging(event);


         switch (wrapper.getAction()) {
            case START_DIGGING:
                started = true;
                lastCanceled = false;

                break;
            case CANCELLED_DIGGING:
                if (!started) flag("Cancelled a digging that never started");

                started = false;
                lastCanceled = true;

                break;
            case FINISHED_DIGGING:
                if (!started && !lastCanceled) {
                    flag("Finished digging, but didn't start or cancel");
                }

                started = lastCanceled = false;

                break;
        }

    }
}
