package dev.scat.aquarium.config;

import dev.scat.aquarium.Aquarium;
import org.bukkit.configuration.file.FileConfiguration;

public class CheckConfig {

    private FileConfiguration config;

    public void setup() {
        config = Aquarium.getInstance().getConfig();
    }

    public boolean enabled(String type, String name) {
        String path = type.toLowerCase() + "." + name.toLowerCase() + ".enabled";

        if (config.contains(path)) {
            return (boolean) config.get(path);
        } else {
            config.set(path, true);

            return true;
        }
    }
}
