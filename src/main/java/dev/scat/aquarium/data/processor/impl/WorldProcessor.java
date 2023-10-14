package dev.scat.aquarium.data.processor.impl;

import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.protocol.world.BlockFace;
import com.github.retrooper.packetevents.protocol.world.chunk.BaseChunk;
import com.github.retrooper.packetevents.protocol.world.chunk.impl.v1_8.Chunk_v1_8;
import com.github.retrooper.packetevents.protocol.world.states.WrappedBlockState;
import com.github.retrooper.packetevents.util.Vector3i;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerBlockPlacement;
import com.github.retrooper.packetevents.wrapper.play.server.*;
import dev.scat.aquarium.data.PlayerData;
import dev.scat.aquarium.data.processor.Processor;
import dev.scat.aquarium.util.mc.MathHelper;
import lombok.Getter;
import org.bukkit.Bukkit;

import java.util.HashMap;
import java.util.Map;

@Getter
public class WorldProcessor extends Processor {

    private final Map<Long, BaseChunk[]> chunks = new HashMap<>();

    public WorldProcessor(PlayerData data) {
        super(data);
    }

    @Override
    public void handlePre(PacketSendEvent event) {
        if (event.getPacketType() == PacketType.Play.Server.CHUNK_DATA) {
            WrapperPlayServerChunkData mapChunk = new WrapperPlayServerChunkData(event);

            data.getTransactionProcessor().confirmPre(() -> {
                long xz = toLong(mapChunk.getColumn().getX(), mapChunk.getColumn().getZ());

                if (mapChunk.getColumn().isFullChunk()) {
                    chunks.put(xz, mapChunk.getColumn().getChunks());
                } else {
                    if (chunks.containsKey(xz)) {
                        BaseChunk[] currentChunks = chunks.get(xz);
                        for (int i = 0; i < 15; i++) {
                            BaseChunk chunk = mapChunk.getColumn().getChunks()[i];
                            if (chunk != null) {
                                currentChunks[i] = chunk;
                            }
                        }
                        chunks.put(xz, currentChunks);
                    }
                }
            });
        } else if (event.getPacketType() == PacketType.Play.Server.MAP_CHUNK_BULK) {
            WrapperPlayServerChunkDataBulk mapChunkBulk = new WrapperPlayServerChunkDataBulk(event);

            data.getTransactionProcessor().confirmPre(() -> {
                for (int i = 0; i < mapChunkBulk.getX().length; i++) {
                    int x = mapChunkBulk.getX()[i];
                    int z = mapChunkBulk.getZ()[i];
                    long xz = toLong(x, z);

                    chunks.put(xz, mapChunkBulk.getChunks()[i]);
                }
            });
        } else if (event.getPacketType() == PacketType.Play.Server.UNLOAD_CHUNK) {
            WrapperPlayServerUnloadChunk unloadChunk = new WrapperPlayServerUnloadChunk(event);

            long xz = toLong(unloadChunk.getChunkX(), unloadChunk.getChunkZ());

            data.getTransactionProcessor().confirmPost(() -> {
                chunks.remove(xz);
            });
        } else if (event.getPacketType() == PacketType.Play.Server.BLOCK_CHANGE) {
            WrapperPlayServerBlockChange blockChange = new WrapperPlayServerBlockChange(event);

            int x = blockChange.getBlockPosition().getX();
            int y = blockChange.getBlockPosition().getY();
            int z = blockChange.getBlockPosition().getZ();
            long xz = toLong(x >> 4, z >> 4);

            data.getTransactionProcessor().confirmPre(() -> {
                if (!chunks.containsKey(xz)) {
                    chunks.put(xz, new BaseChunk[16]);
                }

                int length = chunks.get(xz).length;

                if (length <= (y >> 4))
                    return;

                BaseChunk chunk = chunks.get(xz)[y >> 4];

                if (chunk == null) {
                    chunk = create();
                    chunk.set(0, 0, 0, 0);
                    chunks.get(xz)[y >> 4] = chunk;
                }

                chunks.get(xz)[y >> 4].set(x & 15, y & 15, z & 15, blockChange.getBlockId());
            });
        } else if (event.getPacketType() == PacketType.Play.Server.MULTI_BLOCK_CHANGE) {
            WrapperPlayServerMultiBlockChange multiBlockChange = new WrapperPlayServerMultiBlockChange(event);

            data.getTransactionProcessor().confirmPre(() -> {
                for (WrapperPlayServerMultiBlockChange.EncodedBlock block : multiBlockChange.getBlocks()) {
                    int x = block.getX();
                    int y = block.getY();
                    int z = block.getZ();

                    long xz = toLong(x >> 4, z >> 4);

                    if (!chunks.containsKey(xz)) {
                        chunks.put(xz, new BaseChunk[16]);
                    }

                    int length = chunks.get(xz).length;

                    if (length <= (y >> 4))
                        continue;

                    BaseChunk chunk = chunks.get(xz)[y >> 4];

                    if (chunk == null) {
                        chunk = create();
                        chunk.set(0, 0, 0, 0);
                        chunks.get(xz)[y >> 4] = chunk;
                    }

                    chunks.get(xz)[y >> 4]
                            .set(x & 15, y & 15, z & 15, block.getBlockId());
                }
            });
        }
    }

