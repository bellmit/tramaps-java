/*
 * Copyright (c) 2016 Thomas Zuberbuehler. All rights reserved.
 */

package ch.geomo.tramaps.conflict;

import ch.geomo.tramaps.conflict.buffer.ElementBuffer;
import ch.geomo.tramaps.geom.MoveVector;
import ch.geomo.tramaps.graph.Edge;
import ch.geomo.tramaps.graph.Node;
import ch.geomo.tramaps.graph.util.Direction;
import ch.geomo.util.Contracts;
import ch.geomo.util.collection.pair.Pair;
import ch.geomo.util.geom.GeomUtil;
import ch.geomo.util.geom.PolygonUtil;
import ch.geomo.util.logging.Loggers;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.operation.distance.DistanceOp;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.stream.Stream;

import static ch.geomo.tramaps.conflict.ConflictType.NODE_EDGE;
import static ch.geomo.tramaps.geom.Axis.X;
import static ch.geomo.tramaps.geom.Axis.Y;

public class BufferConflict extends AbstractConflict {

    private Polygon conflictPolygon;

    private Geometry conflictArea;

    public BufferConflict(Pair<ElementBuffer> bufferPair) {
        this(bufferPair.getFirst(), bufferPair.getSecond());
    }

    public BufferConflict(@NotNull ElementBuffer bufferA, @NotNull ElementBuffer bufferB) {
        super(bufferA, bufferB);
        evaluateConflictType();
        initConflict();
    }

    /**
     * Evaluates the conflict type.
     */
    private void evaluateConflictType() {
        if (buffers.isNodePair() && buffers.hasAdjacentElements()) {
            if (((Node) buffers.first().getElement()).getAdjacentEdges().stream()
                    .filter(edge -> buffers.second().getElement().equals(edge.getOtherNode((Node) buffers.first().getElement())))
                    .map(edge -> edge.getOriginalDirection(null))
                    .noneMatch(Direction::isDiagonal)) {
                conflictType = ConflictType.ADJACENT_NODE_NODE;
            }
            else {
                conflictType = ConflictType.ADJACENT_NODE_NODE_DIAGONAL;
            }
        }
        else if (buffers.isNodePair()) {
            conflictType = ConflictType.NODE_NODE;
        }
        else if (buffers.isEdgePair()) {
            conflictType = ConflictType.EDGE_EDGE;
        }
        else {
            conflictType = ConflictType.NODE_EDGE;
        }
    }

    /**
     * @return polygon set the intersecting area set both buffers
     */
    @NotNull
    private Polygon createConflictPolygon() {
        Geometry geometry = getBufferA().getBuffer().intersection(getBufferB().getBuffer());
        if (geometry instanceof Polygon) {
            return (Polygon) geometry;
        }
        // Loggers.info(this, "Cannot create conflict polygon. Result was: " + geometry);
        return GeomUtil.createEmptyPolygon();
    }

    /**
     * @return the {@link LineString} between the buffer element's centroid
     */
    @NotNull
    private LineString createQ() {
        return GeomUtil.createLineString(getBufferA().getElement().getCentroid(), getBufferB().getElement().getCentroid());
    }

    private void initConflict() {

        initConflictArea();

        // create conflict polygon
        conflictPolygon = createConflictPolygon();

        // create exact move vector
        displaceVector = PolygonUtil.findLongestParallelLineString(conflictPolygon, createQ())
                .map(MoveVector::new)
                .orElse(new MoveVector());

        projection = displaceVector.getProjection(MoveVector.VECTOR_ALONG_X_AXIS);
        rejection = new MoveVector(displaceVector.subtract(projection));

        // default values
        bestDisplaceStartPoint = conflictPolygon.getCentroid().getCoordinate();

        // special cases
        switch (conflictType) {
            case NODE_EDGE: {
                initNodeEdgeConflict(getNodes().get(0), getEdges().get(0));
                break;
            }
            case NODE_NODE:
            case ADJACENT_NODE_NODE:
            case ADJACENT_NODE_NODE_DIAGONAL: {
                initNodeNodeConflict(getNodes().get(0), getNodes().get(1));
                break;
            }
            case EDGE_EDGE: {
                initEdgeEdgeConflict();
                break;
            }
            default: {
                Contracts.fail();
            }
        }

        if (conflictPolygon.isEmpty()) {
            solved = true;
        }

        if (conflictType == NODE_EDGE) {
            Node node = getNodes().get(0);
            Edge edge = getEdges().get(0);
            if (node.isAdjacent(edge.getNodeA()) || node.isAdjacent(edge.getNodeB())) {
                // we currently ignore this kind set conflict
                // TODO remove workaround and find another solution
                // solved = true;
            }
        }

    }

