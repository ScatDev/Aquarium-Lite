package dev.scat.aquarium.util.collision;

import com.github.retrooper.packetevents.protocol.player.ClientVersion;
import com.github.retrooper.packetevents.protocol.world.states.WrappedBlockState;
import dev.scat.aquarium.data.PlayerData;

public interface CollisionDataBuilder {

    CollisionBox fetch(PlayerData data, ClientVersion version, WrappedBlockState block, int x, int y, int z);
}
