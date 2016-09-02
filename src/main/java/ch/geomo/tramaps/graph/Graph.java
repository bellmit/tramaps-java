/*
 * Copyright (c) 2016 Thomas Zuberbuehler. All rights reserved.
 */

package ch.geomo.tramaps.graph;

import ch.geomo.tramaps.map.signature.NodeSignature;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryCollection;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

import static ch.geomo.tramaps.geo.util.GeomUtil.createCollection;

/**
 * A container representing a graph.
 */
public class Graph {

    @NotNull
    private Set<Node> nodes;

    /**
     * Cache with all edges received from nodes. Initialized once with
     * {@link #buildEdgeCache()} when accessing. Invoke {@link #updateGraph()}
     * to flag for a rebuild when accessing next time.
     */
    private Set<Edge> edgeCache;

    /**
     * Creates a new instance of a {@link Graph} without nodes.
     */
    public Graph() {
        this(Collections.emptyList());
    }

    /**
     * Copy constructor. Creates a new instance of a {@link Graph} with the
     * nodes of the given {@link Graph}.
     */
    public Graph(@NotNull Graph graph) {
        this(graph.getNodes());
    }

    /**
     * Creates a new instance of a {@link Graph} with given edges and nodes.
     */
    public Graph(@NotNull Collection<Node> nodes) {
        this.nodes = new HashSet<>(nodes);
        edgeCache = null;
    }

    private void buildEdgeCache() {
        edgeCache = nodes.stream()
                .flatMap(node -> node.getAdjacentEdges().stream())
                .distinct()
                .collect(Collectors.toSet());
    }

    /**
     * Adds given nodes to this {@link Graph} instance.
     */
    public void addNodes(@NotNull Node... nodes) {
        addNodes(Arrays.asList(nodes));
    }

    /**
     * Adds given nodes to this {@link Graph} instance.
     */
    public void addNodes(@NotNull Collection<Node> nodes) {
        this.nodes.addAll(nodes);
        edgeCache = null;
    }

    /**
     * Removes given nodes from this {@link Graph} instance.
     */
    public void removeNodes(@NotNull Node... nodes) {
        this.nodes.removeAll(Arrays.asList(nodes));
        edgeCache = null;
    }

    /**
     * @return a {@link Set} with all edge's geometries
     */
    @NotNull
    private Set<Geometry> getEdgeGeometries() {
        return getEdgeCache().stream()
                .map(Edge::getLineString)
                .collect(Collectors.toSet());
    }

    private Set<Edge> getEdgeCache() {
        if (edgeCache == null) {
            buildEdgeCache();
        }
        return edgeCache;
    }

    /**
     * @return a {@link Set} with all node signature's geometries
     */
    @NotNull
    private Set<Geometry> getNodeSignatureGeometries() {
        return nodes.stream()
                .map(Node::getNodeSignature)
                .map(NodeSignature::getGeometry)
                .collect(Collectors.toSet());
    }

    /**
     * Gets all edges of this graph.
     *
     * @return an unmodifiable {@link Set} of all edges
     */
    @NotNull
    public Set<Edge> getEdges() {
        return Collections.unmodifiableSet(getEdgeCache());
    }

    /**
     * Gets all nodes of this graph.
     *
     * @return an unmodifiable {@link Set} of all nodes
     */
    @NotNull
    public Set<Node> getNodes() {
        return Collections.unmodifiableSet(nodes);
    }

    /**
     * Calculates the bounding box with a collection of all edge and node signature geometries.
     *
     * @return a bounding box of all edge and node signatures
     * @see #getEdgeGeometries()
     * @see #getNodeSignatureGeometries()
     */
    @NotNull
    public Envelope getBoundingBox() {
        GeometryCollection collection = createCollection(getEdgeGeometries(), getNodeSignatureGeometries());
        return collection.getEnvelopeInternal();
    }

    /**
     * @return a string representation of this graph
     */
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
        edgeCache = null;
    }

}
