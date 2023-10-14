package dev.scat.aquarium.command;

import dev.scat.aquarium.Aquarium;
import dev.scat.aquarium.config.Config;
import dev.scat.aquarium.database.Log;
import dev.scat.aquarium.util.ColorUtil;
import dev.scat.aquarium.util.MathUtil;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class LogsCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("aquarium.logs")) {
            sender.sendMessage(Config.NO_PERMISSION_MESSAGE.translate());
            return false;
        }

        if (args.length != 1 && args.length != 2) {
            sender.sendMessage(ColorUtil.translate("&cWrong usage, please use : /logs <player> [page]"));
            return false;
        }

        OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);

        if (!target.hasPlayedBefore()) {
            sender.sendMessage(Config.PLAYER_HASNT_JOINED_MESSAGE.translate()
                    .replaceAll("%player%", target.getName()));
            return false;
        }

        int page = args.length == 2 ? Integer.parseInt(args[1]) : 1;

        if (page < 1) {
            sender.sendMessage(ColorUtil.translate("&cWrong usage, please use : /logs <player> [page]"));
            return false;
        }

        List<Log> logs = Aquarium.getInstance().getDatabaseManager().getLogs(target.getUniqueId(), page);

        if (logs.isEmpty()) {
            if (page == 1) {
                sender.sendMessage(Config.NO_LOGS_MESSAGE.translate()
                        .replaceAll("%player%", target.getName()));
            } else {
                sender.sendMessage(Config.NO_LOGS_ON_PAGE.translate()
                        .replaceAll("%player%", target.getName()));
            }

            return false;
        }

        long now = System.currentTimeMillis();

        sender.sendMessage(Config.LOGS_START_MESSAGE.translate()
                .replaceAll("%player%", target.getName())
                .replaceAll("%page%", String.valueOf(page)));

        logs.forEach(log -> {
            String time = MathUtil.friendlyTimeDiff(now - log.getTime());

            TextComponent message = new TextComponent();

            if (sender instanceof Player) {
                Player player = (Player) sender;

                message.setText(Config.LOG_MESSAGE.translate()
                        .replaceAll("%player%", target.getName())
                        .replaceAll("%type%", log.getType())
                        .replaceAll("%name%", log.getName())
                        .replaceAll("%vl%", String.valueOf(log.getVl()))
                        .replaceAll("%time%", time));

                message.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                        new ComponentBuilder(log.getInfo()).create()));


                player.spigot().sendMessage(message);
            } else {
                sender.sendMessage(Config.LOG_MESSAGE.translate()
                        .replaceAll("%player%", target.getName())
                        .replaceAll("%type%", log.getType())
                        .replaceAll("%name%", log.getName())
                        .replaceAll("%vl%", String.valueOf(log.getVl()))
                        .replaceAll("%time%", time));
            }
        });

        return false;
    }
}
