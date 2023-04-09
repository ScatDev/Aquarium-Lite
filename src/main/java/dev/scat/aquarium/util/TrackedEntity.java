package dev.scat.aquarium.util;

import dev.scat.aquarium.util.mc.AxisAlignedBB;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TrackedEntity {

    private int interpolationSteps;
    private double posX, posY, posZ, serverX, serverY, serverZ;

    public TrackedEntity(double posX, double posY, double posZ) {
        this.posX = posX;
        this.posY = posY;
        this.posZ = posZ;
    }

    public void interpolate() {
        if (interpolationSteps > 3) {
            posX = posX + (serverX - posX) / (double) interpolationSteps;
            posY = posY + (serverY - posY) / (double) interpolationSteps;
            posZ = posZ + (serverZ - posZ) / (double) interpolationSteps;

            --interpolationSteps;
        }
    }

    public void handleMovement(double x, double y, double z) {
        serverX += x;
        serverY += y;
        serverZ += z;

        interpolationSteps = 3;
    }

    public AxisAlignedBB getBoundingBox() {
        return PlayerUtil.getBoundingBox(posX, posY, posZ);
    }
}
