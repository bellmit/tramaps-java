/*
 * Copyright (c) 2016 by Thomas Zuberbühler
 */

package ch.geomo.tramaps.geom;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.Point;
import org.geotools.geometry.jts.JTSFactoryFinder;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class GeomUtil {

    private GeomUtil() {
    }

    public static boolean adjacent(@NotNull LineString l1, @NotNull LineString l2) {
        return l1.getEndPoint().equals(l2.getEndPoint())
                || l1.getEndPoint().equals(l2.getStartPoint())
                || l2.getEndPoint().equals(l1.getStartPoint());
    }

    @Contract("_,null->null")
    public static Point getOtherPoint(@NotNull LineString l, @Nullable Point point) {
        if (l.getEndPoint().equals(point)) {
            return l.getStartPoint();
        }
        if (l.getStartPoint().equals(point)) {
            return l.getEndPoint();
        }
        return null;
    }

    /**
     * @see JTSFactoryFinder#getGeometryFactory()
     * @see com.vividsolutions.jts.geom.GeometryFactory#createLineString(Coordinate[])
     */
    @NotNull
    public static LineString createLineString(@Nullable Coordinate... points) {
        return JTSFactoryFinder.getGeometryFactory().createLineString(points);
    }

    /**
     * @see #createLineString(Coordinate...)
     */
    @NotNull
    public static LineString createLineString(@NotNull Point pointA, @NotNull Point pointB) {
        return createLineString(pointA.getCoordinate(), pointB.getCoordinate());
    }

    /**
     * @see #createLineString(Coordinate...)
     */
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

}
