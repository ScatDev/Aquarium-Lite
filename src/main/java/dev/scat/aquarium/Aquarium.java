package dev.scat.aquarium;

import com.github.retrooper.packetevents.PacketEvents;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import dev.scat.aquarium.command.AlertsCommand;
import dev.scat.aquarium.command.AqDebugCommand;
import dev.scat.aquarium.command.DeleteLogsCommand;
import dev.scat.aquarium.command.LogsCommand;
import dev.scat.aquarium.config.CheckConfig;
import dev.scat.aquarium.config.Config;
import dev.scat.aquarium.listener.BukkitListener;
import dev.scat.aquarium.listener.PacketEventsInListener;
import dev.scat.aquarium.listener.PacketEventsOutListener;
import dev.scat.aquarium.listener.PledgeListener;
import dev.scat.aquarium.manager.*;
import dev.thomazz.pledge.api.Pledge;
import io.github.retrooper.packetevents.factory.spigot.SpigotPacketEventsBuilder;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Getter
public class Aquarium extends JavaPlugin {

    @Getter
    private static Aquarium instance;

    private Pledge pledge;

    private final PlayerDataManager playerDataManager = new PlayerDataManager();
    private final CheckManager checkManager = new CheckManager();
    private final DatabaseManager databaseManager = new DatabaseManager();

    private final CheckConfig checkConfig = new CheckConfig();

    private final ExecutorService executorService = Executors.newSingleThreadExecutor(
            new ThreadFactoryBuilder()
                    .setPriority(3)
                    .setNameFormat("Aquarium Log Thread")
                    .build()
    );

    private int tick;

    @Override
    public void onLoad() {
        instance = this;

        PacketEvents.setAPI(SpigotPacketEventsBuilder.build(this));
        PacketEvents.getAPI().getSettings().bStats(true).checkForUpdates(false);
        PacketEvents.getAPI().load();
    }

    @Override
    public void onEnable() {
        saveDefaultConfig();
        checkConfig.setup();

        databaseManager.setup();

        pledge = Pledge.build().setRange(-13000, -15000).start(this);

        PacketEvents.getAPI().init();

        PacketEvents.getAPI().getEventManager().registerListeners(
                new PacketEventsInListener(),
                new PacketEventsOutListener()
        );

        getCommand("alerts").setExecutor(new AlertsCommand());
        getCommand("logs").setExecutor(new LogsCommand());
        getCommand("deletelogs").setExecutor(new DeleteLogsCommand());
        getCommand("aqdebug").setExecutor(new AqDebugCommand());

        Bukkit.getPluginManager().registerEvents(new PledgeListener(), this);
        Bukkit.getPluginManager().registerEvents(new BukkitListener(), this);

        Bukkit.getScheduler().runTaskTimer(this, () -> {
            ++tick;

            executorService.execute(databaseManager::run);

            if (tick % ((int) Config.VL_RESET_DELAY.getValue() * 20) == 0) {
                playerDataManager.getValues().forEach(data
                        -> data.getChecks().forEach(check -> check.setVl(0)));
            }
        }, 0L, 1L);

        Bukkit.getOnlinePlayers().forEach(playerDataManager::add);
        Bukkit.getOnlinePlayers().stream().filter(player
                -> player.hasPermission("aquarium.alerts")).forEach(player
                -> playerDataManager.getAlertingPlayers().add(player));
    }

    @Override
    public void onDisable() {
        playerDataManager.getAlertingPlayers().clear();
        playerDataManager.clear();

        PacketEvents.getAPI().terminate();
        this.pledge = null;

        tick = 0;
    }
}