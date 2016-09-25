/*
 * Copyright (c) 2016 Thomas Zuberbuehler. All rights reserved.
 */

package ch.geomo.tramaps.conflict;

import ch.geomo.tramaps.conflict.buffer.ElementBuffer;
import ch.geomo.tramaps.graph.Edge;
import ch.geomo.tramaps.graph.Node;
import ch.geomo.util.collection.pair.Pair;
import ch.geomo.util.geom.GeomUtil;
import ch.geomo.util.geom.PolygonUtil;
import ch.geomo.util.logging.Loggers;
import ch.geomo.util.math.MoveVector;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.operation.distance.DistanceOp;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.stream.Stream;

import static ch.geomo.util.geom.Axis.X;
import static ch.geomo.util.geom.Axis.Y;

/**
 * Represents conflicts between overlapping elements/buffers, eg. node/edge, edge/edge or node/node.
 * @see ConflictType
 */
public class BufferConflict extends AbstractConflict {

    private Polygon conflictPolygon;
    private Geometry conflictArea;

    public BufferConflict(@NotNull Pair<ElementBuffer> bufferPair) {
        super(bufferPair);
        evaluateConflictType();
        initConflict();
    }

    /**
     * Evaluates the type of this conflict in order to be prioritized and easier comparision.
     */
    private void evaluateConflictType() {
        if (buffers.isNodePair() && buffers.hasAdjacentElements()) {
            if (isAdjacentNodeNodeDiagonal()) {
                conflictType = ConflictType.ADJACENT_NODE_NODE_DIAGONAL;
            }
            else {
                conflictType = ConflictType.ADJACENT_NODE_NODE;
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
     * @return true if the shared adjacent edge of both conflict nodes is diagonal
     */
    private boolean isAdjacentNodeNodeDiagonal() {
        return getNodes().get(0)
                .getAdjacentEdgeWith(getNodes().get(1)).getOriginalDirection(getNodes().get(0))
                .isDiagonal();
    }

    /**
     * Initialize this conflict.
     */
    private void initConflict() {

        initConflictArea();

        // create conflict polygon
        conflictPolygon = createConflictPolygon();

        // create exact move vector
        displaceVector = PolygonUtil.findLongestParallelLineString(conflictPolygon, createQ())
                .map(MoveVector::new)
                .orElse(new MoveVector());

        projection = displaceVector.getProjection(MoveVector.VECTOR_ALONG_X_AXIS);

        // default values
        bestDisplaceStartPoint = conflictPolygon.getCentroid().getCoordinate();

        // handle different conflict types
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
        }

        if (conflictPolygon.isEmpty()) {
            solved = true;
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
        // result of intersection is not a polygon, it's a geometry with a dimension of 1 or less, so we do not
        // care about this conflict at the moment since it's a very small one
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

    /**
     * Initializes and creates the conflict area. The conflict area is within or identical with the conflict polygon
     * and allows to define the starting point for the displacement. The starting point must always within the
     * conflict area, otherwise it would not be between both conflict elements and therefore never solve the conflict.
     */
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

                // improvement required: replace try and error approach
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
        }

    }

    /**
     * Evaluates and initialize the best displace vector as well as the best displace axis.
     */
    private void initBestDisplaceVector() {
        // choosing the projection with the smallest angle to the x-axis
        double dx = Math.abs(getBufferA().getElement().getCentroid().getX() - getBufferB().getElement().getCentroid().getX());
        double dy = Math.abs(getBufferA().getElement().getCentroid().getY() - getBufferB().getElement().getCentroid().getY());
        if (dy < dx) {
            bestDisplaceVector = getMoveVectorAlongX();
            bestDisplaceAxis = X;
        }
        else {
            bestDisplaceVector = getMoveVectorAlongY();
            bestDisplaceAxis = Y;
        }
    }

    /**
     * Initialize an edge/edge conflict.
     */
    private void initEdgeEdgeConflict() {
        initBestDisplaceVector();
        Geometry intersection = conflictPolygon.intersection(conflictArea);
        if (intersection.isEmpty()) {
            solved = true;
        }
        else {
            // only point within the conflict area can be handled since starting points outside of the conflict area
            // may not be between both edges and would never solve the conflict
            bestDisplaceStartPoint = intersection.getCentroid().getCoordinate();
        }
    }

    /**
     * Initialize an node/edge conflict.
     */
    private void initNodeEdgeConflict(@NotNull Node node, @NotNull Edge edge) {
        if (node.isWestOf(edge) || node.isEastOf(edge)) {
            bestDisplaceVector = getMoveVectorAlongX();
            bestDisplaceAxis = X;
        }
        else if (node.isSouthOf(edge) || node.isNorthOf(edge)) {
            bestDisplaceVector = getMoveVectorAlongY();
            bestDisplaceAxis = Y;
        }
        else {
            // find the best move direction done by calculating the deltas between the node
            // and both end nodes of the edge, move along the other axis than the axis with
            // the bigger delta
            double dxa = Math.abs(node.getX() - edge.getNodeA().getX());
            double dya = Math.abs(node.getY() - edge.getNodeA().getY());
            double dxb = Math.abs(node.getX() - edge.getNodeB().getX());
            double dyb = Math.abs(node.getY() - edge.getNodeB().getY());
            if (Math.max(dxa, dxb) < Math.max(dya, dyb)) {
                bestDisplaceVector = getMoveVectorAlongX();
                bestDisplaceAxis = X;
            }
            else {
                bestDisplaceVector = getMoveVectorAlongY();
                bestDisplaceAxis = Y;
            }
        }
        // set the best displace start point half way on the line between the nearest points of the edge and the node
        Coordinate nearestPoint = DistanceOp.nearestPoints(edge.getGeometry(), node.getGeometry())[0];
        LineString line = GeomUtil.createLineString(node.getCoordinate(), nearestPoint);
        bestDisplaceStartPoint = line.getCentroid().getCoordinate();
    }

    /**
     * Initialize an node/node conflict.
     */
    private void initNodeNodeConflict(@NotNull Node node1, @NotNull Node node2) {
        if (node1.getX() == node2.getX() && node1.getY() == node2.getY()) {
            throw new IllegalStateException("Both conflict nodes has equal position. Please check input data!");
        }
        initBestDisplaceVector();
        bestDisplaceStartPoint = GeomUtil.createLineString(node1, node2).getCentroid().getCoordinate();
    }

    /**
     * @return the conflict polygon
     */
    public Polygon getConflictPolygon() {
        return conflictPolygon;
    }

    /**
     * Evaluates if both conflict elements are neighbors. This is given when the conflict area is not crossed
     * by another edge.
     * @return true if the given edges <b>do not</b> cross with the conflict area
     */
    protected boolean hasElementNeighborhood(@NotNull Collection<Edge> edges) {
        return edges.stream()
                .noneMatch(edge -> conflictArea.crosses(edge.getLineString()));
    }

}
