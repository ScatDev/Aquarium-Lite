package dev.scat.aquarium.util;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CBuffer {

    private final double max;
    private double value;

    public CBuffer(double max) {
        this.max = max;
    }

    public boolean canFlag() {
        return value > max;
    }

    public boolean fail(double add) {
        value += add;
        return canFlag();
    }

    public boolean fail() {
        fail(1);
        return canFlag();
    }

    public void reduce(double reward) {
        value = Math.max(0, value - reward);
    }

    public void reduce() {
        reduce(1);
    }

    public void reset() {
        value = 0;
    }
}
