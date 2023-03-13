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
    PUNISH_COMMAND("punish-command", "ban %player% Aquarium Anticheat");

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