    public void handlePre(PacketReceiveEvent event) {
        if (event.getPacketType() == PacketType.Play.Client.PLAYER_BLOCK_PLACEMENT) {
            WrapperPlayClientPlayerBlockPlacement blockPlace = new WrapperPlayClientPlayerBlockPlacement(event);

            Vector3i pos = shift(blockPlace.getBlockPosition(), blockPlace.getFace());

            long xz = toLong(pos.getX() >> 4, pos.getZ() >> 4);

            boolean block = blockPlace.getItemStack().isPresent()
                    && blockPlace.getItemStack().get().getType().getPlacedType() != null;

            if (block && chunks.containsKey(xz)) {
                int length = chunks.get(xz).length;

                if (length <= (pos.getY() >> 4))
                    return;

                BaseChunk chunk = chunks.get(xz)[pos.getY() >> 4];

                if (chunk == null) {
                    chunk = create();
                    chunk.set(0, 0, 0, 0);
                    chunks.get(xz)[pos.getY() >> 4] = chunk;
                }

                chunks.get(xz)[pos.getY() >> 4].set(pos.getX() & 15, pos.getY() & 15, pos.getZ() & 15,
                        blockPlace.getItemStack().get().getType().getPlacedType()
                                .createBlockState(data.getVersion()).getGlobalId());
            }
        }
    }

    public long toLong(int x, int z) {
        return ((x & 0xFFFFFFFFL) << 32L) | (z & 0xFFFFFFFFL);
    }

    public WrappedBlockState getBlock(int x, int y, int z) {
        long xz = toLong(x >> 4, z >> 4);

        if (chunks.containsKey(xz)) {
            BaseChunk[] baseChunks = chunks.get(xz);

            if (y < 0 || (y >> 4) > baseChunks.length || baseChunks[y >> 4] == null)
                if (y < 0 || (y >> 4) >= baseChunks.length || baseChunks[y >> 4] == null)
                    return WrappedBlockState.getByGlobalId(0);

            return baseChunks[y >> 4].get(data.getVersion(),x & 15, y & 15, z & 15);
        }

        return WrappedBlockState.getByGlobalId(0);
    }

    public WrappedBlockState getBlock(double x, double y, double z) {
        return getBlock(MathHelper.floor_double(x), MathHelper.floor_double(y), MathHelper.floor_double(z));
    }

    public boolean isChunkLoaded(int x, int z) {
        long xz = toLong(x >> 4, z >> 4);

        if (chunks.containsKey(xz)) {
            return chunks.get(xz) != null;
        } else {
            return false;
        }
    }

    public boolean isChunkLoaded(double x, double z) {
        long xz = toLong((int) x >> 4, (int) z >> 4);

        if (chunks.containsKey(xz)) {
            return chunks.get(xz) != null;
        } else {
            return false;
        }
    }

    private static BaseChunk create() {
        return new Chunk_v1_8(false);
    }

    public Vector3i shift(Vector3i pos, BlockFace facing) {
        switch(facing) {
            case UP:
                return new Vector3i(pos.getX(), pos.getY() + 1, pos.getZ());
            case DOWN:
                return new Vector3i(pos.getX(), pos.getY() - 1, pos.getZ());
            case NORTH:
                return new Vector3i(pos.getX(), pos.getY(), pos.getZ() - 1);
            case SOUTH:
                return new Vector3i(pos.getX(), pos.getY(), pos.getZ() + 1);
            case WEST:
                return new Vector3i(pos.getX() - 1, pos.getY(), pos.getZ());
            case EAST:
                return new Vector3i(pos.getX() + 1, pos.getY(), pos.getZ());
            default:
                // TODO: fix this shit
                return pos;
        }
    }
}