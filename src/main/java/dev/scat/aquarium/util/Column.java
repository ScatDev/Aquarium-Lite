package dev.scat.aquarium.util;

import com.github.retrooper.packetevents.protocol.world.chunk.BaseChunk;
import dev.scat.aquarium.Aquarium;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class Column {

    private final BaseChunk[] chunks;
    private final int tick = Aquarium.getInstance().getTick();
}
