/*
 * Copyright (c) 2016-2018 Thomas Zuberbuehler. All rights reserved.
 */

package ch.geomo.tramaps.map.displacement.alg;

import ch.geomo.tramaps.graph.Edge;

import java.util.Comparator;

public class NonOctilinearEdgeComparator implements Comparator<Edge> {

    @Override
    public int compare(Edge e1, Edge e2) {
        if (e1.getLength() != e2.getLength()) {
            return Double.compare(e1.getLength(), e2.getLength());
        }
        // improvement required: implement better comparision
        return Double.compare(e1.getRoutes().size(), e2.getRoutes().size());
    }

}
