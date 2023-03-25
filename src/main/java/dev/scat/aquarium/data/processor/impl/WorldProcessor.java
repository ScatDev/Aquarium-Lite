package dev.scat.aquarium.data.processor.impl;

import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.protocol.world.chunk.BaseChunk;
import com.github.retrooper.packetevents.protocol.world.states.WrappedBlockState;
import com.github.retrooper.packetevents.wrapper.play.server.*;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import dev.scat.aquarium.data.PlayerData;
import dev.scat.aquarium.data.processor.Processor;
import lombok.Getter;
import org.bukkit.Bukkit;

import java.util.*;

@Getter
public class WorldProcessor extends Processor {

    // Not perfect, doesn't handle trans split and another rare issue ;)
    // Still better than 90% of anticheats and most paid anticheats

    private final Map<Long, BaseChunk[]> possibleChunks = new HashMap<>();

    public WorldProcessor(PlayerData data) {
        super(data);
    }

    @Override
    public void handle(PacketSendEvent event) {
        if (event.getPacketType() == PacketType.Play.Server.CHUNK_DATA) {
            WrapperPlayServerChunkData mapChunk = new WrapperPlayServerChunkData(event);

            Bukkit.broadcastMessage("chunk x=" + (mapChunk.getColumn().getX() << 4) + " z=" + (mapChunk.getColumn().getZ() << 4));

            data.getPledgeProcessor().confirmPre(() -> {
                long xz = toLong(mapChunk.getColumn().getX() << 4, mapChunk.getColumn().getZ() << 4);

                possibleChunks.put(xz, mapChunk.getColumn().getChunks());
            });
        } else if (event.getPacketType() == PacketType.Play.Server.MAP_CHUNK_BULK) {
            WrapperPlayServerChunkDataBulk mapChunkBulk = new WrapperPlayServerChunkDataBulk(event);

            data.getPledgeProcessor().confirmPre(() -> {
                for (int i = 0; i < mapChunkBulk.getX().length; i++) {
                    int x = mapChunkBulk.getX()[i];
                    int z = mapChunkBulk.getZ()[i];

                    long xz = toLong(x, z);

                    possibleChunks.put(xz, mapChunkBulk.getChunks()[i]);
                }
            });
        } else if (event.getPacketType() == PacketType.Play.Server.UNLOAD_CHUNK) {
            WrapperPlayServerUnloadChunk unloadChunk = new WrapperPlayServerUnloadChunk(event);

            long xz = toLong(unloadChunk.getChunkX(), unloadChunk.getChunkZ());

            data.getPledgeProcessor().confirmPost(() -> {
                possibleChunks.remove(xz);
            });
        } else if (event.getPacketType() == PacketType.Play.Server.BLOCK_CHANGE) {
            WrapperPlayServerBlockChange blockChange = new WrapperPlayServerBlockChange(event);

            long xz = toLong(blockChange.getBlockPosition().getZ(), blockChange.getBlockPosition().getZ());

            int x = ((blockChange.getBlockPosition().getX() % 16) * 16;
            int y = ((blockChange.getBlockPosition().getY() % 16) * 16;
            int z = ((blockChange.getBlockPosition().getZ() % 16) * 16;
            int dividedY = (int) Math.floor(blockChange.getBlockPosition().getY() / 16D);

            data.getPledgeProcessor().confirmPre(() -> {
                if (possibleChunks.containsKey(xz)) {
                    possibleChunks.get(xz)[dividedY].set(x, y, z, blockChange.getBlockId());
                }
            });
        } else if (event.getPacketType() == PacketType.Play.Server.MULTI_BLOCK_CHANGE) {
            WrapperPlayServerMultiBlockChange multiBlockChange = new WrapperPlayServerMultiBlockChange(event);

        }
    }

    public long toLong(int x, int z) {
        return ((x & 0xFFFFFFFFL) << 32L) | (z & 0xFFFFFFFFL);
    }

    // Probably not the most efficient but should work so who cares for open source really
    public List<WrappedBlockState> getBlock(int x, int y, int z) {
        List<WrappedBlockState> possibleBlocks = new ArrayList<>();

        long xz = toLong(x, z);

        int chunkX = ((x % 16) * 16);
        int chunkZ = ((z % 16) * 16);

        if (possibleChunks.containsKey(xz)) {
            for (ChunkColumn column : possibleChunks.get(xz)) {
                possibleBlocks.add(column.getBlock(chunkX, y, chunkZ));
            }
        }

        possibleBlocks.removeIf(Objects::isNull);

        return possibleBlocks;
    }
}
