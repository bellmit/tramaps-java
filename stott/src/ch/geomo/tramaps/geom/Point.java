/*
 * Copyright (c) 2016 by Thomas Zuberbühler
 */

package ch.geomo.tramaps.geom;

import org.jetbrains.annotations.NotNull;

public interface Point<N extends Number & Comparable<N>> {

    /**
     * Get X coordinate.
     */
    @NotNull
    N getX();

    /**
     * Gets Y coordinate.
     */
    @NotNull
    N getY();

    /**
     * Calculates the distance between this point and the other given point.
     */
    double calculateDistanceTo(@NotNull Point<N> point);

    /**
     * Calculates the angle between three points while current point is the center point of a circle.
     */
    double calculateAngleBetween(@NotNull Point<N> p1, @NotNull Point<N> p2);

}
