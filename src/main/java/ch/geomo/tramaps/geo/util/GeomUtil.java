package ch.geomo.tramaps.geo.util;

import ch.geomo.util.point.NodePoint;
import com.vividsolutions.jts.geom.*;
import com.vividsolutions.jts.operation.buffer.BufferBuilder;
import com.vividsolutions.jts.operation.buffer.BufferParameters;
import org.geotools.geometry.jts.JTSFactoryFinder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class GeomUtil {

    private GeomUtil() {
    }

    public static Polygon createBuffer(Geometry geom, double distance, boolean useSquareEndCap) {
        if (useSquareEndCap) {
            return (Polygon) geom.buffer(distance, BufferParameters.DEFAULT_QUADRANT_SEGMENTS, BufferParameters.CAP_SQUARE);
        }
        return (Polygon) geom.buffer(distance);
    }

    public static Point createPoint(@NotNull Coordinate coordinate) {
        return JTSFactoryFinder.getGeometryFactory().createPoint(coordinate);
    }

    @NotNull
    public static Point createPoint(@NotNull Geometry geometry) {
        return JTSFactoryFinder.getGeometryFactory().createPoint(geometry.getCoordinate());
    }

    @NotNull
    @SafeVarargs
    public static GeometryCollection createCollection(@NotNull Collection<Geometry>... collections) {
        Collection<Geometry> merged = Stream.of(collections)
                .flatMap(Collection::stream)
                .collect(Collectors.toSet());
        return JTSFactoryFinder.getGeometryFactory().createGeometryCollection(merged.toArray(new Geometry[]{}));
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
        return createLineString(nodeA.getCoordinate(), nodeB.getCoordinate());
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

    public static double getAngleToXAxis(@NotNull LineString lineString) {
        // TODO
        return 0;
    }
}
