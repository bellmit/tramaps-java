/*
 * Copyright (c) 2016 Thomas Zuberbuehler. All rights reserved.
 */

package ch.geomo.tramaps.conflict;

import ch.geomo.tramaps.conflict.buffer.ElementBuffer;
import ch.geomo.util.geom.Axis;
import ch.geomo.util.math.MoveVector;
import ch.geomo.tramaps.graph.GraphElement;
import ch.geomo.tramaps.graph.direction.OctilinearDirection;

import com.vividsolutions.jts.geom.Coordinate;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a {@link Conflict} within a graph layout.
 */
public interface Conflict extends Comparable<Conflict> {

    /**
     * @return the necessary displace distance along the <b>x</b>-axis to solve this conflict
     */
    double getDisplaceDistanceAlongX();

    /**
     * @return the necessary displace distance along the <b>y</b>-axis to solve this conflict
     */
    double getDisplaceDistanceAlongY();

    /**
     * @return the type of this conflict
     * @see ConflictType
     */
    @NotNull
    ConflictType getConflictType();

    /**
     * @return the required displace vector to solve this conflict (v)
     */
    @NotNull
    MoveVector getDisplaceVector();

    /**
     * @return the best axis for the displacement line (g)
     */
    @NotNull
    Axis getBestDisplaceAxis();

    /**
     * @return the best displacement direction based on the best displacement axis
     * @see #getBestDisplaceAxis()
     */
    @NotNull
    OctilinearDirection getBestDisplaceDirection();

    /**
     * @return the best displacement distance based on the best displacement axis
     * @see #getBestDisplaceAxis()
     */
    double getBestDisplaceDistance();

    /**
     * @return a point (p) through which the displacement line (g) is running
     */
    @NotNull
    Coordinate getDisplaceOriginPoint();

    /**
     * @return true if this {@link Conflict} has <b>not</b> been solved meanwhile
     */
    default boolean isNotSolved() {
        return !isSolved();
    }

    /**
     * @return true if this {@link Conflict} has been solved meanwhile
     */
    boolean isSolved();

    /**
     * @return the first buffer element
     */
    @NotNull
    ElementBuffer getBufferA();

    /**
     * @return the second buffer element
     */
    @NotNull
    ElementBuffer getBufferB();

    /**
     * @return true if the given {@link GraphElement} is related to this conflict
     */
    boolean isConflictRelated(@NotNull GraphElement graphElement);

}
