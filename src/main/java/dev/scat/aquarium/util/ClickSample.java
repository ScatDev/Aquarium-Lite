package dev.scat.aquarium.util;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Salers
 * made on eu.salers.apollon.util
 */
public class ClickSample {

    public List<Integer> delays;
    public int size;
    public int clearSize;
    @Getter
    public ClickingStats lastStats, stats;

    public ClickSample(int size, boolean clearOnMax) {
        this.size = size;
        this.clearSize = clearOnMax ? size : size + 5;
        delays = new ArrayList<>();
        stats = lastStats = new ClickingStats();

    }

    public boolean add(int d) {
        if (delays.size() > size) {
            lastStats = stats;
            stats = new ClickingStats(delays);
            if (delays.size() >= clearSize)
                delays.clear();


        }
        if (d <= 4 && d > 0)
            return delays.add(d);

        return false;


    }

    public boolean isReady() {
        return delays.size() >= size;
    }


}