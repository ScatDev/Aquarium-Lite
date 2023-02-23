package dev.scat.aquarium.check;

import dev.scat.aquarium.Aquarium;
import dev.scat.aquarium.config.Config;
import dev.scat.aquarium.data.PlayerData;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public abstract class Check {

    protected final PlayerData data;
    private String type, name;

    @Getter
    private boolean enabled = Aquarium.getInstance().getCheckConfig().enabled(type, name);

}
