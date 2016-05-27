/*
 * Copyright (c) 2016 by Thomas Zuberbühler
 */

package ch.geomo.tramaps;

import ch.geomo.tramaps.graph.Graph;
import ch.geomo.tramaps.grid.GridEdge;
import ch.geomo.tramaps.grid.GridGraph;
import ch.geomo.tramaps.grid.GridNode;

import java.util.Set;

public class MetroMap implements Graph {

    private GridGraph graph;

    public MetroMap(GridGraph graph) {
        this.graph = graph;
    }

    @Override
    public Set<GridNode> getNodes() {
        return graph.getNodes();
    }

    @Override
    public Set<GridEdge> getEdges() {
        return graph.getEdges();
    }

}
