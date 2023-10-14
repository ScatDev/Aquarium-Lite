package dev.scat.aquarium.util.lag;

import com.github.retrooper.packetevents.util.Vector3d;
import lombok.Getter;
import lombok.Setter;

/**
 * @author Salers
 * made on dev.scat.aquarium.util.lag
 */

@Getter
@Setter
public class ConfirmedVelocity {

    private double x, y, z, offset, percent, movementSpeed;
    private boolean confirming, validated, desync;

    public ConfirmedVelocity(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.confirming = true;
    }

    public ConfirmedVelocity(Vector3d vec) {
        this(vec.x, vec.y, vec.z);
    }

    public boolean isConfirmed() {
        return !confirming;
    }

    public boolean isDefault() {
        return x == 666.666171221;
    }
}
