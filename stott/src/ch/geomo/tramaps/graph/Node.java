/*
 * Copyright (c) 2016 by Thomas Zuberbühler
 */

package ch.geomo.tramaps.graph;

import ch.geomo.tramaps.geom.Point;
import ch.geomo.tramaps.util.CollectionUtil;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;

public interface Node<T extends Number & Comparable<T>, E extends Edge<T, ?>> extends Point<T> {

    /**
     * Gets the node name. If not overridden, returns x/y coordinates as a String representation.
     */
    @NotNull
    default String getName() {
        return getX() + "/" + getY();
    }

    @Nullable
    NodeLabel getLabel();

    /**
     * Gets pairs of adjacent edges.
     */
    @NotNull
    default Set<Pair<E, E>> getAdjacentEdgePairs() {
        return new HashSet<>(CollectionUtil.makePairs(getEdges(), true, true));
    }

    /**
     * Gets all edges.
     */
    @NotNull
    Set<E> getEdges();

    /**
     * Gets the degree value of this node.
     */
    default int getDegreeValue() {
        return getEdges().size();
    }

    /**
     * Returns true if the degree value is equals to given value.
     */
    default boolean hasDegreeValueOf(int value) {
        return getDegreeValue() == value;
    }

    /**
     * Returns the number of the quadrant of a Cartesian coordinate system using given node as origin.
     */
    @NotNull
    default Quadrant getQuadrant(@NotNull Point<T> originPoint) {
        return Quadrant.getQuadrant(this, originPoint);
    }

}
