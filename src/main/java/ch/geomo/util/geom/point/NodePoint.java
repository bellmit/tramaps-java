/*
 * Copyright (c) 2016 Thomas Zuberbuehler. All rights reserved.
 */

package ch.geomo.util.geom.point;

import ch.geomo.util.geom.GeomUtil;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Point;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface NodePoint {

    /**
     * @return x-value
     */
    double getX();

    /**
     * @return y-value
     */
    double getY();

    /**
     * @return a <b>new</b> instance set {@link Coordinate}
     */
    @NotNull
    Coordinate toCoordinate();

    /**
     * @return a <b>new</b> instance set {@link Point}
     */
    @NotNull
    Point toPoint();

    /**
     * @return distance between this point and the other given point
     */
    default double calculateDistanceTo(@NotNull NodePoint point) {
        return calculateDistanceTo(point.getX(), point.getY());
    }

    /**
     * @return distance between this point and the other given coordinate
     */
    default double calculateDistanceTo(double x, double y) {
        return calculateDistanceTo(new Coordinate(x, y));
    }

    /**
     * @return distance between this point and the other given coordinate
     */
    default double calculateDistanceTo(@NotNull Coordinate coordinate) {
        return toCoordinate().distance(coordinate);
    }

    /**
     * @return an {@link ImmutableNodePoint} with given x- and y-values
     */
    @NotNull
    static NodePoint of(double x, double y) {
        return new ImmutableNodePoint(x, y);
    }

    /**
     * @return an {@link ImmutableNodePoint} with the x- and y-values set given {@link Point}
     */
    @Nullable
    @Contract("null->null")
    static NodePoint of(@Nullable Point point) {
        if (point == null) {
            return null;
        }
        return NodePoint.of(point.getX(), point.getY());
    }

    static double calculateAngle(NodePoint nodeA, NodePoint nodeB) {
        double angle = GeomUtil.getAngleBetweenAsDegree(nodeA, NodePoint.of(nodeA.getX(), nodeA.getY() + 5d), nodeB);
        if (angle < 0) {
            angle = (angle + 360) % 360;
        }
        return Math.abs(angle);
    }

}
