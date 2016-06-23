/*
 * Copyright (c) 2016 by Thomas Zuberbühler
 */

package ch.geomo.tramaps.geotools;

import ch.geomo.tramaps.grid.GridEdge;
import ch.geomo.tramaps.grid.GridGraph;
import ch.geomo.tramaps.grid.GridNode;
import com.vividsolutions.jts.geom.Point;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.geotools.graph.build.GraphBuilder;
import org.geotools.graph.structure.Edge;
import org.geotools.graph.structure.Graph;
import org.geotools.graph.structure.Node;
import org.opengis.feature.simple.SimpleFeature;

import java.util.*;

public class TramapsGraphBuilder implements GraphBuilder {

    private Map<Point, SimpleFeature> stations;

    private Set<GridNode> nodes;
    private Set<GridEdge> edges;

    public TramapsGraphBuilder(SimpleFeatureCollection stations) {
        nodes = new HashSet<>();
        edges = new HashSet<>();
        this.stations = new HashMap<>();
        try (FeatureIterator<SimpleFeature> iterator = stations.features()) {
            while (iterator.hasNext()) {
                SimpleFeature feature = iterator.next();
                Point point = (Point) feature.getDefaultGeometry();
                this.stations.put(point, feature);
            }
        }
        System.out.println(this.stations.keySet());
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
        GridNode gridNode = (GridNode) node;
        stations.entrySet().stream()
                .filter(entry -> gridNode.calculateDistanceTo(entry.getKey().getCoordinate()) < 10)
                .sorted((e1, e2) -> (int) (gridNode.calculateDistanceTo(e1.getKey().getCoordinate()) - gridNode.calculateDistanceTo(e2.getKey().getCoordinate())) * 10)
                .findFirst()
                .ifPresent(entry -> {
                    gridNode.setLabelName((String)entry.getValue().getAttribute("name"));
                    gridNode.setType((int)entry.getValue().getAttribute("type"));
                });
        nodes.add(gridNode);
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
        GridNode gridNode = (GridNode)node;
        nodes.remove(gridNode);
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
        GridEdge gridEdge = (GridEdge)edge;
        edges.remove(gridEdge);
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
