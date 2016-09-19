/*
 * Copyright (c) 2016 Thomas Zuberbuehler. All rights reserved.
 */

package ch.geomo.tramaps.map.displacement.alg.adjustment;

import ch.geomo.tramaps.geom.MoveVector;
import ch.geomo.tramaps.graph.Edge;
import ch.geomo.tramaps.graph.Node;
import ch.geomo.tramaps.graph.util.Direction;
import ch.geomo.tramaps.graph.util.GraphUtil;
import ch.geomo.tramaps.graph.util.OctilinearDirection;
import ch.geomo.util.collection.GCollectors;
import ch.geomo.util.collection.list.EnhancedList;
import ch.geomo.util.doc.HelperMethod;
import ch.geomo.util.logging.Loggers;
import org.jetbrains.annotations.NotNull;

public class DirectionEvaluator {

    public DirectionEvaluator() {
    }

    @NotNull
    public MoveVector evaluateSingleNodeDirection(@NotNull Node moveableNode, @NotNull Edge connectionEdge) {

        Node otherNode = connectionEdge.getOtherNode(moveableNode);

        double diff = GraphUtil.getAbsDiffDeltaXY(connectionEdge);

        OctilinearDirection originalDirection = connectionEdge.getOriginalDirection(otherNode).toOctilinear();
        double angle = originalDirection.getAngleTo(connectionEdge.getDirection(otherNode));

        switch (originalDirection.opposite()) {
            case NORTH_EAST: {
                if (angle < 90) {
                    return new MoveVector(0, -diff);
                }
                return new MoveVector(-diff, 0);
            }
            case SOUTH_EAST: {
                if (angle < 90) {
                    return new MoveVector(-diff, 0);
                }
                return new MoveVector(0, diff);
            }
            case SOUTH_WEST: {
                if (angle < 90) {
                    return new MoveVector(0, diff);
                }
                return new MoveVector(diff, 0);
            }
            case NORTH_WEST: {
                if (angle < 90) {
                    return new MoveVector(diff, 0);
                }
                return new MoveVector(0, -diff);
            }
            default: {
                // analysis required: do we reach this point? when yes, how can this case be solved?
                Loggers.info(this, "Single node {0} has a non-diagonal connection edge. -> Not (yet) treated/implemented.");
                return new MoveVector(0, 0);
            }
        }

    }

    @NotNull
    @HelperMethod
    private EnhancedList<OctilinearDirection> getAdjacentEdgeDirections(@NotNull Node node, @NotNull Edge connectionEdge) {
        return node.getAdjacentEdges().stream()
                .filter(connectionEdge::isNotEquals)
                .map(edge -> edge.getOriginalDirection(node).toOctilinear())
                .collect(GCollectors.toList());
    }

    @NotNull
    public MoveVector evaluateDirection(@NotNull Node moveableNode, @NotNull Edge connectionEdge) {

        Node otherNode = connectionEdge.getOtherNode(moveableNode);

        double dx = GraphUtil.getAbsDeltaX(connectionEdge);
        double dy = GraphUtil.getAbsDeltaY(connectionEdge);

        double moveDistance = GraphUtil.getAbsDiffDeltaXY(connectionEdge);

        EnhancedList<OctilinearDirection> directions = getAdjacentEdgeDirections(moveableNode, connectionEdge);

        if (directions.allMatch(Direction::isVertical)) {
            if (dy > dx) {
                if (otherNode.isNorthOf(moveableNode)) {
                    return new MoveVector(0, moveDistance);
                }
                return new MoveVector(0, -moveDistance);
            }
        }
        else if (directions.allMatch(Direction::isHorizontal)) {
            if (dx > dy) {
                if (otherNode.isEastOf(moveableNode)) {
                    return new MoveVector(moveDistance, 0);
                }
                return new MoveVector(-moveDistance, 0);
            }
        }
        else if (directions.size() == 2) {

        }
        else {
            // return evaluateSingleNodeDirection(moveableNode, connectionEdge);
        }

        return new MoveVector(0, 0);

    }

}
