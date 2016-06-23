/*
 * Copyright (c) 2016 by Thomas Zuberbühler
 */

package ch.geomo.tramaps.graph;

import ch.geomo.tramaps.grid.GridEdge;
import ch.geomo.tramaps.grid.GridNode;
import org.jetbrains.annotations.NotNull;

import java.util.Comparator;

/**
 * Compares two edges in order to sort edges counter-clockwise using the quadrants of the
 * Cartesian coordinate system.
 */
public class GridEdgeOrderComparator implements Comparator<GridEdge> {

    private LineStringOrderComparator comparator;

    public GridEdgeOrderComparator(@NotNull GridNode originNode) {
        comparator = new LineStringOrderComparator(originNode);
    }

    @Override
    public int compare(GridEdge e1, GridEdge e2) {
        return comparator.compare(e1.getLineString(), e2.getLineString());
    }

}
