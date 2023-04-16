package dev.scat.aquarium.util;

import com.github.retrooper.packetevents.protocol.world.states.WrappedBlockState;
import com.github.retrooper.packetevents.protocol.world.states.type.StateTypes;
import dev.scat.aquarium.data.PlayerData;
import dev.scat.aquarium.util.mc.AxisAlignedBB;
import dev.scat.aquarium.util.mc.MathHelper;
import dev.scat.aquarium.util.mc.Vec3;
import lombok.experimental.UtilityClass;

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
        float f = MathHelper.cos(-yaw * 0.017453292F - (float) Math.PI);
        float f1 = MathHelper.sin(-yaw * 0.017453292F - (float) Math.PI);
        float f2 = -MathHelper.cos(-pitch * 0.017453292F);
        float f3 = MathHelper.sin(-pitch * 0.017453292F);
        return new Vec3((double) (f1 * f2), (double) f3, (double) (f * f2));
    }

    public boolean isInWater(PlayerData data) {
        AxisAlignedBB bb = getBoundingBox(data.getPositionProcessor().getX(),
                data.getPositionProcessor().getY(), data.getPositionProcessor().getZ())
                .expand(0, -0.4000000059604645D, 0)
                .contract(0.001, 0.001, 0.001);

        for (int x = MathHelper.floor_double(bb.minX); x <= MathHelper.floor_double(bb.maxX); x++) {
            for (int y = MathHelper.floor_double(bb.minY); y <= MathHelper.floor_double(bb.maxY); y++) {
                for (int z = MathHelper.floor_double(bb.minZ); z <= MathHelper.floor_double(bb.maxZ); z++) {
                    WrappedBlockState block = data.getWorldProcessor().getBlock(x, y, z);

                    if (block.getType() == StateTypes.WATER)
                        return true;
                }
            }
        }

        return false;
    }

    public boolean isInLava(PlayerData data) {
        AxisAlignedBB bb = getBoundingBox(data.getPositionProcessor().getX(),
                data.getPositionProcessor().getY(), data.getPositionProcessor().getZ())
                .expand(-0.10000000149011612D, -0.4000000059604645D, -0.10000000149011612D);

        for (int x = MathHelper.floor_double(bb.minX); x <= MathHelper.floor_double(bb.maxX); x++) {
            for (int y = MathHelper.floor_double(bb.minY); y <= MathHelper.floor_double(bb.maxY); y++) {
                for (int z = MathHelper.floor_double(bb.minZ); z <= MathHelper.floor_double(bb.maxZ); z++) {
                    WrappedBlockState block = data.getWorldProcessor().getBlock(x, y, z);

                    if (block.getType() == StateTypes.LAVA)
                        return true;
                }
            }
        }

        return false;
    }
}
