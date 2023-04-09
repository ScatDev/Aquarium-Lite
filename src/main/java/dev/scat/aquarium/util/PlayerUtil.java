package dev.scat.aquarium.util;

import dev.scat.aquarium.util.mc.AxisAlignedBB;
import dev.scat.aquarium.util.mc.Vec3;
import lombok.experimental.UtilityClass;
import net.minecraft.server.v1_8_R3.MathHelper;

@UtilityClass
public class PlayerUtil {

    public float getEyeHeight(boolean sneaking) {
        float eyeHeight = 1.62F;

        if (sneaking) {
            eyeHeight -= 0.08F;
        }

        return eyeHeight;
    }

    public AxisAlignedBB getBoundingBox(double x, double y, double z) {
        float width = 0.6F / 2.0F;
        
        return new AxisAlignedBB(x - width, y, z - width, x + width, y + 1.8F, z + width);
    }

    public final Vec3 getVectorForRotation(float pitch, float yaw) {
        float f = MathHelper.cos(-yaw * 0.017453292F - (float)Math.PI);
        float f1 = MathHelper.sin(-yaw * 0.017453292F - (float)Math.PI);
        float f2 = -MathHelper.cos(-pitch * 0.017453292F);
        float f3 = MathHelper.sin(-pitch * 0.017453292F);
        return new Vec3((double)(f1 * f2), (double)f3, (double)(f * f2));
    }
}
