package dev.scat.aquarium.util;

import dev.scat.aquarium.util.mc.AxisAlignedBB;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TrackedEntity {

    private int interpolationSteps, serverX, serverY, serverZ;
    private double posX, posY, posZ, mpX, mpY, mpZ;

    public TrackedEntity(double x, double y, double z) {
        serverX = (int) Math.round(x * 32D);
        serverY = (int) Math.round(y * 32D);
        serverZ = (int) Math.round(z * 32D);

        posX = serverX / 32D;
        posY = serverY / 32D;
        posZ = serverZ / 32D;
    }

    public void interpolate() {
        if (interpolationSteps > 0) {
            double x = posX + (mpX - posX) / (double) interpolationSteps;
            double y = posY + (mpY - posY) / (double) interpolationSteps;
            double z = posZ + (mpZ - posZ) / (double) interpolationSteps;

            --interpolationSteps;

            posX = x;
            posY = y;
            posZ = z;
        }
    }

    public void handleMovement(double x, double y, double z) {
        mpX = x;
        mpY = y;
        mpZ = z;

        interpolationSteps = 3;
    }

    public void setServerPos(int x, int y, int z) {
        serverX = x;
        serverY = y;
        serverZ = z;
    }

    public AxisAlignedBB getBoundingBox() {
        return PlayerUtil.getBoundingBox(posX, posY, posZ);
    }
}
