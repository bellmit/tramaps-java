/*
 * Copyright (c) 2016 Thomas Zuberbuehler. All rights reserved.
 */

package ch.geomo.tramaps.map.displacement.alg.adjustment;

import ch.geomo.tramaps.geom.MoveVector;
import ch.geomo.tramaps.graph.Edge;
import ch.geomo.tramaps.graph.Node;
import ch.geomo.tramaps.graph.util.Direction;
import ch.geomo.tramaps.graph.util.OctilinearDirection;
import ch.geomo.util.collection.GCollectors;
import ch.geomo.util.collection.list.EnhancedList;
import ch.geomo.util.logging.Loggers;
import com.vividsolutions.jts.math.Vector2D;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

import static ch.geomo.tramaps.graph.util.OctilinearDirection.EAST;
import static ch.geomo.tramaps.graph.util.OctilinearDirection.NORTH;

public class AdjustmentDirectionEvaluator {

    private final AdjustmentGuard guard;

    AdjustmentDirectionEvaluator(@NotNull AdjustmentGuard guard) {
        this.guard = guard;
    }

    @NotNull
    public MoveVector evaluateSingleNodeDirection(@NotNull Node moveableNode, @NotNull Edge connectionEdge) {

        if (guard.getConflict().isConflictRelated(moveableNode)) {
            return new MoveVector(0, 0);
        }

        Node otherNode = connectionEdge.getOtherNode(moveableNode);

        OctilinearDirection originalDirection = connectionEdge.getOriginalDirection(otherNode).toOctilinear();
        MoveVector originalConnectionEdgeVector = originalDirection.getVector();

        double dx = moveableNode.getX() - otherNode.getX();
        double dy = moveableNode.getY() - otherNode.getY();

        MoveVector connectionEdgeVector = new MoveVector(dx, dy);
        Vector2D correctedEdgeVector = originalConnectionEdgeVector.multiply(connectionEdgeVector.length());

        return MoveVector.getProjection(correctedEdgeVector, connectionEdgeVector).getSecond();

    }

    @NotNull
    public EnhancedList<Direction> getAdjacentEdgeDirections(@NotNull Node node, @NotNull Edge connectionEdge) {
        return node.getAdjacentEdges().stream()
                .filter(connectionEdge::isNotEquals)
                .map(edge -> edge.getOriginalDirection(node))
                .collect(GCollectors.toList());
    }

    @NotNull
    public MoveVector evaluateDirection(@NotNull Node moveableNode, @NotNull Edge connectionEdge, @Nullable Edge firstAdjacentEdge) {

        if (guard.getConflict().isConflictRelated(moveableNode)) {
            return new MoveVector(0, 0);
        }

        EnhancedList<Direction> directions = getAdjacentEdgeDirections(moveableNode, connectionEdge);
        if (directions.allMatch(Direction::isHorizontal)) {

        }
        else if (directions.allMatch(Direction::isVertical)) {

        }
        else if (directions.size() == 2) {

        }
        else {
            // return evaluateSingleNodeDirection(moveableNode, connectionEdge);
        }

        Direction currentDirection = connectionEdge.getDirection(moveableNode);

        double diff = connectionEdge.getDiffDeltaXY();

        OctilinearDirection adjacentEdgeDirection = Optional.ofNullable(firstAdjacentEdge)
                .map(edge -> edge.getDirection(moveableNode).toOctilinear())
                .orElse(guard.getDisplaceDirection());

        switch (adjacentEdgeDirection) {
            case NORTH:
            case SOUTH: {
                double beta = NORTH.getAngleTo(currentDirection);
                if ((beta > 45 && beta < 90) || (beta > 135 && beta < 225) || (beta > 270 && beta < 335)) {
                    //return new AdjustmentDirection(NORTH, moveableNode, diff);

                }
                //return new AdjustmentDirection(SOUTH, moveableNode, diff);
            }
            case EAST:
            case WEST: {
                double beta = EAST.getAngleTo(currentDirection);
                if ((beta > 45 && beta < 90) || (beta > 135 && beta < 225) || (beta > 270 && beta < 335)) {
                    //return new AdjustmentDirection(WEST, moveableNode, diff);
                }
                //return new AdjustmentDirection(EAST, moveableNode, diff);
            }
//            case NORTH_EAST:
//            case SOUTH_WEST: {
//                double beta = NORTH_EAST.getAngleTo(currentDirection);
//                if (beta > 90 && beta < 270) {
//                    // TODO handle displace direction
//                    return new AdjustmentDirection(SOUTH_WEST, moveableNode, diff);
//                }
//                return new AdjustmentDirection(NORTH_EAST, moveableNode, diff);
//            }
//            case SOUTH_EAST:
//            case NORTH_WEST: {
//                double beta = SOUTH_EAST.getAngleTo(currentDirection);
//                if (beta > 90 && beta < 270) {
//                    // TODO handle displace direction
//                    return new AdjustmentDirection(NORTH_WEST, moveableNode, diff);
//                }
//                return new AdjustmentDirection(SOUTH_EAST, moveableNode, diff);
//            }
        }

        // Contracts.fail("Should never reach this point...");
        //return new AdjustmentDirection(NORTH, moveableNode, 0);

        return new MoveVector(0, 0);

    }

}
