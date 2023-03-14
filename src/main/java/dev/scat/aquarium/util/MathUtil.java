package dev.scat.aquarium.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public class MathUtil {

    public double angleDistance(double alpha, double beta) {
        double alphax = alpha % 360;
        double betax = beta % 360;

        double delta = Math.abs(alphax - betax);

        return Math.abs(Math.min(360.0 - delta, delta));
    }

    public float angleDistance(float alpha, float beta) {
        float alphax = alpha % 360;
        float betax = beta % 360;

        float delta = Math.abs(alphax - betax);

        return abs(Math.min(360F - delta, delta));
    }

    public float abs(float f) {
        return Float.intBitsToFloat(Float.floatToIntBits(f) & 0x7FFFFFFF);
    }
}
