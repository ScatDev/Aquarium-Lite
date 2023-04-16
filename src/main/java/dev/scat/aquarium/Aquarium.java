package dev.scat.aquarium;

import com.github.retrooper.packetevents.PacketEvents;
import dev.scat.aquarium.command.AlertsCommand;
import dev.scat.aquarium.config.CheckConfig;
import dev.scat.aquarium.config.Config;
import dev.scat.aquarium.listener.BukkitListener;
import dev.scat.aquarium.listener.PacketEventsInListener;
import dev.scat.aquarium.listener.PacketEventsOutListener;
import dev.scat.aquarium.listener.PledgeListener;
import dev.scat.aquarium.manager.CheckManager;
import dev.scat.aquarium.manager.PlayerDataManager;
import dev.thomazz.pledge.api.Pledge;
import io.github.retrooper.packetevents.factory.spigot.SpigotPacketEventsBuilder;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

@Getter
public class Aquarium extends JavaPlugin {

    private static Aquarium instance;

    private Pledge pledge;

    private final PlayerDataManager playerDataManager = new PlayerDataManager();
    private final CheckManager checkManager = new CheckManager();

    private final CheckConfig checkConfig = new CheckConfig();

    private int tick;

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

        pledge = Pledge.build().start(this);

        PacketEvents.getAPI().init();

        PacketEvents.getAPI().getEventManager().registerListeners(
                new PacketEventsInListener(),
                new PacketEventsOutListener()
        );

        getCommand("alerts").setExecutor(new AlertsCommand());

        Bukkit.getPluginManager().registerEvents(new BukkitListener(), this);
        Bukkit.getPluginManager().registerEvents(new PledgeListener(), this);

        Bukkit.getScheduler().runTaskTimer(this, () -> {
            ++tick;
        }, 0L, 1L);
    }

    @Override
    public void onDisable() {
        PacketEvents.getAPI().terminate();
    }

    public static Aquarium getInstance() {
        return instance;
    }
}