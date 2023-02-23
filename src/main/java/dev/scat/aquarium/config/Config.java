package dev.scat.aquarium.config;

import dev.scat.aquarium.Aquarium;
import dev.scat.aquarium.util.ColorUtil;
import lombok.Getter;

@Getter
public enum Config {
    PREFIX("prefix", "x");

    private final String path, value;

    Config(String path, String value) {
       this.path = path;

        if (Aquarium.getInstance().getConfig().contains(path)) {
            this.value = ColorUtil.translate((String) Aquarium.getInstance().getConfig().get(path));
        } else {
            Aquarium.getInstance().getConfig().set(path, value);

            this.value = ColorUtil.translate(value);
        }
    }
}
