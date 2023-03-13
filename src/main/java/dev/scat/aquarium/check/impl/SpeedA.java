package dev.scat.aquarium.check.impl;

import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import dev.scat.aquarium.check.Check;
import dev.scat.aquarium.data.PlayerData;
import dev.scat.aquarium.util.PacketUtil;
import org.bukkit.Bukkit;

public class SpeedA extends Check {

    public SpeedA(PlayerData data) {
        super(data, "Speed", "A");
    }

    @Override
    public void handle(PacketReceiveEvent event) {
        if (PacketUtil.isPosition(event.getPacketType())) {
            double deltaXZ = data.getPositionProcessor().getDeltaXZ();

            if (deltaXZ > 0.3) {
                flag("dxz=" + deltaXZ);
            }
        }
    }
}
