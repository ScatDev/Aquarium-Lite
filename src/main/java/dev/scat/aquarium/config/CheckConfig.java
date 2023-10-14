package dev.scat.aquarium.config;

import dev.scat.aquarium.Aquarium;
import org.bukkit.configuration.file.FileConfiguration;

public class CheckConfig {

    private FileConfiguration config;
    
    public void setup() {
        config = Aquarium.getInstance().getConfig();
    }

    public boolean isEnabled(String type, String name) {
        String path = "checks." + type.toLowerCase() + "." + name.toLowerCase() + ".enabled";

        if (config.contains(path)) {
            return (boolean) config.get(path);
        } else {
            config.set(path, true);
            Aquarium.getInstance().saveConfig();

            return true;
        }
    }

    public boolean isPunishable(String type, String name) {
        String path = "checks." + type.toLowerCase() + "." + name.toLowerCase() + ".punishable";

        if (config.contains(path)) {
            return (boolean) config.get(path);
        } else {
            config.set(path, true);
            Aquarium.getInstance().saveConfig();

            return true;
        }
    }

    public double getMaxVl(String type, String name) {
        String path = "checks." + type.toLowerCase() + "." + name.toLowerCase() + ".max-vl";

        if (config.contains(path)) {
            return config.getDouble(path);
        } else {
            config.set(path, 25D);
            Aquarium.getInstance().saveConfig();

            return 25D;
        }
    }

    public String getPunishCommand(String type, String name) {
        String path = "checks." + type.toLowerCase() + "." + name.toLowerCase() + ".punish-command";

        if (config.contains(path)) {
            return config.getString(path);
        } else {

            return Config.PUNISH_COMMAND.translate();
        }
    }
}