    private void initConflictArea() {

        switch (conflictType) {
            case NODE_NODE:
            case ADJACENT_NODE_NODE:
            case ADJACENT_NODE_NODE_DIAGONAL: {
                conflictArea = GeomUtil.createLineString(getNodes().get(0), getNodes().get(1));
                break;
            }
            case NODE_EDGE: {
                conflictArea = GeomUtil.createPolygon(getNodes().get(0), getEdges().get(0).getNodeA(), getEdges().get(0).getNodeB(), getNodes().get(0));
                break;
            }
            case EDGE_EDGE: {

                Node a1 = getEdges().get(0).getNodeA();
                Node b1 = getEdges().get(0).getNodeB();
                Node a2 = getEdges().get(1).getNodeA();
                Node b2 = getEdges().get(1).getNodeB();

                conflictArea = GeomUtil.createPolygon(a1, b1, b2, a2, a1);
                if (!conflictArea.isValid()) {
                    conflictArea = GeomUtil.createPolygon(a1, b1, a2, b2, a1);
                    if (!conflictArea.isValid()) {
                        Coordinate[] coordinates = Stream.of(a1, a2, b1, b2).map(Node::getCoordinate).toArray(Coordinate[]::new);
                        conflictArea = GeomUtil.createLineString(coordinates);
                    }
                }

                break;

            }
            default: {
                Contracts.fail();
            }
        }

    }

    private void initEdgeEdgeConflict() {

        double angleX = displaceVector.angle(MoveVector.VECTOR_ALONG_X_AXIS);
        double angleY = displaceVector.angle(MoveVector.VECTOR_ALONG_Y_AXIS);
        if (angleY < angleX) {
            bestDisplaceVector = displaceVector.getProjection(MoveVector.VECTOR_ALONG_X_AXIS);
            bestDisplaceAxis = X;
        }
        else {
            bestDisplaceVector = displaceVector.getProjection(MoveVector.VECTOR_ALONG_Y_AXIS);
            bestDisplaceAxis = Y;
        }

        Geometry intersection = conflictPolygon.intersection(conflictArea);
        if (intersection.isEmpty()) {
            solved = true;
        }
        else {
            bestDisplaceStartPoint = intersection.getCentroid().getCoordinate();
        }

    }

    private void initNodeEdgeConflict(@NotNull Node node, @NotNull Edge edge) {
        if ((node.getX() < Math.min(edge.getNodeA().getX(), edge.getNodeB().getX()))
                || (node.getX() > Math.max(edge.getNodeA().getX(), edge.getNodeB().getX()))) {

            bestDisplaceVector = displaceVector.getProjection(MoveVector.VECTOR_ALONG_X_AXIS);
            bestDisplaceAxis = X;
        }
        else if ((node.getY() < Math.min(edge.getNodeA().getY(), edge.getNodeB().getY()))
                || (node.getY() > Math.max(edge.getNodeA().getY(), edge.getNodeB().getY()))) {
            bestDisplaceVector = displaceVector.getProjection(MoveVector.VECTOR_ALONG_Y_AXIS);
            bestDisplaceAxis = Y;
        }
        else {
            double dxa = Math.abs(node.getX() - edge.getNodeA().getX());
            double dya = Math.abs(node.getY() - edge.getNodeA().getY());
            double dxb = Math.abs(node.getX() - edge.getNodeB().getX());
            double dyb = Math.abs(node.getY() - edge.getNodeB().getY());
            if (Math.max(dxa, dxb) < Math.max(dya, dyb)) {
                bestDisplaceVector = displaceVector.getProjection(MoveVector.VECTOR_ALONG_X_AXIS);
                bestDisplaceAxis = X;
            }
            else {
                bestDisplaceVector = displaceVector.getProjection(MoveVector.VECTOR_ALONG_Y_AXIS);
                bestDisplaceAxis = Y;
            }
        }
        Coordinate nearestPoint = DistanceOp.nearestPoints(edge.getGeometry(), node.getGeometry())[0];
        LineString line = GeomUtil.createLineString(node.getX(), node.getY(), nearestPoint.x, nearestPoint.y);
        bestDisplaceStartPoint = line.getCentroid().getCoordinate();
    }

    private void initNodeNodeConflict(@NotNull Node node1, @NotNull Node node2) {

        if (node1.getX() == node2.getX() && node1.getY() == node2.getY()) {
            Contracts.fail("Both conflict nodes has equal position. Please check input data!");
        }

        double dx = Math.abs(node1.getX() - node2.getX());
        double dy = Math.abs(node1.getY() - node2.getY());
        if (dy < dx) {
            bestDisplaceVector = displaceVector.getProjection(MoveVector.VECTOR_ALONG_X_AXIS);
            bestDisplaceAxis = X;
        }
        else {
            bestDisplaceVector = displaceVector.getProjection(MoveVector.VECTOR_ALONG_Y_AXIS);
            bestDisplaceAxis = Y;
        }
        bestDisplaceStartPoint = GeomUtil.createLineString(node1, node2).getCentroid().getCoordinate();

    }

    public Polygon getConflictPolygon() {
        return conflictPolygon;
    }

    protected boolean hasElementNeighborhood(Collection<Edge> edges) {
        return edges.stream().noneMatch(edge -> conflictArea.crosses(edge.getLineString()));
    }

}
