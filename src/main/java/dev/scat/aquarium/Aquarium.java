package dev.scat.aquarium;

import com.github.retrooper.packetevents.PacketEvents;
import dev.scat.aquarium.config.CheckConfig;
import dev.scat.aquarium.listener.BukkitListener;
import dev.scat.aquarium.listener.PacketEventsInListener;
import dev.scat.aquarium.listener.PacketEventsOutListener;
import dev.scat.aquarium.manager.PlayerDataManager;
import io.github.retrooper.packetevents.factory.spigot.SpigotPacketEventsBuilder;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public class Aquarium extends JavaPlugin {

    private static Aquarium instance;

    private final PlayerDataManager playerDataManager = new PlayerDataManager();

    private final FileConfiguration config = getConfig();
    private final CheckConfig checkConfig = new CheckConfig();

    @Override
    public void onLoad() {
        instance = this;

        PacketEvents.setAPI(SpigotPacketEventsBuilder.build(this));
        PacketEvents.getAPI().load();
    }

    @Override
    public void onEnable() {
        saveDefaultConfig();
        checkConfig.setup();

        PacketEvents.getAPI().init();

        PacketEvents.getAPI().getEventManager().registerListeners(
                new PacketEventsInListener(),
                new PacketEventsOutListener()
        );

        Bukkit.getPluginManager().registerEvents(new BukkitListener(), this);
    }

    @Override
    public void onDisable() {
        PacketEvents.getAPI().terminate();
    }

    public static Aquarium getInstance() {
        return instance;
    }
}
