/*
 * Copyright (c) 2016 Thomas Zuberbuehler. All rights reserved.
 */

package ch.geomo.tramaps.graph.layout;

import ch.geomo.tramaps.graph.Edge;
import ch.geomo.tramaps.graph.Graph;
import ch.geomo.tramaps.graph.Node;
import ch.geomo.tramaps.graph.util.OctilinearDirection;
import ch.geomo.tramaps.map.signature.BendNodeSignature;
import ch.geomo.util.Loggers;
import ch.geomo.util.MathUtil;
import ch.geomo.util.pair.MutablePair;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryCollection;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.Polygon;
import org.jetbrains.annotations.NotNull;

import java.util.stream.Stream;

import static ch.geomo.tramaps.geo.util.GeomUtil.createCollection;
import static ch.geomo.tramaps.geo.util.GeomUtil.toStream;
import static ch.geomo.tramaps.geo.util.PolygonUtil.splitPolygon;

/**
 * Represents an originalEdge which is octilinear and has one or two additional nodes.
 */
public class OctilinearEdgeBuilder {

    private MutablePair<Node> vertices;

    private Edge originalEdge;
    private Graph graph;

    public OctilinearEdgeBuilder() {
        vertices = new MutablePair<>();
    }

    @NotNull
    public OctilinearEdgeBuilder setOriginalEdge(@NotNull Edge edge) {
        originalEdge = edge;
        return this;
    }

    @NotNull
    public OctilinearEdgeBuilder setGraph(@NotNull Graph graph) {
        this.graph = graph;
        return this;
    }

    public OctilinearEdge build() {
        createNodes();
        OctilinearEdge octilinearEdge = new OctilinearEdge(originalEdge);
        octilinearEdge.setVertices(vertices);
        return octilinearEdge;
    }

    @SuppressWarnings("unused")
    private void evaluatePolygonAndCreateNodes() {

        Stream<LineString> edges = graph.getEdges().stream()
                .filter(edge -> !edge.equals(originalEdge))
                .map(Edge::getLineString);

        Geometry bbox = originalEdge.getLineString().getEnvelope();

        GeometryCollection polygons = splitPolygon(bbox, createCollection(edges));

        toStream(polygons)
                .filter(geom -> geom.relate(originalEdge.getGeometry(), "T********"))
                .findFirst()
                .map(geom -> (Polygon) geom)
                // currently ignoring polygon
                .ifPresent(polygon -> createNodes());

    }

    /**
     * Creates the vertices based on the original edge and checks the result
     */
    private void createNodes() {

        Loggers.info(this, "Create vertices for octilinear edge.");

        vertices.clear();

        Node a = originalEdge.getNodeA();
        Node b = originalEdge.getNodeB();

        double dx = Math.abs(b.getX() - a.getX());
        double dy = Math.abs(b.getY() - a.getY());

        if (dx < dy) {

            Node c = MathUtil.min(a, b, (n1, n2) -> Double.compare(n1.getY(), n2.getY()));
            Node d = b.equals(c) ? a : b;

            // check for a conflict with an adjacent and vertical edge of node C
            boolean conflictFree = c.getAdjacentEdges().stream()
                    .filter(Edge::isVertical)
                    .noneMatch(edge -> edge.getDirection().getAngle() == OctilinearDirection.NORTH.getAngle());

            if (conflictFree) {
                double y1 = d.getY() - dx;
                Node node = new Node(c.getX(), y1, BendNodeSignature::new);
                node.setName("-");
                vertices.set(0, node);
            }
            else {

                // check for a conflict with an adjacent and vertical edge of node D
                conflictFree = d.getAdjacentEdges().stream()
                        .filter(Edge::isVertical)
                        .noneMatch(edge -> edge.getDirection().getAngle() == OctilinearDirection.SOUTH.getAngle());

                if (conflictFree) {
                    double y1 = d.getY() - dx;
                    Node node = new Node(c.getX(), y1, BendNodeSignature::new);
                    node.setName("-");
                    vertices.set(0, node);
                }
                else {

                    double y1 = c.getY() + (dy / 2) + (dx / 2);
                    Node node1 = new Node(c.getX(), y1, BendNodeSignature::new);
                    node1.setName("-");
                    vertices.set(0, node1);

                    double y2 = c.getY() + (dy / 2) - (dx / 2);
                    Node node2 = new Node(c.getX(), y2, BendNodeSignature::new);
                    node2.setName("-");
                    vertices.set(1, node2);

                }

            }

        }
        else {

            Node c = MathUtil.min(a, b, (n1, n2) -> Double.compare(n1.getX(), n2.getX()));
            Node d = b.equals(c) ? a : b;

            // check for a conflict with an adjacent and vertical edge of node C
            boolean conflictFree = c.getAdjacentEdges().stream()
                    .filter(Edge::isHorizontal)
                    .noneMatch(edge -> edge.getDirection().getAngle() == OctilinearDirection.WEST.getAngle());

            if (conflictFree) {
                // OK
                double x1 = c.getX() + dy;
                Node node = new Node(x1, c.getY(), BendNodeSignature::new);
                node.setName("-");
                vertices.set(0, node);
            }
            else {

                // check for a conflict with an adjacent and vertical edge of node D
                conflictFree = d.getAdjacentEdges().stream()
                        .filter(Edge::isHorizontal)
                        .noneMatch(edge -> edge.getDirection().getAngle() == OctilinearDirection.EAST.getAngle());

                if (conflictFree) {
                    double x1 = d.getX() - dy;
                    Node node = new Node(x1, c.getY(), BendNodeSignature::new);
                    node.setName("-");
                    vertices.set(0, node);
                }
                else {

                    double x1 = c.getX() + (dx / 2) + (dy / 2);
                    Node node1 = new Node(x1, c.getY(), BendNodeSignature::new);
                    node1.setName("-");
                    vertices.set(0, node1);

                    double x2 = c.getX() + (dx / 2) - (dy / 2);
                    Node node2 = new Node(x2, c.getY(), BendNodeSignature::new);
                    node2.setName("-");
                    vertices.set(1, node2);

                }

            }
        }

    }


}
