package dev.scat.aquarium.listener;

import dev.scat.aquarium.Aquarium;
import dev.scat.aquarium.data.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class BukkitListener implements Listener {

    @EventHandler()
    public void onJoin(PlayerJoinEvent event) {
        Aquarium.getInstance().getPlayerDataManager().add(event.getPlayer());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Aquarium.getInstance().getPlayerDataManager().remove(event.getPlayer().getUniqueId());

        Aquarium.getInstance().getPlayerDataManager().getAlertingPlayers().remove(event.getPlayer());
    }
}
