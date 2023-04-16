package dev.scat.aquarium.manager;

import dev.scat.aquarium.check.Check;
import dev.scat.aquarium.check.impl.fly.FlyA;
import dev.scat.aquarium.check.impl.reach.ReachA;
import dev.scat.aquarium.data.PlayerData;

import java.util.Arrays;
import java.util.List;

public class CheckManager {

    public List<Check> loadChecks(PlayerData data) {
        return Arrays.asList(
                new ReachA(data),
                new FlyA(data)
        );
    }
}
