package dev.scat.aquarium.manager;

import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.protocol.player.User;
import dev.scat.aquarium.data.PlayerData;
import lombok.Getter;
import org.bukkit.entity.Player;

import java.util.*;

public class PlayerDataManager {

    private Map<UUID, PlayerData> dataMap = new HashMap<>();

    @Getter
    private List<Player> alertingPlayers = new ArrayList<>();

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

    public void clear() {
        dataMap.clear();
    }
}
