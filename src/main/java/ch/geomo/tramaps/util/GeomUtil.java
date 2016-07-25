package ch.geomo.tramaps.util;

import ch.geomo.tramaps.util.point.NodePoint;
import com.vividsolutions.jts.geom.*;
import com.vividsolutions.jts.operation.buffer.BufferBuilder;
import com.vividsolutions.jts.operation.buffer.BufferParameters;
import org.geotools.geometry.jts.JTSFactoryFinder;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class GeomUtil {

    private GeomUtil() {
    }

    public static Polygon createBuffer(Geometry geom, double distance, boolean useFlatEndCap) {
        BufferParameters params = new BufferParameters();
        if (useFlatEndCap) {
            params.setEndCapStyle(BufferParameters.CAP_FLAT);
        }
        BufferBuilder builder = new BufferBuilder(params);
        return (Polygon) builder.buffer(geom, distance);
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
