/*
 * Copyright (c) 2016 by Thomas Zuberbühler
 */

package ch.geomo.tramaps.geom;

import ch.geomo.tramaps.grid.GridNode;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.Point;
import org.geotools.geometry.jts.JTSFactoryFinder;

public final class Geom {

    private Geom() {
    }

    public static boolean adjacent(LineString l1, LineString l2) {
        return l1.getEndPoint().equals(l2.getEndPoint())
                || l1.getEndPoint().equals(l2.getStartPoint())
                || l2.getEndPoint().equals(l1.getStartPoint());
    }

    public static Point getOtherPoint(LineString l, Point point) {
        if (l.getEndPoint().equals(point)) {
            return l.getStartPoint();
        }
        if (l.getStartPoint().equals(point)) {
            return l.getEndPoint();
        }
        return null;
    }

    public static LineString createLineString(Coordinate... points) {
        return JTSFactoryFinder.getGeometryFactory().createLineString(points);
    }

    public static LineString createLineString(Point pointA, Point pointB) {
        return createLineString(pointA.getCoordinate(), pointB.getCoordinate());
    }

    public static LineString createLineString(NodePoint nodeA, NodePoint nodeB) {
        return createLineString(nodeA.getCoordinate(), nodeB.getCoordinate());
    }
}
