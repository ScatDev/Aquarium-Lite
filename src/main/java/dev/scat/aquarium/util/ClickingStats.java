package dev.scat.aquarium.util;

import lombok.Getter;

import java.util.List;

/**
 * @author Salers
 * made on dev.notonweed.annunaki.util
 */

@Getter

public class ClickingStats {

    private final double entropy, std, kurtosis, variance, skewness, cps;
    private final int outliers, duplicates;

    public ClickingStats(final List<Integer> delays) {
        entropy = MathUtil.getEntropy(delays);
        std = MathUtil.getStandardDeviation(delays);
        kurtosis = MathUtil.getKurtosis(delays);
        variance = MathUtil.getVariance(delays);
        skewness = MathUtil.getSkewness(delays);
        cps = MathUtil.getCps(delays);
        outliers = MathUtil.getOutliers(delays).getX().size() + MathUtil.getOutliers(delays).getY().size();
        duplicates = MathUtil.getDuplicates(delays);

    }

    public ClickingStats() {
        entropy = 22323;
        std = 1212;
        kurtosis = variance = 232;
        skewness = cps = 0;
        outliers = 212;
        duplicates = 0;
    }


}