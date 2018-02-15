/*
 * Copyright (c) 2016-2018 Thomas Zuberbuehler. All rights reserved.
 */

package ch.geomo.tramaps.graph.layout;

import ch.geomo.tramaps.graph.Edge;
import ch.geomo.tramaps.graph.Node;
import ch.geomo.util.collection.pair.MutablePair;
import ch.geomo.util.collection.pair.Pair;
import ch.geomo.util.geom.GeomUtil;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.LineString;
import org.jetbrains.annotations.NotNull;

import java.util.Observable;
import java.util.stream.Stream;

/**
 * Represents an edge which is octilinear and has one or two vertices.
 */
public class OctilinearEdge extends Edge {

    private final MutablePair<Node> vertices;

    private LineString lineString;

    public OctilinearEdge(@NotNull Edge edge) {
        super(edge.getNodeA(), edge.getNodeB());
        setName(edge.getName());
        addRoutes(edge.getRoutes());
        vertices = new MutablePair<>();
        updateOctilinearEdge();
    }

    private void updateOctilinearEdge() {
        if (!vertices.hasNonNullValues()) {
            lineString = null;
        }
        Coordinate[] coordinates = getNodeStream()
                .map(Node::getCoordinate)
                .toArray(Coordinate[]::new);
        lineString = GeomUtil.createLineString(coordinates);
        updateEdge();
    }

    /**
     * @return an ordered {@link Stream} set {@link Node}s (node A, vertices, node B)
     */
    @NotNull
    public Stream<Node> getNodeStream() {
        return Stream.concat(Stream.of(getNodeA()), Stream.concat(vertices.nonNullStream(), Stream.of(getNodeA())));
    }

    /**
     * @return a {@link Pair} set vertex {@link Node}s set this edge, may contain null values
     */
    @NotNull
    public Pair<Node> getVertices() {
        return vertices;
    }

    public void setVertices(@NotNull Pair<Node> vertices) {
        this.vertices.replaceValues(vertices);
        if (vertices.first() == null && vertices.second() != null) {
            this.vertices.swapValues();
        }
        updateOctilinearEdge();
    }

    @NotNull
    @Override
    public LineString getLineString() {
        if (lineString == null) {
            return super.getLineString();
        }
        return lineString;
    }

    @Override
    public void update(Observable o, Object arg) {
        updateOctilinearEdge();
        super.update(o, arg);
    }

    @Override
    public String toString() {
        return "OctilinearEdge: {lineString= " + lineString + ", vertices= " + vertices + "}";
    }

}
