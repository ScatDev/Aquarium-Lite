package dev.scat.aquarium.util;

import dev.scat.aquarium.util.mc.AxisAlignedBB;
import dev.scat.aquarium.util.mc.MathHelper;
import dev.scat.aquarium.util.mc.Vec3;
import lombok.Getter;
import lombok.experimental.UtilityClass;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@UtilityClass
public class RotationUtil {

    public final Set<Float> SENSITIVITIES = new HashSet<>();

    static {
        for (int i = 218; i < 368; i++) {
            float value = (float)(i - 222) / (float)(142);

            if (value >= 0.0 && value <= 1.0) {
                float factor = value * 0.6F + 0.2F;
                float constant = factor * factor * factor * 8.0F;

                SENSITIVITIES.add(constant);
            }
        }
    }

    public Vec3 getCenter(AxisAlignedBB bb) {
        return new Vec3((bb.maxX - bb.minX) / 2D, (bb.maxY - bb.minY) / 2D, (bb.maxZ - bb.minZ) / 2D);
    }

    public float getAuraYaw(Vec3 pos, Vec3 targetPos) {
        double zDiff = targetPos.zCoord - pos.zCoord;
        double xDiff = targetPos.xCoord - pos.xCoord;

        return MathHelper.wrapAngleTo180_float(
                (float) (Math.toDegrees(Math.atan2(zDiff, xDiff)) - 90F)
        );
    }

    public float getSensitivityFromGcd(float gcd) {
        float closest = 1, lowestDiff = Float.MAX_VALUE;

        for (float sensitivity : RotationUtil.SENSITIVITIES) {
            float diff = Math.abs((sensitivity * 0.15F) - gcd);

            if (diff < lowestDiff) {
                closest = sensitivity;
                lowestDiff = diff;
            } else if (diff > lowestDiff) {
                // We are going past the closest sensitivity
                break;
            }
        }

        return closest;
    }
}
