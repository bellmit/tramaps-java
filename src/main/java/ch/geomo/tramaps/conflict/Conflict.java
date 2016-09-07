/*
 * Copyright (c) 2016 Thomas Zuberbuehler. All rights reserved.
 */

package ch.geomo.tramaps.conflict;

import ch.geomo.tramaps.conflict.buffer.ElementBuffer;
import ch.geomo.tramaps.conflict.buffer.ElementBufferPair;
import ch.geomo.tramaps.geom.Axis;
import ch.geomo.tramaps.geom.MoveVector;
import ch.geomo.tramaps.geom.util.PolygonUtil;
import ch.geomo.tramaps.graph.Edge;
import ch.geomo.tramaps.graph.GraphElement;
import ch.geomo.tramaps.graph.Node;
import ch.geomo.tramaps.graph.util.OctilinearDirection;
import ch.geomo.util.Loggers;
import ch.geomo.util.point.NodePoint;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.math.Vector2D;
import com.vividsolutions.jts.operation.distance.DistanceOp;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static ch.geomo.tramaps.geom.util.GeomUtil.getGeomUtil;

public class Conflict implements Comparable<Conflict> {

    private final ElementBufferPair buffers;

    private ConflictType conflictType;

    private Polygon conflictPolygon;

    private MoveVector displaceVector;
    private MoveVector bestDisplaceVector;
    private Axis bestDisplaceAxis;
    private Coordinate bestDisplaceStartPoint;

    private boolean solved = false;

    public Conflict(@NotNull ElementBuffer bufferA, @NotNull ElementBuffer bufferB) {
        buffers = new ElementBufferPair(bufferA, bufferB);
        evaluateConflictType();
        initConflict();
    }

