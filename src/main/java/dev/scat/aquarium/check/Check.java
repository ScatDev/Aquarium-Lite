package dev.scat.aquarium.check;

import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import dev.scat.aquarium.Aquarium;
import dev.scat.aquarium.config.Config;
import dev.scat.aquarium.data.PlayerData;
import dev.scat.aquarium.database.Log;
import dev.scat.aquarium.util.MathUtil;
import dev.scat.aquarium.util.mc.MathHelper;
import dev.thomazz.pledge.api.event.PacketFrameReceiveEvent;
import lombok.Getter;
import lombok.Setter;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;

import java.util.Objects;

@Getter
@Setter
public abstract class Check {

    protected final PlayerData data;
    private final String type, name;
    private boolean enabled, punish, setback;
    private double maxVl, vl;
    private String punishCommand, note;
    protected double buffer;

    public Check(PlayerData data, String type, String name) {
        this.data = data;
        this.type = type;
        this.name = name;
        this.setback = false;

        enabled = Aquarium.getInstance().getCheckConfig().isEnabled(type, name);
        punish = Aquarium.getInstance().getCheckConfig().isPunishable(type, name);
        maxVl = Aquarium.getInstance().getCheckConfig().getMaxVl(type, name);
        punishCommand = Aquarium.getInstance().getCheckConfig().getPunishCommand(type, name)
                .replaceAll("%player%", data.getPlayer().getName());
    }

    public Check(PlayerData data, String type, String name, boolean setback) {
        this.data = data;
        this.type = type;
        this.name = name;
        this.setback = setback;

        enabled = Aquarium.getInstance().getCheckConfig().isEnabled(type, name);
        punish = Aquarium.getInstance().getCheckConfig().isPunishable(type, name);
        maxVl = Aquarium.getInstance().getCheckConfig().getMaxVl(type, name);
        punishCommand = Aquarium.getInstance().getCheckConfig().getPunishCommand(type, name)
                .replaceAll("%player%", data.getPlayer().getName());

        if (setback)
            punish = false;
    }

    public Check(PlayerData data, String type, String name, String note) {
        this.data = data;
        this.type = type;
        this.name = name;
        this.note = note;

        enabled = Aquarium.getInstance().getCheckConfig().isEnabled(type, name);
        punish = Aquarium.getInstance().getCheckConfig().isPunishable(type, name);
        maxVl = Aquarium.getInstance().getCheckConfig().getMaxVl(type, name);
        punishCommand = Aquarium.getInstance().getCheckConfig().getPunishCommand(type, name)
                .replaceAll("%player%", data.getPlayer().getName());
    }

    public Check(PlayerData data, String type, String name, String note, boolean setback) {
        this.data = data;
        this.type = type;
        this.name = name;
        this.note = note;
        this.setback = setback;

        enabled = Aquarium.getInstance().getCheckConfig().isEnabled(type, name);
        punish = Aquarium.getInstance().getCheckConfig().isPunishable(type, name);
        maxVl = Aquarium.getInstance().getCheckConfig().getMaxVl(type, name);
        punishCommand = Aquarium.getInstance().getCheckConfig().getPunishCommand(type, name)
                .replaceAll("%player%", data.getPlayer().getName());

        if (setback)
            punish = false;
    }

    public void handle(PacketReceiveEvent event) {
    }

    public void handle(PacketSendEvent event) {
    }

    public void handle(PacketFrameReceiveEvent event) {
    }

    public void flag(double vl, String info) {
        vl += vl;

        TextComponent alert = new TextComponent();

        alert.setText(
                Config.ALERT_MESSAGE.translate()
                        .replaceAll("%player%", data.getPlayer().getName())
                        .replaceAll("%type%", type)
                        .replaceAll("%name%", name)
                        .replaceAll("%vl%", String.valueOf(MathUtil.round(vl, 2)))
        );

        alert.setHoverEvent(
                new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                        new ComponentBuilder(
                                note != null ?
                                        Config.HOVER_MESSAGE_NOTE.translate()
                                                .replaceAll("%note%", note)
                                                .replaceAll("%info%", info)
                                        : Config.HOVER_MESSAGE.translate()
                                        .replaceAll("%info%", info)
                        ).create()
                )
        );

        alert.setClickEvent(
                new ClickEvent(
                        ClickEvent.Action.RUN_COMMAND,
                        "/tp " + data.getPlayer().getName()
                )
        );

        Aquarium.getInstance().getExecutorService().execute(()
                -> Aquarium.getInstance().getPlayerDataManager().getAlertingPlayers()
                .forEach(data -> Objects.requireNonNull(data.getPlayer())
                        .spigot().sendMessage(alert)));

        Aquarium.getInstance().getDatabaseManager().addLog(
                new Log(data.getPlayer().getUniqueId(),
                        System.currentTimeMillis(),
                        type, name, info, vl));

        if (setback)
            data.getSetbackProcessor().setback();

        if (vl >= maxVl && punish && !data.isPunishing()
                && !(data.getPlayer().hasPermission("aquarium.bypass")
                && (boolean) Config.BYPASS_PUNISHMENT.getValue())) {
            data.setPunishing(true);

            Bukkit.getScheduler().runTask(Aquarium.getInstance(),
                    () -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), punishCommand));

            Aquarium.getInstance().getDatabaseManager().addLog(
                    new Log(data.getPlayer().getUniqueId(),
                            System.currentTimeMillis(),
                            type, name, "Punished", vl));
        }
    }


    public void flag(String info) {
        ++vl;

        TextComponent alert = new TextComponent();

        alert.setText(
                Config.ALERT_MESSAGE.translate()
                        .replaceAll("%player%", data.getPlayer().getName())
                        .replaceAll("%type%", type)
                        .replaceAll("%name%", name)
                        .replaceAll("%vl%", String.valueOf(vl))
        );

        alert.setHoverEvent(
                new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                        new ComponentBuilder(
                                note != null ?
                                        Config.HOVER_MESSAGE_NOTE.translate()
                                                .replaceAll("%note%", note)
                                                .replaceAll("%info%", info)
                                        : Config.HOVER_MESSAGE.translate()
                                                .replaceAll("%info%", info)
                        ).create()
                )
        );

        alert.setClickEvent(
                new ClickEvent(
                        ClickEvent.Action.RUN_COMMAND,
                        "/tp " + data.getPlayer().getName()
                )
        );

        Aquarium.getInstance().getExecutorService().execute(()
                -> Aquarium.getInstance().getPlayerDataManager().getAlertingPlayers()
                .forEach(data -> Objects.requireNonNull(data.getPlayer())
                        .spigot().sendMessage(alert)));

        Aquarium.getInstance().getDatabaseManager().addLog(
                new Log(data.getPlayer().getUniqueId(),
                        System.currentTimeMillis(),
                        type, name, info, vl));

        if (setback)
            data.getSetbackProcessor().setback();

        if (vl >= maxVl && punish && !data.isPunishing()
                && !(data.getPlayer().hasPermission("aquarium.bypass")
                && (boolean) Config.BYPASS_PUNISHMENT.getValue())) {
            data.setPunishing(true);

            Bukkit.getScheduler().runTask(Aquarium.getInstance(),
                    () -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), punishCommand));

            Aquarium.getInstance().getDatabaseManager().addLog(
                    new Log(data.getPlayer().getUniqueId(),
                            System.currentTimeMillis(),
                            type, name, "Punished", vl));
        }
    }

    public void debug(String debug) {
        if (data.getDebug().equals(type.toLowerCase() + name.toLowerCase())) {
            data.getPlayer().sendMessage(debug);
        }
    }
}
