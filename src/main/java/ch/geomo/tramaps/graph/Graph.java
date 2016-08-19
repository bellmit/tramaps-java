package ch.geomo.tramaps.graph;

import ch.geomo.tramaps.geo.util.GeomUtil;
import ch.geomo.tramaps.util.CollectionUtil;
import ch.geomo.util.tuple.Tuple;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryCollection;
import org.geotools.geometry.jts.JTSFactoryFinder;
import org.jetbrains.annotations.NotNull;
import org.opengis.geometry.BoundingBox;

import java.util.*;
import java.util.stream.Collectors;

public class Graph {

    private Set<Edge> edges;
    private Set<Node> nodes;

    public Graph() {
        this(new HashSet<>(), new HashSet<>());
    }

    public Graph(@NotNull Set<Edge> edges, @NotNull Set<Node> nodes) {
        this.edges = edges;
        this.nodes = nodes;
    }

    @NotNull
    private Set<Geometry> getEdgeGeometries() {
        return edges.stream()
            .map(Edge::getLineString)
            .collect(Collectors.toSet());
    }

    @NotNull
    private Set<Geometry> getNodeGeometries() {
        return edges.stream()
                .map(Edge::getLineString)
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

    public Map<Edge, Tuple<Graph>> getSubGraphsByLeavingOut(Set<Edge> edges) {
        // TODO
        return null;
    }

    public Envelope getBoundingBox() {
        GeometryCollection geometryCollection = GeomUtil.createCollection(getEdgeGeometries(), getNodeGeometries());
        return geometryCollection.getEnvelopeInternal();
    }

}
