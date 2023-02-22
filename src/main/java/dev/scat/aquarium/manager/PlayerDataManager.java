package dev.scat.aquarium.manager;

import com.github.retrooper.packetevents.protocol.packettype.PacketType;
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

    public void remove(Player player) {
        dataMap.remove(player.getUniqueId());
    }

    public PlayerData get(Player player) {
        return dataMap.get(player.getUniqueId());
    }

    public Collection<PlayerData> getValues() {
        return dataMap.values();
    }
}
