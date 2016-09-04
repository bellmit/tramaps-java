/*
 * Copyright (c) 2016 Thomas Zuberbuehler. All rights reserved.
 */

package ch.geomo.util.point;

import ch.geomo.tramaps.graph.Node;

import java.util.Comparator;

/**
 * Compares two {@link NodePoint} to their distance to an origin {@link NodePoint}. Closest point first,
 * farthest point last.
 */
public class NodePointDistanceComparator<P extends NodePoint> implements Comparator<P> {

    private NodePoint originPoint;

    public NodePointDistanceComparator() {
        this.originPoint = NodePoint.of(0, 0);
    }

    public NodePointDistanceComparator(P originPoint) {
        this.originPoint = originPoint;
    }

    /**
     * Returns a positive int value if first point's distance to the origin point is less
     * than the distance of the second point to the origin point. Otherwise a negative
     * int value will be returned. If both points are equals, first point is considered
     * closer to the origin point.
     */
    @Override
    @SuppressWarnings("unchecked") // -> safe raw type usage
    public int compare(P p1, P p2) {
        double diff = p1.calculateDistanceTo(originPoint) - p2.calculateDistanceTo(originPoint);
        if (diff > 0) {
            return 1;
        }
        return (diff < 0) ? -1 : 0;
    }

}
