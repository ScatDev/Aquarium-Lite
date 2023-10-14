package dev.scat.aquarium.util.entity;

import dev.scat.aquarium.util.PlayerUtil;
import dev.scat.aquarium.util.mc.AxisAlignedBB;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class TrackedPosition {

    private int interpolationSteps;
    private double posX, posY, posZ, lastPosX, lastPosY, lastPosZ, mpX, mpY, mpZ;

    @Setter
    private boolean compensated;

    public TrackedPosition(double x, double y, double z) {
        posX = x;
        posY = y;
        posZ = z;
    }

    public void interpolate() {
        if (interpolationSteps > 0) {
            lastPosX = posX;
            lastPosY = posY;
            lastPosZ = posZ;

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

    public AxisAlignedBB getBoundingBox() {
        return PlayerUtil.getBoundingBox(posX, posY, posZ);
    }

    public TrackedPosition clone() {
        return new TrackedPosition(interpolationSteps, posX, posY, posZ, lastPosX, lastPosY, lastPosZ, mpX, mpY, mpZ, compensated);
    }
}