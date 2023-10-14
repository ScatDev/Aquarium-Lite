package dev.scat.aquarium.util.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class Movement {

    private final MovementType type;
    private final int x, y, z;
}
