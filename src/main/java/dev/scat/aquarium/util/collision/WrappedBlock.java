package dev.scat.aquarium.util.collision;

import com.github.retrooper.packetevents.protocol.world.states.defaulttags.BlockTags;
import com.github.retrooper.packetevents.protocol.world.states.type.StateType;
import lombok.Getter;
import org.bukkit.Material;

@Getter
public class WrappedBlock {

    private final StateType type;
    private final int x, y, z;

    public WrappedBlock(StateType type, int x, int y, int z) {
        this.type = type;

        this.x = x;
        this.y = y;
        this.z = z;
    }
}
