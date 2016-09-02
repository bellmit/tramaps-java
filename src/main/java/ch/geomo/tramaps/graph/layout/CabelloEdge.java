/*
 * Copyright (c) 2016 Thomas Zuberbuehler. All rights reserved.
 */

package ch.geomo.tramaps.graph.layout;

import ch.geomo.tramaps.graph.Edge;
import ch.geomo.tramaps.graph.Graph;
import ch.geomo.tramaps.graph.Node;
import ch.geomo.util.pair.MutablePair;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryCollection;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.Polygon;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static ch.geomo.tramaps.geo.util.GeomUtil.createCollection;
import static ch.geomo.tramaps.geo.util.GeomUtil.toStream;
import static ch.geomo.tramaps.geo.util.PolygonUtil.splitPolygon;

/**
 * Represents an originalEdge which is octilinear and has one or two additional nodes.
 */
public class CabelloEdge implements Observer {

    private MutablePair<Node> vertices;
    private Set<Edge> edges;

    private Graph graph;
    private Edge originalEdge;

    public CabelloEdge(@NotNull Edge edge, @NotNull Graph graph, boolean observes) {
        originalEdge = edge;
        this.graph = graph;
        if (observes) {
            originalEdge.addObserver(this);
        }
        vertices = new MutablePair<>();
        edges = new HashSet<>();
        updateCabelloEdge();
    }

    public Stream<Node> getNodeStream() {
        return Stream.concat(vertices.stream(), Stream.of(originalEdge.getNodeA(), originalEdge.getNodeB()));
    }

    public Set<Node> getNodes() {
        return Collections.unmodifiableSet(getNodeStream().collect(Collectors.toSet()));
    }

    public Set<Edge> getEdges() {
        return Collections.unmodifiableSet(edges);
    }

    private synchronized void updateCabelloEdge() {

        vertices.clear();
        edges.clear();

        Stream<LineString> edges = graph.getEdges().stream()
                .filter(originalEdge::equals)
                .map(Edge::getLineString);

        Geometry bbox = originalEdge.getLineString().getEnvelope();

        GeometryCollection polygons = splitPolygon(bbox, createCollection(edges));

        toStream(polygons)
                .filter(geom -> geom.relate(originalEdge.getGeometry(), "T********"))
                .findFirst()
                .map(geom -> (Polygon)geom)
                .ifPresent(polygon -> {
                    // TODO find octilinear edges for original edge
                    System.out.println("Polygon: " + polygon);
                });

    }

    @Override
    public void update(Observable o, Object arg) {
        updateCabelloEdge();
    }

}
