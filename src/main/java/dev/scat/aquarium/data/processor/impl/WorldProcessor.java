package dev.scat.aquarium.data.processor.impl;

import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerChunkData;
import dev.scat.aquarium.data.PlayerData;
import dev.scat.aquarium.data.processor.Processor;
import lombok.Getter;
import org.bukkit.Bukkit;

@Getter
public class WorldProcessor extends Processor {

    public WorldProcessor(PlayerData data) {
        super(data);
    }

    @Override
    public void handle(PacketSendEvent event) {
        if (event.getPacketType() == PacketType.Play.Server.CHUNK_DATA) {
            WrapperPlayServerChunkData mapChunk = new WrapperPlayServerChunkData(event);

            data.getPledgeProcessor().confirmPre(()
                    -> Bukkit.broadcastMessage("x=" + (mapChunk.getColumn().getX() << 4)
                    + " y=" + (mapChunk.getColumn().getZ() << 4)));
        }
    }

    public long toLong(int x, int z) {
        return ((x & 0xFFFFFFFFL) << 32L) | (z & 0xFFFFFFFFL);
    }
}
