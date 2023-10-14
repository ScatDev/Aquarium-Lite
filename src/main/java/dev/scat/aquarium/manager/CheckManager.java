package dev.scat.aquarium.manager;

import dev.scat.aquarium.check.Check;
import dev.scat.aquarium.check.impl.fly.FlyA;
import dev.scat.aquarium.check.impl.fly.FlyB;
import dev.scat.aquarium.check.impl.groundspoof.GroundSpoofA;
import dev.scat.aquarium.check.impl.speed.SpeedA;
import dev.scat.aquarium.data.PlayerData;

import java.util.Arrays;
import java.util.List;

public class CheckManager {

    public List<Check> loadChecks(PlayerData data) {
        return Arrays.asList(
                new FlyA(data),
                new FlyB(data),
                new GroundSpoofA(data),
                new SpeedA(data)
        );
    }
}
