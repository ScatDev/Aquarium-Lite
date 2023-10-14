package dev.scat.aquarium.util.collision;

import com.github.retrooper.packetevents.protocol.world.states.WrappedBlockState;
import com.github.retrooper.packetevents.protocol.world.states.defaulttags.BlockTags;
import com.github.retrooper.packetevents.protocol.world.states.type.StateType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.List;

@Getter
@RequiredArgsConstructor
public class WrappedBlock {

    private final WrappedBlockState block;
    private final CollisionBox collisionBox;
    private final int x, y, z;
}
