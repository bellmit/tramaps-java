package ch.geomo.tramaps.graph;

import ch.geomo.tramaps.map.SquareStationSignature;
import ch.geomo.tramaps.map.StationSignature;
import ch.geomo.util.tuple.Pair;
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

    /**
     *
     */
    @NotNull
    private Set<Geometry> getEdgeGeometries() {
        return edges.stream()
            .map(Edge::getLineString)
            .collect(Collectors.toSet());
    }

    /**
     * Collects signature geometries of all stations/nodes.
     */
    @NotNull
    private Set<Geometry> getNodeSignatureGeometries() {
        return nodes.stream()
                .map(Node::getSignature)
                .map(StationSignature::getGeometry)
                .collect(Collectors.toSet());
    }

    @NotNull
    public Set<Edge> getEdges() {
        return edges;
    }

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

    @NotNull
    public Envelope getBoundingBox() {
        GeometryCollection geometryCollection = createCollection(getEdgeGeometries(), getNodeSignatureGeometries());
        return geometryCollection.getEnvelopeInternal();
    }

}
