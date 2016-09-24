/*
 * Copyright (c) 2016 Thomas Zuberbuehler. All rights reserved.
 */

package ch.geomo.tramaps.map.displacement.alg.adjustment;

import ch.geomo.tramaps.graph.Edge;
import ch.geomo.tramaps.graph.Node;
import ch.geomo.tramaps.graph.direction.Direction;
import ch.geomo.tramaps.graph.direction.OctilinearDirection;
import ch.geomo.util.collection.GCollectors;
import ch.geomo.util.collection.list.EnhancedList;
import ch.geomo.util.logging.Loggers;
import ch.geomo.util.math.MoveVector;
import org.jetbrains.annotations.NotNull;

/**
 * Provides methods to evaluate the best move vector in order to correct a non-octilinear edge.
 */
public enum DirectionEvaluator {
    ;

    /**
     * Evaluates the best move vector for a node with a degree of 1 and given connection edge.
     * @return the best move vector to correct given connection edge
     */
    @NotNull
    public static MoveVector evaluateSingleNodeDirection(@NotNull Node moveableNode, @NotNull Edge connectionEdge) {

        double dx = Math.abs(connectionEdge.getNodeA().getX() - connectionEdge.getNodeB().getX());
        double dy = Math.abs(connectionEdge.getNodeA().getY() - connectionEdge.getNodeB().getY());
        double diff = Math.abs(dx - dy);

        Node otherNode = connectionEdge.getOtherNode(moveableNode);
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
                Loggers.info(DirectionEvaluator.class, "Single node {0} has a non-diagonal connection edge. -> Not (yet) treated/implemented.");
                return new MoveVector(0, 0);
            }
        }

    }

    @NotNull
    private static EnhancedList<OctilinearDirection> getAdjacentEdgeDirections(@NotNull Node node, @NotNull Edge connectionEdge) {
        return node.getAdjacentEdges().stream()
                .filter(connectionEdge::isNotEquals)
                .map(edge -> edge.getOriginalDirection(node).toOctilinear())
                .collect(GCollectors.toList());
    }

    @NotNull
    public static MoveVector evaluateDirection(@NotNull Node moveableNode, @NotNull Edge connectionEdge) {

        double dx = Math.abs(connectionEdge.getNodeA().getX() - connectionEdge.getNodeB().getX());
        double dy = Math.abs(connectionEdge.getNodeA().getY() - connectionEdge.getNodeB().getY());
        double diff = Math.abs(dx - dy);

        Node otherNode = connectionEdge.getOtherNode(moveableNode);
        EnhancedList<OctilinearDirection> directions = getAdjacentEdgeDirections(moveableNode, connectionEdge);

        if (directions.allMatch(Direction::isVertical)) {
            if (dy > dx) {
                if (otherNode.isNorthOf(moveableNode)) {
                    return new MoveVector(0, diff);
                }
                return new MoveVector(0, -diff);
            }
            if (otherNode.isNorthOf(moveableNode)) {
                return new MoveVector(0, -diff);
            }
            return new MoveVector(0, diff);
        }
        else if (directions.allMatch(Direction::isHorizontal)) {
            if (dx > dy) {
                if (otherNode.isEastOf(moveableNode)) {
                    return new MoveVector(diff, 0);
                }
                return new MoveVector(-diff, 0);
            }
            if (otherNode.isEastOf(moveableNode)) {
                return new MoveVector(-diff, 0);
            }
            return new MoveVector(diff, 0);
        }
        else if (directions.hasOneElement()) {

            Edge adjacentEdge = moveableNode.getAdjacentEdges(connectionEdge)
                    .first()
                    .orElseThrow(IllegalStateException::new);
            Direction adjacentEdgeDirection = adjacentEdge.getDirection(moveableNode);

            // future improvement: analyse if we could either ease the layout to improve quality or if we
            // could correct that node but which may not help since always one edge will remain
            // non-octilinear. furthermore it may decrease the quality depending on the new angle of the edge
            if (!adjacentEdgeDirection.isOctilinear()) {
                OctilinearDirection originalConnectionEdgeDirection = connectionEdge.getOriginalDirection(moveableNode).toOctilinear();
                if (originalConnectionEdgeDirection.getAngle() == directions.get(0).opposite().getAngle()) {
                    // ease layout (no full correction)
                    // LineString line = GeomUtil.createLineString(connectionEdge.getOtherNode(moveableNode), adjacentEdge.getOtherNode(moveableNode));
                    // Point centroid = line.getCentroid();
                    // return new MoveVector(moveableNode.getPoint(), centroid);
                    return new MoveVector(0, 0);
                }
            }

        }

        return new MoveVector(0, 0);

    }

}
