/*
 * Copyright (c) 2016 Thomas Zuberbuehler. All rights reserved.
 */

package ch.geomo.tramaps.conflict;

import ch.geomo.tramaps.conflict.buffer.ElementBuffer;
import ch.geomo.tramaps.conflict.buffer.ElementBufferPair;
import ch.geomo.tramaps.geom.Axis;
import ch.geomo.tramaps.geom.MoveVector;
import ch.geomo.tramaps.geom.util.GeomUtil;
import ch.geomo.tramaps.geom.util.PolygonUtil;
import ch.geomo.tramaps.graph.Edge;
import ch.geomo.tramaps.graph.GraphElement;
import ch.geomo.tramaps.graph.Node;
import ch.geomo.tramaps.graph.util.Direction;
import ch.geomo.tramaps.graph.util.OctilinearDirection;
import ch.geomo.util.Contracts;
import ch.geomo.util.Loggers;
import ch.geomo.util.collection.pair.Pair;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.math.Vector2D;
import com.vividsolutions.jts.operation.distance.DistanceOp;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static ch.geomo.tramaps.conflict.ConflictType.NODE_EDGE;
import static ch.geomo.tramaps.geom.Axis.X;
import static ch.geomo.tramaps.geom.Axis.Y;

public class Conflict implements Comparable<Conflict> {

    private final static Comparator<Conflict> CONFLICT_COMPARATOR = new ConflictComparator();

    private final ElementBufferPair buffers;

    private ConflictType conflictType;

    private Polygon conflictPolygon;

    private MoveVector displaceVector;
    private MoveVector bestDisplaceVector;

    private MoveVector projection;
    private MoveVector rejection;

    private Axis bestDisplaceAxis;
    private Coordinate bestDisplaceStartPoint;

    private Geometry conflictArea;

    private boolean solved = false;

