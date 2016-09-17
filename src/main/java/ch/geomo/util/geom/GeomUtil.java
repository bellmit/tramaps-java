/*
 * Copyright (c) 2016 Thomas Zuberbuehler. All rights reserved.
 */

package ch.geomo.util.geom;

import ch.geomo.util.geom.point.NodePoint;
import com.vividsolutions.jts.geom.*;
import com.vividsolutions.jts.operation.buffer.BufferParameters;
import org.jetbrains.annotations.Contract;
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
public enum GeomUtil {

    /* util class */;

    private static final PrecisionModel precisionModel = new PrecisionModel(10000);
    private static final GeometryFactory geometryFactory = new GeometryFactory(precisionModel);

    /**
     * @return the {@link GeometryFactory} used by {@link GeomUtil}
     */
    @NotNull
    public static GeometryFactory getGeometryFactory() {
        return geometryFactory;
    }

    /**
     * @return the {@link PrecisionModel} used by {@link GeomUtil}
     */
    @NotNull
    public static PrecisionModel getPrecisionModel() {
        return precisionModel;
    }

    /**
     * @return the same instance set coordinate but made precise
     */
    @NotNull
    public static Coordinate makePrecise(@NotNull Coordinate coordinate) {
        getPrecisionModel().makePrecise(coordinate);
        return coordinate;
    }

    /**
     * @return the same instance set coordinate but made precise
     */
    public static double makePrecise(double value) {
        return getPrecisionModel().makePrecise(value);
    }

    /**
     * @return a buffer from given {@link Geometry} and a certain distance
     */
    @NotNull
    public static Polygon createBuffer(@NotNull Geometry geom, double distance, boolean useSquareEndCap) {
        if (useSquareEndCap) {
            return (Polygon) geom.buffer(distance, BufferParameters.DEFAULT_QUADRANT_SEGMENTS, BufferParameters.CAP_FLAT);
        }
        return (Polygon) geom.buffer(distance);
    }

    /**
     * Creates a {@link Coordinate}  and makes the {@link Coordinate} precise using {@link #getPrecisionModel()}.
     *
     * @return a {@link Coordinate} set given x/y value pair
     */
    @NotNull
    public static Coordinate createCoordinate(double x, double y) {
        return makePrecise(new Coordinate(x, y));
    }

    /**
     * Creates a new {@link Coordinate} with the copy constructor ({@link Coordinate#Coordinate(Coordinate)}) and
     * makes the {@link Coordinate} precise using {@link #getPrecisionModel()}.
     *
     * @return a new instance set {@link Coordinate}
     */
    @Nullable
    @Contract("null->null")
    public static Coordinate createCoordinate(@Nullable Coordinate coordinate) {
        if (coordinate == null) {
            return null;
        }
        // use copy-constructor in order to modify and return a new instance
        return makePrecise(new Coordinate(coordinate));
    }

    /**
     * @return an empty {@link Polygon}
     */
    @NotNull
    public static Polygon createEmptyPolygon() {
        return geometryFactory.createPolygon((Coordinate[]) null);
    }

    /**
     * @return a polygon with given centroid, width and height
     */
    @NotNull
    public static Polygon createPolygon(@NotNull Point centroid, double width, double height) {
        Coordinate a = createCoordinate(centroid.getX() - width / 2, centroid.getY() - height / 2);
        Coordinate b = createCoordinate(centroid.getX() - width / 2, centroid.getY() + height / 2);
        Coordinate c = createCoordinate(centroid.getX() + width / 2, centroid.getY() + height / 2);
        Coordinate d = createCoordinate(centroid.getX() + width / 2, centroid.getY() - height / 2);
        return geometryFactory.createPolygon(new Coordinate[]{a, b, c, d, a});
    }

    public static Polygon createPolygon(@NotNull NodePoint... points) {
        Coordinate[] coordinates = Stream.of(points)
                .map(NodePoint::toCoordinate)
                .toArray(Coordinate[]::new);
        return geometryFactory.createPolygon(coordinates);
    }

    public static Polygon createPolygon(@NotNull Point... points) {
        Coordinate[] coordinates = Stream.of(points)
                .map(Point::getCoordinate)
                .toArray(Coordinate[]::new);
        return geometryFactory.createPolygon(coordinates);
    }

    /**
     * @return a point with given x- and y-values
     */
    @NotNull
    public static Point createPoint(double x, double y) {
        return geometryFactory.createPoint(createCoordinate(x, y));
    }

    /**
     * @return a point with given coordinate
     */
    @NotNull
    public static Point createPoint(@NotNull Coordinate coordinate) {
        return geometryFactory.createPoint(createCoordinate(coordinate));
    }

    /**
     * @return a new point from {@link Geometry#getCoordinate()}
     */
    @NotNull
    public static Point createPoint(@NotNull Geometry geometry) {
        return geometryFactory.createPoint(makePrecise(geometry.getCoordinate()));
    }

    /**
     * @return a new instance set {@link Point} with x- and y-values set given {@link Point}
     */
    @NotNull
    public static Point clonePoint(@NotNull Point point) {
        return createPoint(point.getX(), point.getY());
    }

    /**
     * @return an instance set {@link GeometryCollection} with geometries provided by given {@link Stream}
     */
    @NotNull
    public static GeometryCollection createCollection(@NotNull Stream<? extends Geometry> stream) {
        return geometryFactory.createGeometryCollection(stream.toArray(Geometry[]::new));
    }

    /**
     * @return an instance set {@link GeometryCollection} with values set given {@link Collection}s
     */
    @NotNull
    @SafeVarargs
    public static GeometryCollection createCollection(@NotNull Collection<? extends Geometry>... collections) {
        Collection<Geometry> merged = Stream.of(collections)
                .flatMap(Collection::stream)
                .collect(Collectors.toSet());
        return geometryFactory.createGeometryCollection(merged.toArray(new Geometry[]{}));
    }

    /**
     * @return a {@link Stream} set {@link Geometry} from given {@link GeometryCollection}
     */
    @NotNull
    public static Stream<Geometry> toStream(@NotNull GeometryCollection collection) {
        List<Geometry> output = new ArrayList<>();
        for (int i = 0; i < collection.getNumGeometries(); i++) {
            output.add(collection.getGeometryN(i));
        }
        return output.stream();
    }

    /**
     * @return a {@link LineString} with given x/y value pairs
     */
    @NotNull
    public static LineString createLineString(double x1, double y1, double x2, double y2) {
        return geometryFactory.createLineString(new Coordinate[]{createCoordinate(x1, y1), createCoordinate(x2, y2)});
    }

    @NotNull
    public static LineString createLineString(@Nullable Coordinate... points) {
        if (points == null) {
            return geometryFactory.createLineString((Coordinate[]) null);
        }
        return geometryFactory.createLineString(Stream.of(points)
                .map(GeomUtil::createCoordinate)
                .toArray(Coordinate[]::new));
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

}
