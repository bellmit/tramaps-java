/*
 * Copyright (c) 2016 by Thomas Zuberbühler
 */

package ch.geomo.tramaps.graph;

import ch.geomo.tramaps.util.tuple.Tuple;
import com.vividsolutions.jts.geom.LineString;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public interface Edge<N extends Node<? extends Edge>> {

    long getVersion();

    /**
     * Gets the node name. If not overridden, returns "nodeName--nodeName" coordinates as a
     * String representation.
     */
    @NotNull
    String getName();

    /**
     * Gets the nodes of this edge.
     */
    Tuple<N> getNodes();


    @Contract("null -> false")
    boolean contains(@Nullable Node node);

    @NotNull
    LineString getLineString();

    /**
     * Gets the opposite node of current edge. Returns null if given node is not a start or end node
     * of current edge!
     *
     * @throws java.util.NoSuchElementException if given {@link Node} is not a node of current {@link Edge}
     * @see Tuple#getOtherValue(Object)
     */
    @NotNull
    N getOppositeNode(@NotNull N node);

    /**
     * Gets length of this edge.
     */
    double getLength();

    /**
     * Calculates angle between this edge and the given other edge in radians.
     *
     * @throws java.util.NoSuchElementException if no shared node was found
     * @see #getSharedNode(Edge)
     */
    @NotNull
    Optional<Double> calculateAngleTo(@NotNull Edge<N> edge);

    /**
     * Returns true if current and given edge shares a node.
     */
    boolean isAdjacent(@NotNull Edge<N> edge);

    /**
     * Gets the shared node of current and given edge. Returns null if edges are not
     * adjacent. If both nodes are shared, only one will be returned!
     *
     * @throws java.util.NoSuchElementException if no shared node was found
     * @see Tuple#getSharedValue(Tuple)
     */
    @Nullable
    N getSharedNode(@NotNull Edge<N> edge);

    /**
     * Returns true if current edge intersects given edge.
     */
    @Contract("null->false")
    boolean intersects(Edge<N> otherEdge);

}
