/*
 * Copyright (c) 2016 Thomas Zuberbuehler. All rights reserved.
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
import ch.geomo.util.math.MoveVector;
import com.vividsolutions.jts.geom.Coordinate;
import org.jetbrains.annotations.NotNull;

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

    /**
     * @see Conflict#getConflictType()
     */
    @NotNull
    @Override
    public ConflictType getConflictType() {
        return conflictType;
    }

    /**
     * @see Conflict#getDisplaceVector()
     */
    @NotNull
    @Override
    public MoveVector getDisplaceVector() {
        return displaceVector;
    }

    /**
     * @see Conflict#getBestDisplaceAxis()
     */
    @NotNull
    @Override
    public Axis getBestDisplaceAxis() {
        return bestDisplaceAxis;
    }

    /**
     * @return best move direction along an axis
     * @see OctilinearDirection#NORTH
     * @see OctilinearDirection#EAST
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
     * @see #getBestDisplaceDirection()
     */
    @Override
    public double getBestDisplaceDistance() {
        return Math.ceil(Math.abs(bestDisplaceVector.length()));
    }

    /**
     * @see Conflict#getBufferA()
     */
    @NotNull
    @Override
    public ElementBuffer getBufferA() {
        return buffers.first();
    }

    /**
     * @see Conflict#getBufferB()
     */
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

    /**
     * @see Conflict#isSolved()
     */
    @Override
    public boolean isSolved() {
        return solved;
    }

    /**
     * @see Conflict#getDisplaceOriginPoint()
     */
    @NotNull
    @Override
    public Coordinate getDisplaceOriginPoint() {
        return bestDisplaceStartPoint;
    }

    /**
     * @return the <b>ceiled</b> displace distance along x-axis
     * @see Conflict#getDisplaceDistanceAlongX()
     */
    @Override
    public double getDisplaceDistanceAlongX() {
        return Math.ceil(Math.abs(projection.getFirst().length()));
    }

    /**
     * @return the <b>ceiled</b> displace distance along y-axis
     * @see Conflict#getDisplaceDistanceAlongY()
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
     * @see Conflict#isConflictRelated(GraphElement)
     */
    @Override
    public boolean isConflictRelated(@NotNull GraphElement graphElement) {
        return isConflictElement(graphElement) || isAdjacentToConflictElement(graphElement);
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
