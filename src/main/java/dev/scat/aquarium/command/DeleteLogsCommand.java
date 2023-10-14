package dev.scat.aquarium.command;

import dev.scat.aquarium.Aquarium;
import dev.scat.aquarium.config.Config;
import dev.scat.aquarium.util.ColorUtil;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class DeleteLogsCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("aquarium.deletelogs")) {
            sender.sendMessage(Config.NO_PERMISSION_MESSAGE.translate());
            return false;
        }

        if (args.length != 1) {
            sender.sendMessage(ColorUtil.translate("&cUsage: /deletelogs <player>"));
            return false;
        }

        OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);

        if (!target.hasPlayedBefore()) {
            sender.sendMessage(Config.PLAYER_HASNT_JOINED_MESSAGE.translate()
                    .replaceAll("%player%", target.getName()));
            return false;
        }

        Aquarium.getInstance().getDatabaseManager().removeLogs(target.getUniqueId());

        sender.sendMessage(Config.DELETED_LOGS_MESSAGE.translate()
                .replaceAll("%player%", target.getName()));

        return false;
    }
}
