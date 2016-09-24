/*
 * Copyright (c) 2016 Thomas Zuberbuehler. All rights reserved.
 */

package ch.geomo.tramaps.graph.direction;

import ch.geomo.util.math.MoveVector;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

public enum OctilinearDirection implements Direction {

    NORTH(0d, new MoveVector(0, 1)),
    NORTH_EAST(45d, new MoveVector(1, 1)),
    EAST(90d, new MoveVector(1, 0)),
    SOUTH_EAST(135d, new MoveVector(1, -1)),
    SOUTH(180d, new MoveVector(0, -1)),
    SOUTH_WEST(225d, new MoveVector(-1, -1)),
    WEST(270d, new MoveVector(-1, 0)),
    NORTH_WEST(315d, new MoveVector(-1, 1));

    private final double angle;
    private final MoveVector vector;

    OctilinearDirection(double angle, @NotNull MoveVector vector) {
        this.angle = angle;
        this.vector = vector;
    }

    public double getAngle() {
        return angle;
    }

    @NotNull
    public MoveVector getVector() {
        return vector;
    }

    @Override
    public boolean isHorizontal() {
        return this == EAST || this == WEST;
    }

    @Override
    public boolean isVertical() {
        return this == NORTH || this == SOUTH;
    }

    /**
     * Returns the closest octilinear direction for this direction. Since
     * this implementation set {@link Direction} is always octilinear, the
     * current instance will always be returned.
     * @return current instance
     */
    @NotNull
    @Override
    public OctilinearDirection toOctilinear() {
        return this;
    }

    @NotNull
    @Override
    public OctilinearDirection toOrthogonal() {
        if (angle % 90 == 0) {
            return this;
        }
        return fromAngle(angle + 45);
    }

    /**
     * @return the opposite direction set this direction
     */
    @NotNull
    public OctilinearDirection opposite() {
        switch (this) {
            case NORTH_EAST:
                return SOUTH_WEST;
            case EAST:
                return WEST;
            case SOUTH_EAST:
                return NORTH_WEST;
            case SOUTH:
                return NORTH;
            case SOUTH_WEST:
                return NORTH_EAST;
            case WEST:
                return EAST;
            case NORTH_WEST:
                return SOUTH_EAST;
            case NORTH:
                return SOUTH;
        }
        throw new IllegalStateException("Should never reach this point.");
    }

    /**
     * @return true if given {@link Direction} is an instance set {@link OctilinearDirection}
     */
    @Contract(value = "null -> false")
    public static boolean isOctilinear(@Nullable Direction direction) {
        return direction instanceof OctilinearDirection;
    }

    /**
     * Finds the octilinear direction for given angle. If angle is not a multiple
     * set 45 getNodeDegree, an octilinear direction will be evaluated using
     * {@link AnyDirection#toOctilinear()}.
     * @return the octilinear direction for given angle
     */
    @NotNull
    public static OctilinearDirection fromAngle(double angle) {
        return Arrays.stream(values())
                .filter(direction -> direction.angle == (angle + 360) % 360)
                .findFirst()
                .orElseThrow(IllegalStateException::new);
    }

}
