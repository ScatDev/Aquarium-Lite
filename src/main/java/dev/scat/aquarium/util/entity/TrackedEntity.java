package dev.scat.aquarium.util.entity;

import dev.scat.aquarium.data.PlayerData;
import dev.scat.aquarium.util.mc.AxisAlignedBB;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Setter
public class TrackedEntity {

    private final PlayerData data;

    private Set<TrackedPosition> positions = new HashSet<>();

    private int serverX, serverY, serverZ;

    @Setter
    private boolean confirming;

    @Setter
    private MovementType lastMovement = MovementType.NONE;

    public TrackedEntity(PlayerData data, double x, double y, double z) {
        this.data = data;

        serverX = (int) Math.round(x * 32D);
        serverY = (int) Math.round(y * 32D);
        serverZ = (int) Math.round(z * 32D);

        positions.add(new TrackedPosition(serverX / 32D, serverY / 32D, serverZ / 32D));
    }

    public void interpolate() {
        if (confirming) {
            List<TrackedPosition> toAdd = new ArrayList<>();

            positions.forEach(pos -> {
                if (!pos.isCompensated()) {
                    TrackedPosition newPos = pos.clone();

                    if (lastMovement == MovementType.RELATIVE) {
                        newPos.handleMovement(serverX / 32D, serverY / 32D, serverZ / 32D);
                    } else {
                        if (Math.abs(newPos.getPosX() - (serverX / 32D)) < 0.03125D
                                && Math.abs(newPos.getPosY() - (serverY / 32D)) < 0.015625D
                                && Math.abs(newPos.getPosZ() - (serverZ / 32D)) < 0.03125D) {
                            newPos.handleMovement(newPos.getPosX(), newPos.getPosY(), newPos.getPosZ());
                        } else {
                            newPos.handleMovement(serverX / 32D, serverY / 32D, serverZ / 32D);
                        }
                    }

                    newPos.setCompensated(true);

                    toAdd.add(newPos);
                }
            });

            positions.addAll(toAdd);
        }

        positions.forEach(TrackedPosition::interpolate);

        if (positions.size() > 25) {
            data.getUser().closeConnection();
        }
    }

    public void setServerPos(int x, int y, int z) {
        serverX = x;
        serverY = y;
        serverZ = z;
    }

    // TODO: optimize this shit
    public List<AxisAlignedBB> getBoundingBoxes() {
        return positions.stream().map(TrackedPosition::getBoundingBox
        ).collect(Collectors.toList());
    }
}