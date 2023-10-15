package dev.scat.aquarium.check.impl.badpackets;

import dev.scat.aquarium.check.Check;
import dev.scat.aquarium.data.PlayerData;

public class BadPacketsA extends Check {

    public BadPacketsA(PlayerData data) {
        super(data, "BadPackets", "A");
    }
}
