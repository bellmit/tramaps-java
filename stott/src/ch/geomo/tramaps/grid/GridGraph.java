/*
 * Copyright (c) 2016 by Thomas Zuberbühler
 */

package ch.geomo.tramaps.grid;

import ch.geomo.tramaps.graph.Graph;
import ch.geomo.tramaps.graph.geo.GeoEdge;
import ch.geomo.tramaps.graph.geo.GeoNode;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class GridGraph implements Graph {

    private Set<GridNode> nodes;
    private Set<GridEdge> edges;

    public GridGraph(Collection<GridNode> nodes, Collection<GridEdge> edges) {
        this.nodes = new HashSet<>(nodes);
        this.edges = new HashSet<>(edges);
    }

    @Override
    public Set<GridNode> getNodes() {
        return nodes;
    }

    @Override
    public Set<GridEdge> getEdges() {
        return edges;
    }

}
