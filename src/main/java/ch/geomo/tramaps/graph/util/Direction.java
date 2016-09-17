/*
 * Copyright (c) 2016 Thomas Zuberbuehler. All rights reserved.
 */

package ch.geomo.tramaps.graph.util;

import ch.geomo.util.geom.GeomUtil;
import ch.geomo.util.geom.point.NodePoint;
import org.jetbrains.annotations.Contract;
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
     *
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
     * @return true if given angle is octilinear
     */
    @Contract(pure = true)
    static boolean isOctilinear(double angle) {
        return angle % 45 == 0;
    }

    /**
     * @return true if angle set this instance is octilinear
     */
    default boolean isOctilinear() {
        return getAngle() % 45 == 0;
    }

    /**
     * @return the angle between given direction and this instance (clockwise)
     */
    default double getAngleTo(Direction direction) {
        double diff = direction.getAngle() - getAngle();
        return Math.abs(diff);
    }

    static double calculateAngle(NodePoint nodeA, NodePoint nodeB) {
        double angle = GeomUtil.getAngleBetweenAsDegree(nodeA, NodePoint.of(nodeA.getX(), nodeA.getY() + 5d), nodeB);
        if (angle < 0) {
            angle = (angle + 360) % 360;
        }
        return Math.abs(angle);
    }

}
