package dev.scat.aquarium.check.impl.badpackets;

import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import dev.scat.aquarium.check.Check;
import dev.scat.aquarium.data.PlayerData;
import dev.scat.aquarium.util.PacketUtil;

/**
 * @author Salers
 * made on dev.scat.aquarium.check.impl.badpackets
 */
public class BadPacketsC extends Check {

    public BadPacketsC(PlayerData data) {
        super(data, "BadPackets", "C");
    }

    @Override
    public void handle(PacketReceiveEvent event) {
        if(!PacketUtil.isFlying(event.getPacketType())) return;

        if(Math.abs(data.getRotationProcessor().getPitch()) > 90.F)
            flag("Invalid pitch, (pitch=" + data.getRotationProcessor().getPitch() + ")");
    }
}