    public Conflict(Pair<ElementBuffer> bufferPair) {
        this(bufferPair.getFirst(), bufferPair.getSecond());
    }

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
        Loggers.info(this, "Cannot create conflict polygon. Result was: " + geometry);
        return GeomUtil.createEmptyPolygon();
    }

    /**
     * @return the {@link LineString} between the buffer element's centroid
     */
    @NotNull
    private LineString createQ() {
        return GeomUtil.createLineString(getBufferA().getElement().getCentroid(), getBufferB().getElement().getCentroid());
    }

    @NotNull
    public List<Node> getNodes() {
        return Stream.of(getBufferA().getElement(), getBufferB().getElement())
                .filter(element -> element instanceof Node)
                .map(element -> (Node) element)
                .collect(Collectors.toList());
    }

    @NotNull
    public List<Edge> getEdges() {
        return Stream.of(getBufferA().getElement(), getBufferB().getElement())
                .filter(element -> element instanceof Edge)
                .map(element -> (Edge) element)
                .collect(Collectors.toList());
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
                initEdgeEdgeConflict(getEdges().get(0), getEdges().get(1));
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

    private void initEdgeEdgeConflict(@NotNull Edge edge1, @NotNull Edge edge2) {

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

    public boolean hasElementNeighborhood(Collection<Edge> edges) {
        return edges.stream().noneMatch(edge -> conflictArea.crosses(edge.getLineString()));
    }

    public MoveVector getDisplaceVectorAlongX() {
        return projection;
    }

    public int getDisplaceDistanceAlongX() {
        return (int) Math.ceil(Math.abs(projection.length()));
    }

    public MoveVector getDisplaceVectorAlongY() {
        return rejection;
    }

    public double getDisplaceDistanceAlongY() {
        return (int) Math.ceil(Math.abs(rejection.length()));
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

    public boolean isDisplaceElement(GraphElement graphElement) {
        return Objects.equals(graphElement, getDisplaceElement());
    }

    public GraphElement getNonDisplaceElement() {
        if (getBufferA().getElement().equals(getDisplaceElement())) {
            return getBufferB().getElement();
        }
        return getBufferA().getElement();
    }

    public GraphElement getDisplaceElement() {
        switch (conflictType) {
            case NODE_NODE:
            case ADJACENT_NODE_NODE:
            case ADJACENT_NODE_NODE_DIAGONAL: {
                if (bestDisplaceAxis == X) {
                    if (getNodes().get(0).getX() < getDisplaceOriginPoint().x) {
                        return getNodes().get(1);
                    }
                    return getNodes().get(0);
                }
                if (getNodes().get(0).getY() < getDisplaceOriginPoint().y) {
                    return getNodes().get(1);
                }
                return getNodes().get(0);
            }
            case NODE_EDGE: {
                if (bestDisplaceAxis == X) {
                    if (getNodes().get(0).getX() < getDisplaceOriginPoint().x) {
                        return getEdges().get(0);
                    }
                    return getNodes().get(0);
                }
                if (getNodes().get(0).getY() < getDisplaceOriginPoint().y) {
                    return getEdges().get(0);
                }
                return getNodes().get(0);
            }
            case EDGE_EDGE: {
                if (bestDisplaceAxis == X) {
                    if (getEdges().get(0).getNodeA().getX() + getEdges().get(0).getNodeB().getX()
                            < getEdges().get(1).getNodeA().getX() + getEdges().get(1).getNodeB().getX()) {
                        return getEdges().get(1);
                    }
                    return getEdges().get(0);
                }
                if (getEdges().get(0).getNodeA().getX() + getEdges().get(0).getNodeB().getX()
                        < getEdges().get(1).getNodeA().getX() + getEdges().get(1).getNodeB().getX()) {
                    return getEdges().get(1);
                }
                return getEdges().get(0);
            }
        }
        return getBufferA().getElement();
    }

    /**
     * @return best move direction along an axis
     * @see OctilinearDirection#NORTH
     * @see OctilinearDirection#EAST
     */
    @NotNull
    public OctilinearDirection getBestDisplaceDirection() {
        if (bestDisplaceAxis == X) {
            return OctilinearDirection.NORTH;
        }
        return OctilinearDirection.EAST;
    }

    /**
     * @return best move distance along the best move direction
     * @see #getBestDisplaceDirection()
     */
    public int getBestDisplaceDistance() {
        // we do work with int values only
        return (int) Math.ceil(Math.abs(bestDisplaceVector.length()));
    }

    @NotNull
    public Coordinate getDisplaceOriginPoint() {
        return bestDisplaceStartPoint;
    }

    public boolean isNotSolved() {
        return !solved;
    }

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

    public boolean hasSingleNode() {
        return conflictType.hasNode() && getNodes().stream().anyMatch(node -> node.getNodeDegree() == 1);
    }

    public boolean isConflictElement(@NotNull GraphElement graphElement) {
        return graphElement.equals(getBufferA().getElement())
                || graphElement.equals(getBufferB().getElement());
    }

    public boolean isAdjacentToConflictElement(@NotNull GraphElement graphElement) {
        return graphElement.isAdjacent(getBufferA().getElement())
                || graphElement.isAdjacent(getBufferB().getElement());
    }

    public boolean isConflictRelated(@NotNull GraphElement graphElement) {
        return isConflictElement(graphElement) || isAdjacentToConflictElement(graphElement);
    }

    @Override
    public int compareTo(@NotNull Conflict o) {
        return CONFLICT_COMPARATOR.compare(this, o);
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Conflict
                && (Objects.equals(buffers, ((Conflict) obj).buffers));
    }

    @Override
    public int hashCode() {
        return Objects.hash(buffers);
    }

    @Override
    public String toString() {
        return "Conflict: {" + getBufferA() + ", " + getBufferB() + ", distance=" + getBestDisplaceDistance() + ", point=" + bestDisplaceStartPoint + ", axis=" + bestDisplaceAxis + "}";
    }

}
