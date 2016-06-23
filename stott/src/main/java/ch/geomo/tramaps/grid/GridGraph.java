/*
 * Copyright (c) 2016 by Thomas Zuberbühler
 */

package ch.geomo.tramaps.grid;

import ch.geomo.tramaps.util.CollectionUtil;
import ch.geomo.tramaps.util.tuple.Tuple;
import org.geotools.graph.structure.Graph;
import org.geotools.graph.structure.GraphVisitor;
import org.geotools.graph.structure.Graphable;

import java.util.*;
import java.util.stream.Collectors;

public class GridGraph implements Graph {

    private Set<GridNode> nodes;
    private Set<GridEdge> edges;

    private long spacing;
    private int searchDistance;

    private Set<Tuple<GridEdge>> edgePairs;

    public GridGraph(Collection<GridNode> nodes, Collection<GridEdge> edges) {
        this(nodes, edges, 100, 2);
    }

    public GridGraph(Collection<GridNode> nodes, Collection<GridEdge> edges, long spacing, int searchDistance) {
        this.nodes = new HashSet<>(nodes);
        this.edges = new HashSet<>(edges);
        this.spacing = spacing;
        this.searchDistance = searchDistance;
        this.prepareEdgePairs();
    }

    private boolean intersects(Tuple<GridEdge> tuple, double bufferDistance) {
        return tuple.get(0).intersectsWithBuffer(tuple.get(1), bufferDistance);
    }

    /**
     * Prepares derived data for further usage.
     */
    private void prepareEdgePairs() {
        edgePairs = CollectionUtil.makePermutations(edges, true).parallelStream()
                // skip all pairs not within a certain buffer distance
                .filter(p -> this.intersects(p, spacing * searchDistance * 1.5))
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

    @Override
    public List queryNodes(GraphVisitor visitor) {
        return query(getNodes(), visitor);
    }

    @Override
    public List queryEdges(GraphVisitor visitor) {
        return query(getEdges(), visitor);
    }

    @Override
    public void visitNodes(GraphVisitor visitor) {
        nodes.forEach(visitor::visit);
    }

    @Override
    public void visitEdges(GraphVisitor visitor) {
        edges.forEach(visitor::visit);
    }

    @Override
    public List<GridNode> getNodesOfDegree(int degree) {
        return nodes.parallelStream()
                .filter(node -> node.hasDegree(degree))
                .collect(Collectors.toList());
    }

    @Override
    public List<GridNode> getVisitedNodes(boolean visited) {
        return getVisited(nodes, visited);
    }

    @Override
    public List<GridEdge> getVisitedEdges(boolean visited) {
        return getVisited(edges, visited);
    }

    public Set<Tuple<GridEdge>> getEdgePairs() {
        return Collections.unmodifiableSet(edgePairs);
    }

    private <G extends Graphable> List<G> query(Collection<G> components, GraphVisitor visitor) {
        List<G> result = new ArrayList<>();
        for (G component : components) {
            switch (visitor.visit(component)) {
                case PASS_AND_STOP: {
                    result.add(component);
                    return result;
                }
                case PASS_AND_CONTINUE: {
                    result.add(component);
                }
            }
        }
        return result;
    }

    @Override
    public String toString() {
        return "Nodes: " + nodes.toString() + "\nEdges: " + edges.toString();
    }

    private <G extends Graphable> List<G> getVisited(Collection<G> components, boolean visited) {
        GraphVisitor visitor = component -> component.isVisited() == visited ? PASS_AND_CONTINUE : FAIL_QUERY;
        return query(components, visitor);
    }

}
