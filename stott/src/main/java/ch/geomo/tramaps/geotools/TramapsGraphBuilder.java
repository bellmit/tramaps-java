/*
 * Copyright (c) 2016 by Thomas Zuberbühler
 */

package ch.geomo.tramaps.geotools;

import ch.geomo.tramaps.grid.GridEdge;
import ch.geomo.tramaps.grid.GridGraph;
import ch.geomo.tramaps.grid.GridNode;
import org.geotools.graph.build.GraphBuilder;
import org.geotools.graph.structure.Edge;
import org.geotools.graph.structure.Graph;
import org.geotools.graph.structure.Node;

import java.util.*;

public class TramapsGraphBuilder implements GraphBuilder {

    private Set<GridNode> nodes;
    private Set<GridEdge> edges;

    public TramapsGraphBuilder() {
        nodes = new HashSet<>();
        edges = new HashSet<>();
    }

    protected GridGraph buildGraph() {
        return new GridGraph(nodes, edges);
    }

    @Override
    public GridGraph getGraph() {
        return buildGraph();
    }

    @Override
    public GridNode buildNode() {
        return new GridNode();
    }

    @Override
    public GridEdge buildEdge(Node firstNode, Node secondNode) {
        return new GridEdge((GridNode) firstNode, (GridNode) secondNode);
    }

    @Override
    public void addNode(Node node) {
        nodes.add((GridNode) node);
    }

    @Override
    public void addEdge(Edge edge) {
        GridEdge e = ((GridEdge) edge);
        e.getNodeA().add(e);
        if (!e.isLoop()) {
            edge.getNodeB().add(edge);
        }
        edges.add(e);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void removeNode(Node node) {
        List edges = new ArrayList(node.getEdges());
        removeEdges(edges);
        nodes.remove(node);
    }

    @Override
    public void removeNodes(Collection nodes) {
        for (Object node : nodes) {
            removeNode((GridNode) node);
        }
    }

    @Override
    public void removeEdge(Edge edge) {
        edge.getNodeA().remove(edge);
        edge.getNodeB().remove(edge);
        edges.remove(edge);
    }

    @Override
    public void removeEdges(Collection edges) {
        for (Object edge : edges) {
            removeEdge((GridEdge) edge);
        }
    }

    @Override
    public Object clone(boolean deep) throws Exception {
        GraphBuilder builder = getClass().newInstance();
        if (deep) {
            builder.importGraph(getGraph());
        }
        return builder;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void importGraph(Graph g) {
        nodes = new HashSet<>(g.getNodes());
        edges = new HashSet<>(g.getEdges());
    }

}
