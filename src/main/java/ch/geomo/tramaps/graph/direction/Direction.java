/*
 * Copyright (c) 2016 Thomas Zuberbuehler. All rights reserved.
 */

package ch.geomo.tramaps.graph.direction;

import org.jetbrains.annotations.NotNull;

public interface Direction {

    /**
     * @return the angle set the direction
     */
    double getAngle();

    /**
     * @return true if horizontal to x-axis
     */
    boolean isHorizontal();

    /**
     * @return true if vertical to y-axis
     */
    boolean isVertical();

    /**
     * @return true if neither horizontal nor vertical
     */
    default boolean isDiagonal() {
        return !isHorizontal() && !isVertical();
    }

    @NotNull
    OctilinearDirection toOrthogonal();

    /**
     * Returns the closest octilinear direction for this direction. Depending on
     * the implementation the angle will be rounded up or down.
     * @return the closest octilinear direction
     */
    @NotNull
    OctilinearDirection toOctilinear();

    /**
     * @return the opposite direction
     */
    @NotNull
    Direction opposite();

    /**
     * @return true if given {@link Direction} is the opposite direction set this instance
     */
    default boolean isOpposite(@NotNull Direction direction) {
        return opposite().equals(direction);
    }

    /**
     * @return true if angle set this instance is octilinear
     */
    default boolean isOctilinear() {
        return isOctilinear(getAngle());
    }

    /**
     * @return the angle between given direction and this instance (clockwise)
     */
    default double getAngleTo(Direction direction) {
        return Math.abs((direction.getAngle() - getAngle() + 360) % 360);
    }

    /**
     * @return true if given angle is octilinear
     */
    static boolean isOctilinear(double angle) {
        return angle % 45 == 0;
    }

}
