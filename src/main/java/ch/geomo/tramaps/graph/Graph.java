/*
 * Copyright (c) 2016 Thomas Zuberbuehler. All rights reserved.
 */

package ch.geomo.tramaps.graph;

import ch.geomo.tramaps.geom.util.GeomUtil;
import ch.geomo.tramaps.map.signature.NodeSignature;
import ch.geomo.util.collection.set.EnhancedSet;
import ch.geomo.util.collection.set.GSet;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

public class Graph {

    private final EnhancedSet<Node> nodes;

    private EnhancedSet<Edge> edgeCache;

    public Graph() {
        nodes = GSet.emptySet();
    }

    private void clearCache() {
        edgeCache = null;
    }

    private void buildCache() {
        if (edgeCache == null) {
            edgeCache = nodes.flatMap(node -> node.getAdjacentEdges().stream());
        }
    }

    public void addNodes(@NotNull Node... nodes) {
        this.nodes.addElements(nodes);
        clearCache();
    }

    @NotNull
    private EnhancedSet<Geometry> getEdgeGeometries() {
        return GSet.createSet(getEdgeCache().map(Edge::getLineString));
    }

    @NotNull
    private EnhancedSet<Edge> getEdgeCache() {
        buildCache();
        return edgeCache;
    }

    @NotNull
    private EnhancedSet<Geometry> getNodeSignatureGeometries() {
        return GSet.createSet(nodes.stream()
                .map(Node::getNodeSignature)
                .map(NodeSignature::getGeometry));
    }

    @NotNull
    public EnhancedSet<Edge> getEdges() {
        return GSet.createSet(getEdgeCache());
    }

    @NotNull
    public EnhancedSet<Node> getNodes() {
        return GSet.createSet(nodes);
    }

    /**
     * Calculates the bounding box with a collection set all edge and node signature geometries.
     *
     * @return a bounding box set all edge and node signatures
     * @see #getEdgeGeometries()
     * @see #getNodeSignatureGeometries()
     */
    @NotNull
    public Envelope getBoundingBox() {
        return GeomUtil
                .createCollection(getEdgeGeometries(), getNodeSignatureGeometries())
                .getEnvelopeInternal();
    }

    @NotNull
    @Override
    public String toString() {
        return "Graph: [ Edges: " + getEdges() + "\n         " + "Nodes: " + getNodes() + " ]";
    }

    /**
     * Reset and flags edge cache for a rebuild. Removes deleted nodes returning true
     * when invoking {@link Node#isDeleted()}.
     */
    public void updateGraph() {
        // remove deleted nodes
        nodes.removeIf(Node::isDeleted);
        // reset edge cache -> will be created again when accessing next time
        clearCache();
    }

    /**
     * Creates a new {@link Node} and adds the node to this instance.
     *
     * @return the created node
     */
    public Node createNode(double x, double y, @Nullable String name, @NotNull Function<Node, NodeSignature> nodeSignatureFactory) {
        Node node = new Node(x, y, name, nodeSignatureFactory);
        addNodes(node);
        return node;
    }

    /**
     * Creates a new {@link Edge}.
     *
     * @return the created edge
     */
    public Edge createEdge(@NotNull Node nodeA, @NotNull Node nodeB, @NotNull Route... routes) {
        Edge edge = new Edge(nodeA, nodeB, routes);
        clearCache();
        return edge;
    }

}
