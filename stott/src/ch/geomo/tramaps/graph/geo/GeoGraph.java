/*
 * Copyright (c) 2016 by Thomas Zuberbühler
 */

package ch.geomo.tramaps.graph.geo;

import ch.geomo.tramaps.graph.Graph;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class GeoGraph implements Graph {

    private Set<GeoNode> nodes;
    private Set<GeoEdge> edges;

    public GeoGraph(Collection<GeoNode> nodes, Collection<GeoEdge> edges) {
        this.nodes = new HashSet<>(nodes);
        this.edges = new HashSet<>(edges);
    }

    @Override
    public Set<GeoNode> getNodes() {
        return nodes;
    }

    @Override
    public Set<GeoEdge> getEdges() {
        return edges;
    }

}
