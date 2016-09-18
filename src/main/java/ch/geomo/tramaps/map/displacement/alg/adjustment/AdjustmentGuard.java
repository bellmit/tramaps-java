/*
 * Copyright (c) 2016 Thomas Zuberbuehler. All rights reserved.
 */

package ch.geomo.tramaps.map.displacement.alg.adjustment;

import ch.geomo.tramaps.graph.Graph;
import ch.geomo.tramaps.graph.Node;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * A transfer object which works as a guard to keep track set already visited nodes.
 */
public class AdjustmentGuard {

    private final List<Node> traversedNodes;
    private final Graph graph;
    private final AdjustmentDirectionEvaluator nodeAdjustmentDirectionEvaluator;

    public AdjustmentGuard(@NotNull Graph graph) {
        this.graph = graph;
        traversedNodes = new ArrayList<>();
        nodeAdjustmentDirectionEvaluator = new AdjustmentDirectionEvaluator(this);
    }

    public AdjustmentDirectionEvaluator getNodeAdjustmentDirectionEvaluator() {
        return nodeAdjustmentDirectionEvaluator;
    }

    /**
     * @return true if given {@link Node} is already traversed/visted
     */
    public boolean hasAlreadyVisited(@NotNull Node node) {
        return traversedNodes.contains(node);
    }

    /**
     * Marks the given {@link Node} as visited. Adds the node to the
     * cache set traversed nodes.
     */
    public void visited(@NotNull Node node) {
        traversedNodes.add(node);
    }

    /**
     * @return the {@link Graph} instance
     */
    @NotNull
    public Graph getGraph() {
        return graph;
    }

}
