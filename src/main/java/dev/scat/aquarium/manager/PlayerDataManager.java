package dev.scat.aquarium.manager;

import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.protocol.player.User;
import dev.scat.aquarium.data.PlayerData;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerDataManager {

    private Map<UUID, PlayerData> dataMap = new HashMap<>();

    public void add(Player player) {
        dataMap.put(player.getUniqueId(), new PlayerData(player));
    }

    public void remove(UUID uuid) {
        dataMap.remove(uuid);
    }

    public PlayerData get(UUID uuid) {
        return dataMap.get(uuid);
    }

    public Collection<PlayerData> getValues() {
        return dataMap.values();
    }
}
