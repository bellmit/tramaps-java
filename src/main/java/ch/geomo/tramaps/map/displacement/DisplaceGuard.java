/*
 * Copyright (c) 2016 Thomas Zuberbuehler. All rights reserved.
 */

package ch.geomo.tramaps.map.displacement;

import ch.geomo.tramaps.conflict.Conflict;
import ch.geomo.tramaps.graph.Node;
import ch.geomo.tramaps.map.MetroMap;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * A transfer object which works as a guard to keep track of already visited nodes.
 */
public class DisplaceGuard {

    private final List<Node> moveableNodes;
    private final List<Node> traversedNodes;

    private final Conflict conflict;
    private final MetroMap map;

    public DisplaceGuard(@NotNull MetroMap map, @NotNull Conflict conflict, @NotNull List<Node> moveableNodes) {
        this.map = map;
        this.conflict = conflict;
        this.moveableNodes = moveableNodes;
        traversedNodes = new ArrayList<>();
    }

    /**
     * @return the best displace length based on the {@link Conflict}
     */
    public int getMoveDistance() {
        return conflict.getBestDisplaceLength();
    }

    /**
     * @return the {@link Conflict} which is guarded by this instance
     */
    @NotNull
    public Conflict getConflict() {
        return conflict;
    }

    /**
     * @return true if given {@link Node} is already traversed/visted
     */
    public boolean hasAlreadyVisited(@NotNull Node node) {
        return traversedNodes.contains(node);
    }

    /**
     * Marks the given {@link Node} as visited. Adds the node to the
     * cache of traversed nodes.
     */
    public void visited(@NotNull Node node) {
        traversedNodes.add(node);
    }

    /**
     * @return true if given node is moveable
     */
    @Contract(pure = true)
    public boolean isMoveable(@NotNull Node node) {
        return moveableNodes.contains(node);
    }

    /**
     * @return true if given node is not moveable
     */
    @Contract(pure = true)
    public boolean isNotMoveable(@NotNull Node node) {
        return !isMoveable(node);
    }

    /**
     * Clears the cache of the traversed nodes and returns this instance.
     *
     * @return this instance
     */
    @NotNull
    public DisplaceGuard reuse() {
        traversedNodes.clear();
        return this;
    }

    /**
     * @return the {@link MetroMap} instance
     */
    @NotNull
    public MetroMap getMetroMap() {
        return map;
    }

}
