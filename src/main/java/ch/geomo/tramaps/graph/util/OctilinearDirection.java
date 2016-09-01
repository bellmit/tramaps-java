/*
 * Copyright (c) 2016 Thomas Zuberbuehler. All rights reserved.
 */

package ch.geomo.tramaps.graph.util;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public enum OctilinearDirection implements Direction {

    NORTH(0d),
    NORTH_EAST(45d),
    EAST(90d),
    SOUTH_EAST(135d),
    SOUTH(180d),
    SOUTH_WEST(225d),
    WEST(270d),
    NORTH_WEST(315d);

    private double angle;

    OctilinearDirection(double angle) {
        this.angle = angle;
    }

    public double getAngle() {
        return angle;
    }

    @Contract(pure = true)
    public boolean isHorizontal() {
        return this == EAST || this == WEST;
    }

    @Contract(pure = true)
    public boolean isVertical() {
        return this == NORTH || this == SOUTH;
    }

    /**
     * Returns the closest octilinear direction for this direction. Since
     * this implementation of {@link Direction} is always octilinear, the
     * current instance will always be returned.
     *
     * @return current instance
     */
    @NotNull
    @Override
    @Contract(value = "->!null", pure = true)
    public OctilinearDirection toOctilinearDirection() {
        return this; // just satisfying interface :-)
    }

    /**
     * @return true if given {@link Direction} is an instance of {@link OctilinearDirection}
     */
    @Contract(value = "null -> false", pure = true)
    public static boolean isOctilinear(Direction direction) {
        return direction instanceof OctilinearDirection;
    }

    /**
     * Finds the octilinear direction for given angle. If angle is not a multiple
     * of 45 degree, an octilinear direction will be evaluated using
     * {@link AnyDirection#toOctilinearDirection()}.
     *
     * @return the octilinear direction for given angle
     */
    @NotNull
    public static OctilinearDirection fromAngle(double angle) {
        if (angle == 360) {
            return NORTH;
        }
        return Arrays.stream(values())
                .filter(direction -> direction.angle == angle)
                .findFirst()
                .orElseGet(() -> new AnyDirection(angle).toOctilinearDirection());
    }

    /**
     * @return the opposite direction of this direction
     */
    @NotNull
    public Direction oppositeDirection() {
        return fromAngle(Math.abs(360 - angle));
    }

}
