/*
 * Copyright (c) 2016 by Thomas Zuberbühler
 */

package ch.geomo.tramaps.graph;

import ch.geomo.tramaps.util.GeomUtil;
import ch.geomo.tramaps.util.point.NodePoint;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.Point;
import org.jetbrains.annotations.NotNull;

import java.util.Comparator;
import java.util.Objects;

/**
 * Compares two edges in order to sort edges counter-clockwise using the quadrants of the Cartesian coordinate
 * system.
 */
public class LineStringOrderComparator implements Comparator<LineString> {

    private Point point;

    public LineStringOrderComparator(@NotNull NodePoint point) {
        this.point = point.getPoint();
    }

    @SuppressWarnings("unused")
    public LineStringOrderComparator(@NotNull Point point) {
        this.point = point;
    }

    @Override
    public int compare(LineString l1, LineString l2) {

        if (Objects.equals(l1, l2)) {
            return 0;
        }
        if (!GeomUtil.adjacent(l1, l2)) {
            return -1;
        }

        Point p1 = GeomUtil.getOtherPoint(l1, point);
        Point p2 = GeomUtil.getOtherPoint(l2, point);

        if (Objects.equals(p1, p2)) {
            return 0;
        }

        if (p1 == null || p2 == null) {
            throw new IllegalStateException("Something went wrong...");
        }

        Quadrant q1 = Quadrant.getQuadrant(p1, point);
        Quadrant q2 = Quadrant.getQuadrant(p2, point);

        if (Objects.equals(q1, q2)) {

            if (q1.isQuadrant(1) || q1.isQuadrant(2)) {
                return (p1.getY() - p2.getY()) > 0 ? 1 : -1;
            }
            return (p2.getY() - p1.getY()) > 0 ? 1 : -1;

        }

        if (q1.isBefore(q2)) {
            return -1;
        }
        return 1;

    }

}
