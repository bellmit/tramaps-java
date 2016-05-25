/*
 * Copyright (c) 2016 by Thomas Zuberbühler
 */

package ch.geomo.tramaps.graph;

import ch.geomo.tramaps.grid.GridNode;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;

public interface Edge<T extends Number & Comparable<T>, N extends Node<T, ?>> {

    /**
     * Gets the node name. If not overridden, returns "nodeName--nodeName" coordinates as a String representation.
     */
    @NotNull
    default String getName() {
        return getNodes().getLeft().getName() + "--" + getNodes().getRight().getName();
    }

    /**
     * Gets the nodes of this edge.
     */
    @NotNull
    Pair<N, N> getNodes();

    @NotNull
    default N getStart() {
        return getNodes().getLeft();
    }

    @NotNull
    default N getEnd() {
        return getNodes().getRight();
    }

    @NotNull
    default T getStartX() {
        return getStart().getX();
    }

    @NotNull
    default T getStartY() {
        return getStart().getY();
    }

    @NotNull
    default T getEndX() {
        return getEnd().getX();
    }

    @NotNull
    default T getEndY() {
        return getEnd().getY();
    }

    @Contract("null -> false")
    default boolean contains(@Nullable N node) {
        return node != null && (node.equals(getStart()) || node.equals(getEnd()));
    }

    /**
     * Gets the opposite node of current edge. Returns null if given node is not a start or end node
     * of current edge!
     */
    @Nullable
    default N getOppositeNode(@Nullable N node) {

        if (!contains(node)) {
            return null;
        }

        Pair<N, N> nodes = getNodes();
        if (nodes.getRight().equals(node)) {
            return nodes.getLeft();
        }
        return nodes.getRight();

    }

    /**
     * Gets length of this edge.
     */
    double getLength();

    /**
     * Calculates angle between this edge and the given other edge in radians. Returns
     * null if current and given edge does not share a point.
     */
    @Nullable
    default Double calculateAngleTo(@NotNull Edge<T, N> edge) {

        N n1 = getSharedNode(edge);
        if (n1 == null) {
            // not adjacent
            return null;
        }

        N n2 = getOppositeNode(n1);
        N n3 = edge.getOppositeNode(n1);
        if (n2 == null || n3 == null) {
            // should never reach this line since edges are adjacent
            throw new IllegalStateException("Cannot calculate angle between non adjacent edges.");
        }

        return n1.calculateAngleBetween(n2, n3);

    }

    /**
     * Returns true if current and given edge shares a node.
     */
    default boolean isAdjacent(@NotNull Edge<T, N> edge) {
        return getSharedNode(edge) != null;
    }

    /**
     * Gets the shared node of current and given edge. Returns null if edges are not
     * adjacent.
     * <p>
     * Limitation: if both nodes are shared, only one will be returned!
     */
    @Nullable
    default N getSharedNode(@NotNull Edge<T, N> edge) {

        N l1 = getNodes().getLeft();
        N r1 = getNodes().getRight();

        N l2 = edge.getNodes().getLeft();
        N r2 = edge.getNodes().getRight();

        if (l1 == l2 || l1 == r2) {
            return l1;
        }
        if (r1 == l2 || r1 == r2) {
            return r1;
        }
        return null;

    }

}
