/*
 * Copyright (c) 2016 Thomas Zuberbuehler. All rights reserved.
 */

package ch.geomo.tramaps.graph.util;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public interface Direction {

    /**
     * @return the angle of the direction
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
    @Contract(pure = true)
    default boolean isDiagonal() {
        return !isHorizontal() && !isVertical();
    }

    /**
     * Returns the closest octilinear direction for this direction. Depending on
     * the implementation the angle will be rounded up or down.
     *
     * @return the closest octilinear direction
     */
    @NotNull
    OctilinearDirection toOctilinearDirection();

    /**
     * @return the opposite direction
     */
    @NotNull
    Direction oppositeDirection();

    /**
     * @return true if given angle is octilinear
     */
    @Contract(pure = true)
    static boolean isOctilinear(double angle) {
        return angle % 45 == 0;
    }

}
