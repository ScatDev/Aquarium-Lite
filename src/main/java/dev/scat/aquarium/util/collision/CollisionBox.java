package dev.scat.aquarium.util.collision;

import dev.scat.aquarium.util.mc.AxisAlignedBB;

import java.util.List;

public interface CollisionBox {

    CollisionBox copy();

    CollisionBox offset(double x, double y, double z);

    boolean isFullBlock();

    boolean isCollided(AxisAlignedBB boundingBox);

    boolean isCollided(CollisionBox collisionBox);

    List<SimpleCollisionBox> getBoxes();

    double calculateYOffset(AxisAlignedBB bb, double y);
}
