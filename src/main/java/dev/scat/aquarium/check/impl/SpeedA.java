package dev.scat.aquarium.check.impl;

import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.world.states.WrappedBlockState;
import dev.scat.aquarium.check.Check;
import dev.scat.aquarium.data.PlayerData;
import dev.scat.aquarium.util.PacketUtil;
import org.bukkit.Bukkit;
import org.bukkit.util.NumberConversions;

public class SpeedA extends Check {

    public SpeedA(PlayerData data) {
        super(data, "Speed", "A");
    }

    @Override
    public void handle(PacketReceiveEvent event) {
        if (PacketUtil.isPosition(event.getPacketType())) {
//            int x = NumberConversions.floor(data.getPositionProcessor().getX());
//            int y = NumberConversions.floor(data.getPositionProcessor().getY()) - 1;
//            int z = NumberConversions.floor(data.getPositionProcessor().getZ());
//
//            WrappedBlockState block = data.getWorldProcessor().getBlock(x, y, z);
//
//            Bukkit.broadcastMessage(block.getType().getName());
        }
    }
}