    /**
     * Evaluates the conflict type.
     */
    private void evaluateConflictType() {
        if (buffers.isNodePair() && buffers.hasAdjacentElements()) {
            conflictType = ConflictType.ADJACENT_NODE_NODE;
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
     * @return polygon of the intersecting area of both buffers
     */
    @NotNull
    private Polygon createConflictPolygon() {
        Geometry geometry = getBufferA().getBuffer().intersection(getBufferB().getBuffer());
        if (geometry instanceof Polygon) {
            return (Polygon) geometry;
        }
        Loggers.info(this, "Cannot create conflict polygon. Result was: " + geometry);
        return getGeomUtil().createEmptyPolygon();
    }

    /**
     * @return the {@link LineString} between the buffer element's centroid
     */
    @NotNull
    private LineString createQ() {
        return getGeomUtil().createLineString(getBufferA().getElement().getCentroid(), getBufferB().getElement().getCentroid());
    }

    private List<Node> getNodes() {
        return Stream.of(getBufferElementA(), getBufferElementB())
                .filter(element -> element instanceof Node)
                .map(element -> (Node) element)
                .collect(Collectors.toList());
    }

    private List<Edge> getEdges() {
        return Stream.of(getBufferElementA(), getBufferElementB())
                .filter(element -> element instanceof Edge)
                .map(element -> (Edge) element)
                .collect(Collectors.toList());
    }

    private void initConflict() {

        // create conflict polygon
        conflictPolygon = createConflictPolygon();

        // create exact move vector
        displaceVector = PolygonUtil.findLongestParallelLineString(conflictPolygon, createQ())
                .map(MoveVector::new)
                .orElse(new MoveVector());

        // default values
        Coordinate[] closestPointsBetweenElements = DistanceOp.nearestPoints(getBufferElementA().getGeometry(), getBufferElementB().getGeometry());
        bestDisplaceStartPoint = getGeomUtil().createLineString(closestPointsBetweenElements).getCentroid().getCoordinate();

        double angleX = displaceVector.angle(MoveVector.VECTOR_ALONG_X_AXIS);
        double angleY = displaceVector.angle(MoveVector.VECTOR_ALONG_Y_AXIS);
        if (angleY < angleX) {
            bestDisplaceVector = displaceVector.getProjection(MoveVector.VECTOR_ALONG_X_AXIS);
            bestDisplaceAxis = Axis.X;
        }
        else {
            bestDisplaceVector = displaceVector.getProjection(MoveVector.VECTOR_ALONG_Y_AXIS);
            bestDisplaceAxis = Axis.Y;
        }

        // special cases
        switch (conflictType) {
            case NODE_EDGE: {
                initNodeEdgeConflict(getNodes().get(0), getEdges().get(0));
                break;
            }
            case NODE_NODE:
            case ADJACENT_NODE_NODE: {
                initNodeNodeConflict(getNodes().get(0), getNodes().get(1));
                break;
            }
        }

        if (conflictPolygon.isEmpty()) {
            solved = true;
        }

    }

    private void initNodeEdgeConflict(@NotNull Node node, @NotNull Edge edge) {
        if ((node.getX() < Math.min(edge.getNodeA().getX(), edge.getNodeB().getX()))
                || (node.getX() > Math.max(edge.getNodeA().getX(), edge.getNodeB().getX()))) {
            bestDisplaceVector = displaceVector.getProjection(MoveVector.VECTOR_ALONG_X_AXIS);
            bestDisplaceAxis = Axis.X;
        }
        else if ((node.getY() < Math.min(edge.getNodeA().getY(), edge.getNodeB().getY()))
                || (node.getY() > Math.max(edge.getNodeA().getY(), edge.getNodeB().getY()))) {
            bestDisplaceVector = displaceVector.getProjection(MoveVector.VECTOR_ALONG_Y_AXIS);
            bestDisplaceAxis = Axis.Y;
        }
        else {
            double dxa = Math.abs(node.getX() - edge.getNodeA().getX());
            double dya = Math.abs(node.getY() - edge.getNodeA().getY());
            double dxb = Math.abs(node.getX() - edge.getNodeB().getX());
            double dyb = Math.abs(node.getY() - edge.getNodeB().getY());
            if (Math.max(dxa, dxb) < Math.max(dya, dyb)) {
                bestDisplaceVector = displaceVector.getProjection(MoveVector.VECTOR_ALONG_X_AXIS);
                bestDisplaceAxis = Axis.X;
            }
            else {
                bestDisplaceVector = displaceVector.getProjection(MoveVector.VECTOR_ALONG_Y_AXIS);
                bestDisplaceAxis = Axis.Y;
            }
        }
        bestDisplaceStartPoint = conflictPolygon.getCentroid().getCoordinate();
    }

    private void initNodeNodeConflict(@NotNull NodePoint node1, @NotNull NodePoint node2) {
        double dx = Math.abs(node1.getX() - node2.getX());
        double dy = Math.abs(node1.getY() - node2.getY());
        if (dy < dx) {
            bestDisplaceVector = displaceVector.getProjection(MoveVector.VECTOR_ALONG_X_AXIS);
            bestDisplaceAxis = Axis.X;
        }
        else {
            bestDisplaceVector = displaceVector.getProjection(MoveVector.VECTOR_ALONG_Y_AXIS);
            bestDisplaceAxis = Axis.Y;
        }
        bestDisplaceStartPoint = conflictPolygon.getCentroid().getCoordinate();
    }

    @NotNull
    public ConflictType getConflictType() {
        return conflictType;
    }

    @NotNull
    public Polygon getConflictPolygon() {
        return conflictPolygon;
    }

    @NotNull
    public MoveVector getDisplaceVector() {
        return displaceVector;
    }

    @NotNull
    public Vector2D getBestDisplaceVector() {
        return bestDisplaceVector;
    }

    @NotNull
    public Axis getBestDisplaceAxis() {
        return bestDisplaceAxis;
    }

    /**
     * @return best move direction along an axis
     * @see OctilinearDirection#NORTH
     * @see OctilinearDirection#EAST
     */
    @NotNull
    public OctilinearDirection getBestDisplaceDirection() {
        if (bestDisplaceAxis == Axis.X) {
            return OctilinearDirection.NORTH;
        }
        return OctilinearDirection.EAST;
    }

    /**
     * @return best move distance along the best move direction
     * @see #getBestDisplaceDirection()
     */
    public int getBestDisplaceLength() {
        // we do work with int values only
        return (int) Math.ceil(bestDisplaceVector.length());
    }

    @NotNull
    public Coordinate getSamplePointOnDisplaceLine() {
        return bestDisplaceStartPoint;
    }

    /**
     * @return true if solved
     */
    public boolean isSolved() {
        return solved;
    }

    @NotNull
    public ElementBuffer getBufferA() {
        return buffers.first();
    }

    @NotNull
    public ElementBuffer getBufferB() {
        return buffers.second();
    }

    @NotNull
    public GraphElement getBufferElementA() {
        return buffers.first().getElement();
    }

    @NotNull
    public GraphElement getBufferElementB() {
        return buffers.second().getElement();
    }

    public boolean isConflictElement(@NotNull GraphElement graphElement) {
        return graphElement.equals(getBufferA().getElement())
                || graphElement.equals(getBufferB().getElement());
    }

    public boolean isAdjacentToConflictElement(@NotNull GraphElement graphElement) {
        return graphElement.isAdjacent(getBufferA().getElement())
                || graphElement.isAdjacent(getBufferB().getElement());
    }

    public boolean isConflictElementRelated(@NotNull GraphElement graphElement) {
        return isConflictElement(graphElement) || isAdjacentToConflictElement(graphElement);
    }

    @Override
    public int compareTo(@NotNull Conflict o) {

        int rank1 = conflictType.getConflictRank();
        int rank2 = o.getConflictType().getConflictRank();

        if (rank1 != rank2) {
            return Integer.compare(rank1, rank2);
        }

        double length1a = getBestDisplaceLength();
        double length2a = o.getBestDisplaceLength();

        if (length1a != length2a) {
            return Double.compare(length1a, length2a);
        }

        double length1b = displaceVector.length();
        double length2b = o.getDisplaceVector().length();

        if (length1b != length2b) {
            return Double.compare(length1b, length2b);
        }

        double x1 = displaceVector.getX();
        double x2 = o.getDisplaceVector().getX();

        if (x1 != x2) {
            return Double.compare(x1, x2);
        }

        double y1 = displaceVector.getY();
        double y2 = o.getDisplaceVector().getY();

        if (y1 != y2) {
            return Double.compare(y1, y2);
        }

        // Loggers.warning(this, "Conflicts are equals. Output might not be reproduceable.");
        return 0;

    }

    @Override
    public boolean equals(Object obj) {
        double length = getBestDisplaceLength();
        return obj instanceof Conflict
                && Objects.equals(length, ((Conflict) obj).getBestDisplaceLength())
                && Objects.equals(displaceVector.length(), ((Conflict) obj).getDisplaceVector().length())
                && Objects.equals(displaceVector.getX(), ((Conflict) obj).getDisplaceVector().getX())
                && Objects.equals(displaceVector.getY(), ((Conflict) obj).getDisplaceVector().getY());
    }

    @Override
    public int hashCode() {
        double length = getBestDisplaceLength();
        return Objects.hash(length, displaceVector.length(), displaceVector.getX(), displaceVector.getY());
    }

    @Override
    public String toString() {
        return "Conflict: {" + getBufferA() + ", " + getBufferB() + "}";
    }

}
