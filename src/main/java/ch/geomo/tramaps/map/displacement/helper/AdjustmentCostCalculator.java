/*
 * Copyright (c) 2016 Thomas Zuberbuehler. All rights reserved.
 */

package ch.geomo.tramaps.map.displacement.helper;

import ch.geomo.tramaps.graph.Edge;
import ch.geomo.tramaps.graph.Node;
import ch.geomo.tramaps.graph.util.Direction;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class AdjustmentCostCalculator {

    private static final double CORRECT_CIRCLE_PENALTY = 1000;
    private static final double ADJACENT_CONFLICT_RELATED_EDGE_PENALTY = 10;

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

        if (node.getDegree() == 1) {
            return 0;
        }

        Set<Edge> adjacentEdges = node.getAdjacentEdges().stream()
                .filter(edge -> !edge.equals(connectionEdge))
                .collect(Collectors.toSet());

        if (isSimpleNode(connectionEdge, node)) {

            boolean hasConflictRelatedEdge = node.getAdjacentEdges().stream()
                    .anyMatch(guard::isConflictElementRelated);

            if (hasConflictRelatedEdge && guard.hasBeenDisplaced(node)) {
                return ADJACENT_CONFLICT_RELATED_EDGE_PENALTY;
            }

            switch (guard.getLastMoveDirection()) {
                case NORTH:
                case SOUTH: {
                    if (adjacentEdges.stream().allMatch(Edge::isVertical)) {
                        return 1;
                    }
                    break;
                }
                case WEST:
                case EAST: {
                    if (adjacentEdges.stream().allMatch(Edge::isHorizontal)) {
                        return 1;
                    }
                    break;
                }
            }
            return 2;

        }

        double costs = 2 + adjacentEdges.size();

        for (Edge adjacentEdge : adjacentEdges) {
            Node otherNode = adjacentEdge.getOtherNode(node);
            costs = costs + calculateAdjustmentCosts(adjacentEdge, otherNode, guard);
        }

        return costs;

    }


}
