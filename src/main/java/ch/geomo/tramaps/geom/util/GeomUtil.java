/*
 * Copyright (c) 2016 Thomas Zuberbuehler. All rights reserved.
 */

package ch.geomo.tramaps.geom.util;

import ch.geomo.util.point.NodePoint;
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
 * Provides helper methods for creating and manipulating geometries. It's a Singleton implementation.
 */
public enum GeomUtil {

    // DEFAULT(new PrecisionModel()),
    TRAMAPS(new PrecisionModel(100));

    private final PrecisionModel precisionModel;
    private final GeometryFactory geometryFactory;

    GeomUtil(@NotNull PrecisionModel precisionModel) {
        geometryFactory = new GeometryFactory(precisionModel);
        this.precisionModel = precisionModel;
    }

    @NotNull
    public static GeomUtil getGeomUtil() {
        return TRAMAPS;
    }

    @NotNull
    public GeometryFactory getGeometryFactory() {
        return geometryFactory;
    }

    @NotNull
    public PrecisionModel getPrecisionModel() {
        return precisionModel;
    }

    /**
     * @return the same instance of coordinate but made precise
     */
    @NotNull
    public Coordinate makePrecise(@NotNull Coordinate coordinate) {
        getPrecisionModel().makePrecise(coordinate);
        return coordinate;
    }

    /**
     * @return the same instance of coordinate but made precise
     */
    @NotNull
    public double makePrecise(@NotNull double value) {
        return getPrecisionModel().makePrecise(value);
    }

    @NotNull
    public Polygon createBuffer(@NotNull Geometry geom, double distance, boolean useSquareEndCap) {
        if (useSquareEndCap) {
            return (Polygon) geom.buffer(distance, BufferParameters.DEFAULT_QUADRANT_SEGMENTS, BufferParameters.CAP_SQUARE);
        }
        return (Polygon) geom.buffer(distance);
    }

    @NotNull
    public Coordinate createCoordinate(double x, double y) {
        return makePrecise(new Coordinate(x, y));
    }

    @Nullable
    @Contract("null->null")
    public Coordinate createCoordinate(@Nullable Coordinate coordinate) {
        if (coordinate == null) {
            return null;
        }
        // use copy-constructor in order to modify and return a new instance
        return makePrecise(new Coordinate(coordinate));
    }

    /**
     * @return a polygon with given centroid, width and height
     */
    @NotNull
    public Polygon createPolygon(@NotNull Point centroid, double width, double height) {
        Coordinate a = createCoordinate(centroid.getX() - width / 2, centroid.getY() - height / 2);
        Coordinate b = createCoordinate(centroid.getX() - width / 2, centroid.getY() + height / 2);
        Coordinate c = createCoordinate(centroid.getX() + width / 2, centroid.getY() + height / 2);
        Coordinate d = createCoordinate(centroid.getX() + width / 2, centroid.getY() - height / 2);
        return geometryFactory.createPolygon(new Coordinate[]{a, b, c, d, a});
    }

    /**
     * @return a point with given x- and y-values
     */
    @NotNull
    public Point createPoint(double x, double y) {
        return geometryFactory.createPoint(createCoordinate(x, y));
    }

    /**
     * @return a point with given coordinate
     */
    @NotNull
    public Point createPoint(@NotNull Coordinate coordinate) {
        return geometryFactory.createPoint(createCoordinate(coordinate));
    }

    @NotNull
    public Point createPoint(@NotNull Geometry geometry) {
        return geometryFactory.createPoint(geometry.getCoordinate());
    }

    /**
     * @return a new instance of {@link Point} with x- and y-values of given {@link Point}
     */
    @NotNull
    public Point clonePoint(@NotNull Point point) {
        return createPoint(point.getX(), point.getY());
    }

    /**
     * @return an instance of {@link GeometryCollection} with geometries provided by given {@link Stream}
     */
    @NotNull
    public GeometryCollection createCollection(@NotNull Stream<? extends Geometry> stream) {
        return geometryFactory.createGeometryCollection(stream.toArray(Geometry[]::new));
    }

    /**
     * @return an instance of {@link GeometryCollection} with values of given {@link Collection}s
     */
    @NotNull
    @SafeVarargs
    public final GeometryCollection createCollection(@NotNull Collection<? extends Geometry>... collections) {
        Collection<Geometry> merged = Stream.of(collections)
                .flatMap(Collection::stream)
                .collect(Collectors.toSet());
        return geometryFactory.createGeometryCollection(merged.toArray(new Geometry[]{}));
    }

    @NotNull
    public Stream<Geometry> toStream(@NotNull GeometryCollection collection) {
        List<Geometry> output = new ArrayList<>();
        for (int i = 0; i < collection.getNumGeometries(); i++) {
            output.add(collection.getGeometryN(i));
        }
        return output.stream();
    }

    @NotNull
    public LineString createLineString(double x1, double y1, double x2, double y2) {
        return geometryFactory.createLineString(new Coordinate[]{createCoordinate(x1, y2), createCoordinate(x2, y2)});
    }

    @NotNull
    public LineString createLineString(@Nullable Coordinate... points) {
        if (points == null) {
            return geometryFactory.createLineString((Coordinate[]) null);
        }
        return geometryFactory.createLineString(Stream.of(points)
                .map(this::createCoordinate)
                .toArray(Coordinate[]::new));
    }

    @NotNull
    public LineString createLineString(@NotNull Point pointA, @NotNull Point pointB) {
        return createLineString(pointA.getCoordinate(), pointB.getCoordinate());
    }

    @NotNull
    public LineString createLineString(@NotNull NodePoint nodeA, @NotNull NodePoint nodeB) {
        return createLineString(nodeA.toCoordinate(), nodeB.toCoordinate());
    }

    /**
     * Returns the angle between two adjacent lines connecting p1 and p2 with circleCenterPoint.
     */
    public double getAngleBetween(@NotNull NodePoint circleCenterPoint, @NotNull NodePoint p1, @NotNull NodePoint p2) {
        double angle1 = Math.atan2(p1.getY() - circleCenterPoint.getY(), p1.getX() - circleCenterPoint.getX());
        double angle2 = Math.atan2(p2.getY() - circleCenterPoint.getY(), p2.getX() - circleCenterPoint.getX());
        return angle1 - angle2;
    }

    public double getAngleBetweenAsDegree(@NotNull NodePoint circleCenterPoint, @NotNull NodePoint p1, @NotNull NodePoint p2) {
        double angle = getAngleBetween(circleCenterPoint, p1, p2);
        return Math.toDegrees(angle);
    }

}
