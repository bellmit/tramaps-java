/*
 * Copyright (c) 2016 by Thomas Zuberbühler
 */

package ch.geomo.tramaps.util.point;

import ch.geomo.tramaps.util.GeomUtil;
import ch.geomo.tramaps.graph.Quadrant;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Point;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public interface NodePoint {

    /**
     * Get X coordinate.
     */
    double getX();

    /**
     * Gets Y coordinate.
     */
    double getY();

    Coordinate getCoordinate();

    /**
     * Calculates the distance between this point and the other given point.
     */
    default double calculateDistanceTo(@NotNull NodePoint point) {
        return calculateDistanceTo(point.getX(), point.getY());
    }

    default double calculateDistanceTo(double x, double y) {
        return calculateDistanceTo(new Coordinate(x, y));
    }

    default double calculateDistanceTo(Coordinate coordinate) {
        return getCoordinate().distance(coordinate);
    }

    /**
     * Calculates the angle between three points while current point is the center point of a circle.
     */
    default double calculateAngleBetween(@NotNull NodePoint p1, @NotNull NodePoint p2) {
        return GeomUtil.getAngleBetween(this, p1, p2);
    }

    @NotNull
    default Quadrant getQuadrant(@NotNull NodePoint originPoint) {
        return Quadrant.getQuadrant(this, originPoint);
    }

    @NotNull
    static NodePoint of(double x, double y) {
        return new ImmutableNodePoint(x, y);
    }

    @NotNull
    static NodePoint of(Point point) {
        return NodePoint.of(point.getX(), point.getY());
    }

    /**
     * Casts a {@link Set} of instances implementing {@link NodePoint} to a {@link Set} of {@link NodePoint}s.
     */
    @Contract(pure = true)
    @SuppressWarnings("unchecked")
    static <N extends NodePoint>Set<NodePoint> cast(Set<N> col) {
        return (Set<NodePoint>)col;
    }

    Point getPoint();

}
