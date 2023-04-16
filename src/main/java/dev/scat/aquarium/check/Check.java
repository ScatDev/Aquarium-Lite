package dev.scat.aquarium.check;

import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import dev.scat.aquarium.Aquarium;
import dev.scat.aquarium.config.Config;
import dev.scat.aquarium.data.PlayerData;
import dev.scat.aquarium.database.Log;
import lombok.Getter;
import lombok.Setter;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;

@Getter
@Setter
public abstract class Check {

    protected final PlayerData data;
    private final String type, name;
    private boolean enabled, punish;
    private int maxVl, vl;
    private String punishCommand;
    protected double buffer;

    public Check(PlayerData data, String type, String name) {
        this.data = data;
        this.type = type;
        this.name = name;

        enabled = Aquarium.getInstance().getCheckConfig().isEnabled(type, name);
        punish = Aquarium.getInstance().getCheckConfig().isPunishable(type, name);
        maxVl = Aquarium.getInstance().getCheckConfig().getMaxVl(type, name);
        punishCommand = Aquarium.getInstance().getCheckConfig().getPunishCommand(type, name)
                .replaceAll("%player%", data.getPlayer().getName());
    }
    
    public void handle(PacketReceiveEvent event) {}
    public void handle(PacketSendEvent event) {}

    public void flag(String info) {
        Aquarium.getInstance().getExecutorService().execute(() -> {
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
                                    Config.HOVER_MESSAGE.translate()
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

            // could make a list of alerting players but im too lazy rn
            Aquarium.getInstance().getPlayerDataManager().getValues().stream()
                    .filter(PlayerData::isAlerting)
                    .filter(data -> data.getPlayer() != null)
                    .forEach(data -> data.getPlayer().spigot().sendMessage(alert));

            Aquarium.getInstance().getDatabaseManager().addLog(
                    new Log(data.getPlayer().getUniqueId(),
                            System.currentTimeMillis(),
                            type + " (" + name + ")", info, vl));

            if (vl >= maxVl && punish && !data.isPunishing()
                    && !(data.getPlayer().hasPermission("aquarium.bypass")
                    && (boolean) Config.BYPASS_PUNISHMENT.getValue())) {
                data.setPunishing(true);

                Bukkit.getScheduler().runTask(Aquarium.getInstance(),
                        () -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), punishCommand));

                Aquarium.getInstance().getDatabaseManager().addLog(
                        new Log(data.getPlayer().getUniqueId(),
                                System.currentTimeMillis(),
                                type + " (" + name + ")", "Punished", vl));
            }
        });
    }
}
