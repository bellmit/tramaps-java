/*
 * Copyright (c) 2016 by Thomas Zuberbühler
 */

package ch.geomo.tramaps.graph;

import ch.geomo.tramaps.geom.NodePoint;
import ch.geomo.tramaps.grid.GridNode;
import ch.geomo.tramaps.util.tuple.Tuple;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Point;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

public interface Node<E extends Edge> extends NodePoint {

    long getVersion();

    /**
     * Gets the node name. If not overridden, returns x/y coordinates as a String representation.
     */
    @NotNull
    String getName();

    @Nullable
    NodeLabel getLabel();

    /**
     * Gets pairs of adjacent edges.
     */
    @NotNull
    Set<Tuple<E>> getAdjacentEdgePairs();

    /**
     * Returns all adjacent nodes.
     */
    @NotNull
    Set<? extends Node<E>> getAdjacentNodes();

    /**
     * Gets all edges.
     */
    @NotNull
    Set<E> getEdges();

    @NotNull
    Point getPoint();

    @NotNull
    Coordinate getCoordinate();

    /**
     * Gets the degree value of this node.
     */
    int getDegreeValue();

    /**
     * Returns true if the degree value is equals to given value.
     */
    boolean hasDegreeValueOf(int value);

    /**
     * Returns the number of the quadrant of a Cartesian coordinate system using given node as origin.
     */
    @NotNull
    Quadrant getQuadrant(@NotNull NodePoint originPoint);

}
