/*
 * Copyright (c) 2016 Thomas Zuberbuehler. All rights reserved.
 */

package ch.geomo.tramaps.map.displacement.alg.helper;

import ch.geomo.tramaps.graph.Edge;
import ch.geomo.tramaps.graph.Node;
import ch.geomo.tramaps.graph.util.Direction;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class AdjustmentCostCalculator {

    private static final double CORRECT_CIRCLE_PENALTY = 1000;

    public static boolean isSimpleNode(@NotNull Edge connectionEdge, @NotNull Node node) {

        Direction originalDirection = connectionEdge.getOriginalDirection(node).toOctilinear();

        List<Direction> directions = node.getAdjacentEdgeStream(connectionEdge)
                .map(edge -> edge.getDirection(node))
                .collect(Collectors.toList());

        if (directions.size() == 0) {
            return true;
        }
        else if (directions.size() > 2) {
            return false;
        }
        else if (directions.stream().anyMatch(originalDirection::isOpposite)) {
            return false;
        }

        return directions.size() == 1 || directions.get(0).isOpposite(directions.get(1));

    }

    /**
     * Calculates the costs to adjust given {@link Edge} by moving given {@link Node}. The {@link List} of traversed
     * nodes is needed to avoid correction circles.
     */
    public double calculateAdjustmentCosts(@NotNull Edge connectionEdge,
                                           @NotNull Node node,
                                           @NotNull MoveNodeGuard guard) {

        if (guard.isNotMoveable(node) || guard.hasAlreadyVisited(node)) {
            return CORRECT_CIRCLE_PENALTY;
        }

        guard.visited(node);

        if (node.getNodeDegree() == 1) {
            return 0;
        }

        if (isSimpleNode(connectionEdge, node)) {
            return 1;
        }

        Set<Edge> adjacentEdges = node.getAdjacentEdgeStream(connectionEdge)
                .collect(Collectors.toSet());

        double costs = 2 + adjacentEdges.size();

        for (Edge adjacentEdge : adjacentEdges) {
            Node otherNode = adjacentEdge.getOtherNode(node);
            costs = costs + calculateAdjustmentCosts(adjacentEdge, otherNode, guard);
        }

        return costs;

    }


}
