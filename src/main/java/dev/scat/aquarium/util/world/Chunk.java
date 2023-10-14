package dev.scat.aquarium.util.world;

import com.github.retrooper.packetevents.protocol.world.chunk.BaseChunk;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class Chunk {
    private BaseChunk[] baseChunks;
    private boolean confirming;
}
