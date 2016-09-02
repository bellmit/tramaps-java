/*
 * Copyright (c) 2016 Thomas Zuberbuehler. All rights reserved.
 */

package ch.geomo.tramaps.graph.layout;

import ch.geomo.tramaps.graph.Edge;
import ch.geomo.tramaps.graph.Graph;
import ch.geomo.tramaps.graph.Node;
import ch.geomo.tramaps.map.signature.BendNodeSignature;
import ch.geomo.util.pair.MutablePair;
import ch.geomo.util.pair.Pair;
import com.vividsolutions.jts.geom.*;
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

    public Pair<Node> getVertices() {
        return vertices;
    }

    public Set<Edge> getEdges() {
        return Collections.unmodifiableSet(edges);
    }

    private synchronized void updateCabelloEdge() {

        vertices.clear();
        edges.clear();

        Stream<LineString> edges = graph.getEdges().stream()
                .filter(edge -> !edge.equals(originalEdge))
                .map(Edge::getLineString);

        Geometry bbox = originalEdge.getLineString().getEnvelope();

        GeometryCollection polygons = splitPolygon(bbox, createCollection(edges));

        toStream(polygons)
                .filter(geom -> geom.relate(originalEdge.getGeometry(), "T********"))
                .findFirst()
                .map(geom -> (Polygon)geom)
                .ifPresent(polygon -> {
                    // TODO find octilinear edges for original edge
                    Coordinate coordinate = evaluateOctilinearBendCoordinate(originalEdge);
                    vertices.setFirst(new Node(coordinate, BendNodeSignature::new));
                    // System.out.println(polygon);
                });

    }

    @Override
    public void update(Observable o, Object arg) {
        updateCabelloEdge();
    }


    /**
     * Evaluates a coordinate which allows an octilinear bend in given {@link Edge}.
     *
     * @return the evaluated {@link Coordinate}
     */
    @NotNull
    private static Coordinate evaluateOctilinearBendCoordinate(@NotNull Edge edge) {

        Node nodeA = edge.getNodeA();
        Node nodeB = edge.getNodeB();

        double relX = nodeB.getX() / nodeA.getX();
        double relY = nodeB.getY() / nodeB.getY();

        if (relX < relY) {
            if (nodeA.getDegree() < nodeB.getDegree()) {
                double y = nodeA.getX() / nodeB.getX() * nodeB.getY();
                return new Coordinate(nodeA.getX(), y);
            }
            double y = nodeB.getX() / nodeA.getX() * nodeA.getY();
            return new Coordinate(nodeB.getX(), y);
        }

        // relX > relY
        if (nodeA.getDegree() < nodeB.getDegree()) {
            double x = nodeA.getY() / nodeB.getY() * nodeB.getX();
            return new Coordinate(x, nodeA.getY());
        }
        double x = nodeB.getY() / nodeA.getY() * nodeA.getX();
        return new Coordinate(x, nodeB.getY());

    }

}
