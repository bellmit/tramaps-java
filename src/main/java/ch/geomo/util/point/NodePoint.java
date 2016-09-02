/*
 * Copyright (c) 2016 Thomas Zuberbuehler. All rights reserved.
 */

package ch.geomo.util.point;

import ch.geomo.tramaps.geo.util.GeomUtil;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Point;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

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
     * Creates a new instance of {@link Coordinate} based on this {@link NodePoint} instance.
     *
     * @return an instance of {@link Coordinate}
     */
    @NotNull
    Coordinate toCoordinate();

    /**
     * Creates a new instance of {@link Point} based on this {@link NodePoint} instance.
     *
     * @return an instance of {@link Point}
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
     * @return angle between three points while current point is the center point of a circle
     */
    @SuppressWarnings("unused")
    default double calculateAngleBetween(@NotNull NodePoint p1, @NotNull NodePoint p2) {
        return GeomUtil.getAngleBetween(this, p1, p2);
    }

    @NotNull
    static NodePoint of(double x, double y) {
        return new ImmutableNodePoint(x, y);
    }

    @Nullable
    @Contract("null->null")
    static NodePoint of(@Nullable Point point) {
        if (point == null) {
            return null;
        }
        return NodePoint.of(point.getX(), point.getY());
    }

    /**
     * Helper method to hide warnings. Casts a {@link Set} of instances implementing {@link NodePoint} to a {@link Set}
     * of {@link NodePoint}s.
     */
    @Contract(pure = true)
    @SuppressWarnings("unchecked")
    static <N extends NodePoint> Set<NodePoint> cast(Set<N> col) {
        return (Set<NodePoint>) col;
    }

}
