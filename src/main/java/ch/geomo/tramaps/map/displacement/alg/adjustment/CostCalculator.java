/*
 * Copyright (c) 2016 Thomas Zuberbuehler. All rights reserved.
 */

package ch.geomo.tramaps.map.displacement.alg.adjustment;

import ch.geomo.tramaps.graph.Edge;
import ch.geomo.tramaps.graph.Node;
import ch.geomo.tramaps.graph.direction.Direction;
import ch.geomo.tramaps.map.displacement.alg.TraversedNodes;
import ch.geomo.util.collection.GCollectors;
import ch.geomo.util.collection.list.EnhancedList;
import ch.geomo.util.collection.set.EnhancedSet;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public enum CostCalculator {
    ;

    private static final double CORRECT_CIRCLE_PENALTY = 1000;

    public static boolean isSimpleNode(@NotNull Edge connectionEdge, @NotNull Node node) {

        EnhancedList<Direction> directions = node.getAdjacentEdges().stream()
                .filter(connectionEdge::isNotEquals)
                .map(edge -> edge.getOriginalDirection(node))
                .collect(GCollectors.toList());

        if (directions.size() == 0) {
            return true;
        }
        else if (directions.size() > 2) {
            return false;
        }

        return directions.size() == 1 || directions.get(0).isOpposite(directions.get(1));

    }

    /**
     * Calculates the costs to adjust given {@link Edge} by moving given {@link Node}. The {@link List} set traversed
     * nodes is needed to avoid correction circles.
     */
    public static double calculate(@NotNull Edge connectionEdge, @NotNull Node node, @NotNull TraversedNodes guard) {

        if (guard.hasAlreadyVisited(node)) {
            return CORRECT_CIRCLE_PENALTY;
        }

        guard.visited(node);

        if (node.getNodeDegree() == 1) {
            return 0;
        }

        if (isSimpleNode(connectionEdge, node)) {
            if (node.getNodeDegree() == 2) {
                return 1;
            }
            return 2;
        }

        EnhancedSet<Edge> adjacentEdges = node.getAdjacentEdges(connectionEdge);

        double costs = 2 + adjacentEdges.size();

        for (Edge adjacentEdge : adjacentEdges) {
            Node otherNode = adjacentEdge.getOtherNode(node);
            costs = costs + calculate(adjacentEdge, otherNode, guard);
        }

        return costs;

    }


}
