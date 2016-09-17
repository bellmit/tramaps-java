/*
 * Copyright (c) 2016 Thomas Zuberbuehler. All rights reserved.
 */

package ch.geomo.tramaps.conflict;

import ch.geomo.tramaps.conflict.buffer.ElementBuffer;
import ch.geomo.tramaps.geom.Axis;
import ch.geomo.tramaps.geom.MoveVector;
import ch.geomo.tramaps.graph.GraphElement;
import ch.geomo.tramaps.graph.util.OctilinearDirection;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.math.Vector2D;
import org.jetbrains.annotations.NotNull;

public interface Conflict extends Comparable<Conflict> {

    double getDisplaceDistanceAlongX();

    double getDisplaceDistanceAlongY();

    @NotNull
    ConflictType getConflictType();

    @NotNull
    MoveVector getDisplaceVector();

    @NotNull
    Vector2D getBestDisplaceVector();

    @NotNull
    Axis getBestDisplaceAxis();

    @NotNull
    OctilinearDirection getBestDisplaceDirection();

    int getBestDisplaceDistance();

    @NotNull
    Coordinate getDisplaceOriginPoint();

    boolean isNotSolved();

    boolean isSolved();

    @NotNull
    ElementBuffer getBufferA();

    @NotNull
    ElementBuffer getBufferB();

    boolean isConflictRelated(@NotNull GraphElement graphElement);

}
