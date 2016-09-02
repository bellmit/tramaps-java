/*
 * Copyright (c) 2016 Thomas Zuberbuehler. All rights reserved.
 */

package ch.geomo.tramaps.geo.util;

import ch.geomo.util.point.NodePoint;
import com.vividsolutions.jts.geom.*;
import com.vividsolutions.jts.operation.buffer.BufferParameters;
import org.geotools.geometry.jts.JTSFactoryFinder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Provides helper methods for creating and manipulating geometries.
 */
public final class GeomUtil {

    private GeomUtil() {
    }

    @NotNull
    public static Polygon createBuffer(@NotNull Geometry geom, double distance, boolean useSquareEndCap) {
        if (useSquareEndCap) {
            return (Polygon) geom.buffer(distance, BufferParameters.DEFAULT_QUADRANT_SEGMENTS, BufferParameters.CAP_SQUARE);
        }
        return (Polygon) geom.buffer(distance);
    }

    /**
     * @return a polygon with given centroid, width and height
     */
    @NotNull
    public static Polygon createPolygon(@NotNull Point centroid, double width, double height) {
        Coordinate a = new Coordinate(centroid.getX() - width / 2, centroid.getY() - height / 2);
        Coordinate b = new Coordinate(centroid.getX() - width / 2, centroid.getY() + height / 2);
        Coordinate c = new Coordinate(centroid.getX() + width / 2, centroid.getY() + height / 2);
        Coordinate d = new Coordinate(centroid.getX() + width / 2, centroid.getY() - height / 2);
        return JTSFactoryFinder.getGeometryFactory().createPolygon(new Coordinate[]{a, b, c, d, a});
    }

    /**
     * @return a point with given x- and y-values
     */
    @NotNull
    public static Point createPoint(double x, double y) {
        return JTSFactoryFinder.getGeometryFactory().createPoint(new Coordinate(x, y));
    }

    /**
     * @return a point with given coordinate
     */
    @NotNull
    public static Point createPoint(@NotNull Coordinate coordinate) {
        return JTSFactoryFinder.getGeometryFactory().createPoint(coordinate);
    }

    @NotNull
    public static Point createPoint(@NotNull Geometry geometry) {
        return JTSFactoryFinder.getGeometryFactory().createPoint(geometry.getCoordinate());
    }

    /**
     * @return a new instance of {@link Point} with x- and y-values of given {@link Point}
     */
    @NotNull
    public static Point clonePoint(@NotNull Point point) {
        return createPoint(point.getX(), point.getY());
    }

    /**
     * @return an instance of {@link GeometryCollection} with geometries provided by given {@link Stream}
     */
    @NotNull
    public static GeometryCollection createCollection(@NotNull Stream<? extends Geometry> stream) {
        return JTSFactoryFinder.getGeometryFactory().createGeometryCollection(stream.toArray(Geometry[]::new));
    }

    /**
     * @return an instance of {@link GeometryCollection} with values of given {@link Collection}s
     */
    @NotNull
    @SafeVarargs
    public static GeometryCollection createCollection(@NotNull Collection<? extends Geometry>... collections) {
        Collection<Geometry> merged = Stream.of(collections)
                .flatMap(Collection::stream)
                .collect(Collectors.toSet());
        return JTSFactoryFinder.getGeometryFactory().createGeometryCollection(merged.toArray(new Geometry[]{}));
    }

    @NotNull
    public static Stream<Geometry> toStream(@NotNull GeometryCollection collection) {
        List<Geometry> output = new ArrayList<>();
        for (int i = 0; i < collection.getNumGeometries(); i++) {
            output.add(collection.getGeometryN(i));
        }
        return output.stream();
    }

    @NotNull
    public static LineString createLineString(@Nullable Coordinate... points) {
        return JTSFactoryFinder.getGeometryFactory().createLineString(points);
    }

    @NotNull
    public static LineString createLineString(@NotNull Point pointA, @NotNull Point pointB) {
        return createLineString(pointA.getCoordinate(), pointB.getCoordinate());
    }

    @NotNull
    public static LineString createLineString(@NotNull NodePoint nodeA, @NotNull NodePoint nodeB) {
        return createLineString(nodeA.toCoordinate(), nodeB.toCoordinate());
    }

    /**
     * Returns the angle between two adjacent lines connecting p1 and p2 with circleCenterPoint.
     */
    public static double getAngleBetween(@NotNull NodePoint circleCenterPoint, @NotNull NodePoint p1, @NotNull NodePoint p2) {
        double angle1 = Math.atan2(p1.getY() - circleCenterPoint.getY(), p1.getX() - circleCenterPoint.getX());
        double angle2 = Math.atan2(p2.getY() - circleCenterPoint.getY(), p2.getX() - circleCenterPoint.getX());
        return angle1 - angle2;
    }

    public static double getAngleBetweenAsDegree(@NotNull NodePoint circleCenterPoint, @NotNull NodePoint p1, @NotNull NodePoint p2) {
        double angle = getAngleBetween(circleCenterPoint, p1, p2);
        return Math.toDegrees(angle);
    }

    public static double getAngleToXAxisAsDegree(@NotNull LineString lineString) {
        return getAngleBetweenAsDegree(NodePoint.of(lineString.getStartPoint()), NodePoint.of(lineString.getEndPoint()), NodePoint.of(lineString.getStartPoint().getX(), 5d));
    }
}
