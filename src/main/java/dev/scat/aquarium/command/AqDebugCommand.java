package dev.scat.aquarium.command;

import dev.scat.aquarium.Aquarium;
import dev.scat.aquarium.config.Config;
import dev.scat.aquarium.data.PlayerData;
import dev.scat.aquarium.util.ColorUtil;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

public class AqDebugCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("aquarium.debug")) {
            sender.sendMessage(Config.NO_PERMISSION_MESSAGE.translate());
            return false;
        }

        if (sender instanceof ConsoleCommandSender) {
            sender.sendMessage(Config.PLAYER_ONLY_MESSAGE.translate());
            return false;
        }

        if (args.length != 1) {
            sender.sendMessage(ColorUtil.translate("&cWrong usage, please use : /aqdebug <check>"));
            return false;
        }

        String check = args[0];

        Player player = (Player) sender;
        PlayerData data = Aquarium.getInstance().getPlayerDataManager().get(player.getUniqueId());
        data.setDebug(check.toLowerCase());

        player.sendMessage(ChatColor.GREEN + "Set your debug to " + ChatColor.YELLOW + check + ChatColor.GREEN + ".");
        return true;
    }
}
