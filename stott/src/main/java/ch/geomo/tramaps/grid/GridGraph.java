/*
 * Copyright (c) 2016 by Thomas Zuberbühler
 */

package ch.geomo.tramaps.grid;

import ch.geomo.tramaps.graph.Graph;
import ch.geomo.tramaps.util.CollectionUtil;
import ch.geomo.tramaps.util.tuple.Tuple;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class GridGraph implements Graph {

    private Set<GridNode> nodes;
    private Set<GridEdge> edges;

    private long gridSpace;
    private int moveRadius;

    private Set<Tuple<GridEdge>> edgePairs;

    public GridGraph(Collection<GridNode> nodes, Collection<GridEdge> edges, long gridSpace, int moveRadius) {
        this.nodes = new HashSet<>(nodes);
        this.edges = new HashSet<>(edges);
        this.gridSpace = gridSpace;
        this.moveRadius = moveRadius;
        this.prepareDerivedData();
    }

    private boolean intersects(Tuple<GridEdge> tuple, double bufferDistance) {
        return tuple.get(0).intersectsWithBuffer(tuple.get(1), bufferDistance);
    }

    /**
     * Prepares derived data for further usage.
     */
    private void prepareDerivedData() {
        edgePairs = CollectionUtil.makePermutations(edges, true).parallelStream()
                // skip all pairs not within a certain buffer distance
                .filter(p -> this.intersects(p, gridSpace * moveRadius * 1.5))
                .collect(Collectors.toSet());
    }

    @Override
    public Set<GridNode> getNodes() {
        return Collections.unmodifiableSet(nodes);
    }

    @Override
    public Set<GridEdge> getEdges() {
        return Collections.unmodifiableSet(edges);
    }

    public Set<Tuple<GridEdge>> getIntersectingEdgePairs() {
        return Collections.unmodifiableSet(edgePairs);
    }

}
