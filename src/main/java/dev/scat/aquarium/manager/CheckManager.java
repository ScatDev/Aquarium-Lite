package dev.scat.aquarium.manager;

import dev.scat.aquarium.check.Check;
import dev.scat.aquarium.check.impl.badpackets.*;
import dev.scat.aquarium.check.impl.fly.*;
import dev.scat.aquarium.check.impl.groundspoof.GroundSpoofA;
import dev.scat.aquarium.check.impl.speed.SpeedA;
import dev.scat.aquarium.check.impl.timer.TimerA;
import dev.scat.aquarium.data.PlayerData;

import java.util.Arrays;
import java.util.List;

public class CheckManager {

    public List<Check> loadChecks(PlayerData data) {
        return Arrays.asList(
                new FlyA(data),
                new FlyB(data),
                new GroundSpoofA(data),
                new SpeedA(data),
                new TimerA(data),
                new BadPacketsA(data),
                new BadPacketsB(data),
                new BadPacketsC(data),
                new BadPacketsD(data),
                new BadPacketsE(data),
                new BadPacketsF(data),
                new BadPacketsG(data),
                new BadPacketsH(data)
        );
    }
}
