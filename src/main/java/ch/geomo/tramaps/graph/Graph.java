package ch.geomo.tramaps.graph;

import ch.geomo.tramaps.map.signature.NodeSignature;
import ch.geomo.util.pair.Pair;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryCollection;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

import static ch.geomo.tramaps.geo.util.GeomUtil.createCollection;

public class Graph {

    private Set<Edge> edges;
    private Set<Node> nodes;

    public Graph() {
        edges = new HashSet<>();
        nodes = new HashSet<>();
    }

    public Graph(@NotNull Graph graph) {
        this(graph.getEdges(), graph.getNodes());
    }

    public Graph(@NotNull Collection<Edge> edges, @NotNull Collection<Node> nodes) {
        this.edges = new HashSet<>(edges);
        this.nodes = new HashSet<>(nodes);
    }

    public void addEdges(@NotNull Edge... edges) {
        addEdges(Arrays.asList(edges));
    }

    public void removeEdges(@NotNull Edge... edges) {
        Arrays.stream(edges)
                .forEach(edge -> {
                    edge.destroy(this);
                });
    }

    public void addEdges(@NotNull Collection<Edge> edges) {
        this.edges.addAll(edges);
    }

    public void addNodes(@NotNull Node... nodes) {
        addNodes(Arrays.asList(nodes));
    }

    public void addNodes(@NotNull Collection<Node> nodes) {
        this.nodes.addAll(nodes);
    }

    @NotNull
    private Set<Geometry> getEdgeGeometries() {
        return edges.stream()
            .map(Edge::getLineString)
            .collect(Collectors.toSet());
    }

    /**
     * @return a {@link Set} of the node signature's geometries
     */
    @NotNull
    private Set<Geometry> getNodeSignatureGeometries() {
        return nodes.stream()
                .map(Node::getNodeSignature)
                .map(NodeSignature::getGeometry)
                .collect(Collectors.toSet());
    }

    /**
     * @return all edges of this graph
     */
    @NotNull
    public Set<Edge> getEdges() {
        return edges;
    }

    /**
     * @return all nodes of this graph
     */
    @NotNull
    public Set<Node> getNodes() {
        return nodes;
    }

    public Set<Node> getBridgeEdges() {
        // TODO
        return null;
    }

    public Map<Edge, Pair<Graph>> getSubGraphsByLeavingOut(Set<Edge> edges) {
        // TODO
        return null;
    }

    /**
     * @return a bounding box of all edge and node signatures
     * @see #getEdgeGeometries()
     * @see #getNodeSignatureGeometries()
     */
    @NotNull
    public Envelope getBoundingBox() {
        GeometryCollection collection = createCollection(getEdgeGeometries(), getNodeSignatureGeometries());
        return collection.getEnvelopeInternal();
    }

    @NotNull
    @Override
    public String toString() {
        return "Graph: [ Edges: " + getEdges() + "\n         " + "Nodes: " + getNodes() + " ]";
    }

}
