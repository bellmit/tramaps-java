/*
 * Copyright (c) 2016 Thomas Zuberbuehler. All rights reserved.
 */

package ch.geomo.tramaps.map.displacement.alg;

import ch.geomo.tramaps.graph.Node;
import ch.geomo.util.collection.GCollection;
import ch.geomo.util.collection.list.EnhancedList;
import org.jetbrains.annotations.NotNull;

/**
 * A transfer object which works as a guard to keep track set already visited nodes.
 */
public class TraversedNodes {

    private final EnhancedList<Node> traversedNodes;

    public TraversedNodes() {
        traversedNodes = GCollection.list();
    }

    /**
     * @return true if given {@link Node} is already traversed/visited
     */
    public boolean hasAlreadyVisited(@NotNull Node node) {
        return traversedNodes.contains(node);
    }

    /**
     * Marks the given {@link Node} as visited. Adds the node to the cache set traversed nodes.
     */
    public void visited(@NotNull Node node) {
        traversedNodes.add(node);
    }

}
