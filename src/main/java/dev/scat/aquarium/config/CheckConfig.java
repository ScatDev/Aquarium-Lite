package dev.scat.aquarium.config;

import dev.scat.aquarium.Aquarium;
import org.bukkit.configuration.file.FileConfiguration;

public class CheckConfig {

    private FileConfiguration config;

    public void setup() {
        config = Aquarium.getInstance().getConfig();
    }

    public boolean isEnabled(String type, String name) {
        String path = type.toLowerCase() + "." + name.toLowerCase() + ".enabled";

        if (config.contains(path)) {
            return (boolean) config.get(path);
        } else {
            config.set(path, true);

            return true;
        }
    }

    public boolean isPunishable(String type, String name) {
        String path = type.toLowerCase() + "." + name.toLowerCase() + ".punishable";

        if (config.contains(path)) {
            return (boolean) config.get(path);
        } else {
            config.set(path, true);

            return true;
        }
    }

    public int getMaxVl(String type, String name) {
        String path = type.toLowerCase() + "." + name.toLowerCase() + ".max-vl";

        if (config.contains(path)) {
            return (int) config.get(path);
        } else {
            config.set(path, 25);

            return 25;
        }
    }
}
