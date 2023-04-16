package dev.scat.aquarium.config;

import dev.scat.aquarium.Aquarium;
import dev.scat.aquarium.util.ColorUtil;
import lombok.Getter;
import org.bukkit.Bukkit;

@Getter
public enum Config {
    PREFIX("prefix", "x"),
    ALERT_MESSAGE("alert-message", "&8[&cA&8] &c%player% &ffailed &c%type% &7(&c%name%&7) &fx%vl%"),
    HOVER_MESSAGE("hover-message", "%info%\n\n&cClick to teleport!"),
    BYPASS_PUNISHMENT("bypass-punishment", true),
    VL_RESET_DELAY("vl-reset-delay", 600),
    DATABASE_TYPE("database-type", "flat-file"),
    PUNISH_COMMAND("punish-command", "ban %player% Aquarium Anticheat"),
    PLAYER_ONLY_MESSAGE("player-only-message", "&cThis command is player only!"),
    NO_PERMISSION_MESSAGE("no-permission-message", "&cNo permission."),
    ALERTS_DISABLED_MESSAGE("alerts-disabled-message", "&cYou have disabled your anticheat alerts."),
    ALERTS_ENABLED_MESSAGE("alerts-enabled-message", "&aYou have enabled your anticheat alerts.");

    private final String path;
    private final Object value;

    Config(String path, Object value) {
        this.path = path;

        if (Aquarium.getInstance().getConfig().contains(path)) {
            this.value = Aquarium.getInstance().getConfig().get(path);
        } else {
            Aquarium.getInstance().getConfig().set(path, value);
            Aquarium.getInstance().saveConfig();

            this.value = value;
        }
    }

    public String translate() {
        return ColorUtil.translate((String) value);
    }
}
