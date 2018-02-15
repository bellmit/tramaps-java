/*
 * Copyright (c) 2016-2018 Thomas Zuberbuehler. All rights reserved.
 */

package ch.geomo.tramaps.conflict;

import ch.geomo.tramaps.conflict.buffer.ElementBuffer;
import ch.geomo.tramaps.conflict.buffer.ElementBufferPair;
import ch.geomo.tramaps.graph.Edge;
import ch.geomo.tramaps.graph.GraphElement;
import ch.geomo.tramaps.graph.Node;
import ch.geomo.tramaps.graph.direction.OctilinearDirection;
import ch.geomo.util.collection.pair.Pair;
import ch.geomo.util.geom.Axis;
import ch.geomo.util.geom.GeomUtil;
import ch.geomo.util.math.MoveVector;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryCollection;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static ch.geomo.tramaps.conflict.ConflictFinder.CONFLICT_COMPARATOR;
import static ch.geomo.util.geom.Axis.X;

/**
 * Provides a common implementation of certain methods and properties of a {@link Conflict} to reduce redundancy and
 * duplicated code.
 */
public abstract class AbstractConflict implements Conflict {

    protected final ElementBufferPair buffers;

    /**
     * Projection of the displace vector along the x-axis and its rejection.
     */
    protected Pair<MoveVector> projection;

    protected ConflictType conflictType;
    protected MoveVector displaceVector;
    protected MoveVector bestDisplaceVector;
    protected Axis bestDisplaceAxis;
    protected Coordinate bestDisplaceStartPoint;
    protected boolean solved = false;

    public AbstractConflict(@NotNull Pair<ElementBuffer> bufferPair) {
        buffers = new ElementBufferPair(bufferPair.getFirst(), bufferPair.getSecond());
    }

    /**
     * @return the {@link MoveVector} along x-axis
     */
    @NotNull
    protected MoveVector getMoveVectorAlongX() {
        return projection.getFirst();
    }

    /**
     * @return the {@link MoveVector} along y-axis
     */
    @NotNull
    protected MoveVector getMoveVectorAlongY() {
        return projection.getSecond();
    }

    @NotNull
    @Override
    public ConflictType getConflictType() {
        return conflictType;
    }

    @NotNull
    @Override
    public MoveVector getDisplaceVector() {
        return displaceVector;
    }

    /**
     * @return best move direction along an axis
     */
    @NotNull
    @Override
    public OctilinearDirection getBestDisplaceDirection() {
        if (bestDisplaceAxis == X) {
            return OctilinearDirection.EAST;
        }
        return OctilinearDirection.NORTH;
    }

    /**
     * @return best move distance along the best move direction
     */
    @Override
    public double getBestDisplaceDistance() {
        return Math.ceil(Math.abs(bestDisplaceVector.length()));
    }

    @NotNull
    @Override
    public ElementBuffer getBufferA() {
        return buffers.first();
    }

    @NotNull
    @Override
    public ElementBuffer getBufferB() {
        return buffers.second();
    }

    /**
     * @return all nodes as a list (list's size is 0, 1 or 2 nodes depending on the conflict type)
     */
    @NotNull
    public List<Node> getNodes() {
        return Stream.of(getBufferA().getElement(), getBufferB().getElement())
                .filter(element -> element instanceof Node)
                .map(element -> (Node) element)
                .collect(Collectors.toList());
    }

    /**
     * @return all edges as a list (list's size is 0, 1 or 2 edges depending on the conflict type)
     */
    @NotNull
    public List<Edge> getEdges() {
        return Stream.of(getBufferA().getElement(), getBufferB().getElement())
                .filter(element -> element instanceof Edge)
                .map(element -> (Edge) element)
                .collect(Collectors.toList());
    }

    @Override
    public boolean isSolved() {
        return solved;
    }

    @NotNull
    @Override
    public Coordinate getDisplaceOriginPoint() {
        return bestDisplaceStartPoint;
    }

    /**
     * @return the <b>ceiled</b> displace distance along x-axis
     */
    @Override
    public double getDisplaceDistanceAlongX() {
        return Math.ceil(Math.abs(projection.getFirst().length()));
    }

    /**
     * @return the <b>ceiled</b> displace distance along y-axis
     */
    @Override
    public double getDisplaceDistanceAlongY() {
        return Math.ceil(Math.abs(projection.getSecond().length()));
    }

    /**
     * @return if given element is a conflict element
     */
    private boolean isConflictElement(@NotNull GraphElement graphElement) {
        return graphElement.equals(getBufferA().getElement())
                || graphElement.equals(getBufferB().getElement());
    }

    /**
     * @return if given element is adjacent to a conflict element
     */
    private boolean isAdjacentToConflictElement(@NotNull GraphElement graphElement) {
        return graphElement.isAdjacent(getBufferA().getElement())
                || graphElement.isAdjacent(getBufferB().getElement());
    }

    /**
     * @return if given element is a conflict element or adjacent to a conflict element
     */
    @Override
    public boolean isConflictRelated(@NotNull GraphElement graphElement) {
        return isConflictElement(graphElement) || isAdjacentToConflictElement(graphElement);
    }

    @NotNull
    @Override
    public Envelope getElementBoundingBox() {

        Geometry geomA = getBufferA().getElement().getGeometry();
        Geometry geomB = getBufferB().getElement().getGeometry();

//        if (getConflictType() == ConflictType.NODE_EDGE) {
//            Node node = getNodes().get(0);
//            Edge edge = getEdges().get(0);
//            if (isEdgeAdjacentNodeConflict(node, edge)) {
//                // refactoring required: extract to an util method since function is already used twice
//                Node adjacentNode = node.getAdjacentEdges().stream()
//                        .map(e -> e.getOtherNode(node))
//                        .filter(n -> n.equals(edge.getNodeA()) || n.equals(edge.getNodeB()))
//                        .findFirst()
//                        .orElseThrow(IllegalArgumentException::new);
//                geomA = node.getGeometry();
//                geomB = edge.getOtherNode(adjacentNode).getGeometry();
//            }
//        }

        GeometryCollection col = GeomUtil.createCollection(Arrays.asList(geomA, geomB));
        return col.getEnvelopeInternal();

    }

    /**
     * @return true if this conflict is a node/edge conflict and does have an adjacent node with one end node of the edge
     */
    protected boolean isEdgeAdjacentNodeConflict(@NotNull Node node, @NotNull Edge edge) {
        if (edge.getOriginalDirection(edge.getNodeA()).isDiagonal()) {
            return node.getAdjacentEdges().stream()
                    .map(e -> e.getOtherNode(node))
                    .anyMatch(n -> n.equals(edge.getNodeA()) || n.equals(edge.getNodeB()));
        }
        return false;
    }

    @Override
    public int compareTo(@NotNull Conflict o) {
        return CONFLICT_COMPARATOR.compare(this, o);
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof AbstractConflict
                && (Objects.equals(buffers, ((AbstractConflict) obj).buffers));
    }

    @Override
    public int hashCode() {
        return Objects.hash(buffers);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + ": {" +
                "elements=[" + getBufferA() + ", " + getBufferB() + "], " +
                "distance=" + getBestDisplaceDistance() + ", " +
                "point=" + bestDisplaceStartPoint + ", " +
                "axis=" + bestDisplaceAxis + "}";
    }

}
