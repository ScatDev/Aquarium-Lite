package dev.scat.aquarium.command;

import dev.scat.aquarium.Aquarium;
import dev.scat.aquarium.config.Config;
import dev.scat.aquarium.data.PlayerData;
import dev.scat.aquarium.util.ColorUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

public class AlertsCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof ConsoleCommandSender) {
            sender.sendMessage(Config.PLAYER_ONLY_MESSAGE.translate());
            return false;
        }

        if (!sender.hasPermission("aquarium.alerts")) {
            sender.sendMessage(Config.NO_PERMISSION_MESSAGE.translate());
        }

        if (args.length != 0) {
            sender.sendMessage(ColorUtil.translate("&cWrong usage, please use : /alerts"));
            return false;
        }

        PlayerData data = Aquarium.getInstance().getPlayerDataManager()
                .get(((Player) sender).getUniqueId());

        if (data.isAlerting()) {
            data.setAlerting(false);

            sender.sendMessage(Config.ALERTS_DISABLED_MESSAGE.translate());
        } else {
            data.setAlerting(true);

            sender.sendMessage(Config.ALERTS_ENABLED_MESSAGE.translate());
        }

        return true;
    }
}
