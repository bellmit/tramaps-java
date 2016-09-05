/*
 * Copyright (c) 2016 Thomas Zuberbuehler. All rights reserved.
 */

package ch.geomo.tramaps.map.displacement;

import ch.geomo.tramaps.conflict.Conflict;
import ch.geomo.tramaps.graph.Node;
import ch.geomo.tramaps.graph.util.OctilinearDirection;
import ch.geomo.tramaps.map.MetroMap;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class DisplaceGuard {

    private List<Node> moveableNodes;
    private List<Node> traversedNodes;

    private Conflict conflict;
    private MetroMap map;

    public DisplaceGuard(@NotNull MetroMap map, @NotNull Conflict conflict, @NotNull List<Node> moveableNodes) {
        this.map = map;
        this.conflict = conflict;
        this.moveableNodes = moveableNodes;
        traversedNodes = new ArrayList<>();
    }

    public int getMoveDistance() {
        return conflict.getBestDisplacementLength();
    }

    public boolean hasAlreadyVisited(@NotNull Node node) {
        return traversedNodes.contains(node);
    }

    public void visited(@NotNull Node node) {
        traversedNodes.add(node);
    }

    public boolean isMoveable(@NotNull Node node) {
        return moveableNodes.contains(node);
    }

    public boolean isNotMoveable(@NotNull Node node) {
        return !isMoveable(node);
    }

    public DisplaceGuard reuse() {
        traversedNodes.clear();
        return this;
    }

    public MetroMap getMetroMap() {
        return map;
    }
}
