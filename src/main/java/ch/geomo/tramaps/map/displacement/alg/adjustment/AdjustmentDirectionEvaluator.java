/*
 * Copyright (c) 2016 Thomas Zuberbuehler. All rights reserved.
 */

package ch.geomo.tramaps.map.displacement.alg.adjustment;

import ch.geomo.tramaps.geom.MoveVector;
import ch.geomo.tramaps.graph.Edge;
import ch.geomo.tramaps.graph.Node;
import ch.geomo.tramaps.graph.util.Direction;
import ch.geomo.tramaps.graph.util.OctilinearDirection;
import ch.geomo.util.Contracts;
import ch.geomo.util.collection.GCollectors;
import ch.geomo.util.collection.list.EnhancedList;
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

    private double getSmallerAngle(double angle) {
        double result = Math.abs(angle % 180);
        if (result > 45) {
            return Math.abs(result - 180);
        }
        return result;
    }

    @NotNull
    public MoveVector evaluateSingleNodeDirection(@NotNull Node moveableNode, @NotNull Edge connectionEdge) {

        if (guard.getConflict().isConflictRelated(moveableNode)) {
            return new MoveVector(0, 0);
        }

        Node otherNode = connectionEdge.getOtherNode(moveableNode);

        double diff = connectionEdge.getDiffDeltaXY();

        OctilinearDirection originalDirection = connectionEdge.getOriginalDirection(otherNode).toOctilinear();
        double angle = originalDirection.getAngleTo(connectionEdge.getDirection(otherNode));

        switch (originalDirection) {
            case NORTH_EAST: {
                if (angle > 0) {
                    return new MoveVector(0, -diff);
                }
                return new MoveVector(diff, 0);
            }
            case SOUTH_EAST: {
                if (angle > 0) {
                    return new MoveVector(diff, 0);
                }
                return new MoveVector(0, -diff);
            }
            case SOUTH_WEST: {
                if (angle > 0) {
                    return new MoveVector(0, -diff);
                }
                return new MoveVector(-diff, 0);
            }
            case NORTH_WEST: {
                if (angle > 0) {
                    return new MoveVector(-diff, 0);
                }
                return new MoveVector(0, diff);
            }
            default: {
                Contracts.fail("Should not reach this point.");
                return new MoveVector(0, 0);
            }
        }

    }

    @NotNull
    public EnhancedList<OctilinearDirection> getAdjacentEdgeDirections(@NotNull Node node, @NotNull Edge connectionEdge) {
        return node.getAdjacentEdges().stream()
                .filter(connectionEdge::isNotEquals)
                .map(edge -> edge.getOriginalDirection(node).toOctilinear())
                .collect(GCollectors.toList());
    }

    @NotNull
    public MoveVector evaluateDirection(@NotNull Node moveableNode, @NotNull Edge connectionEdge, @Nullable Edge firstAdjacentEdge) {

        if (guard.getConflict().isConflictRelated(moveableNode)) {
            return new MoveVector(0, 0);
        }

        Node otherNode = connectionEdge.getOtherNode(moveableNode);

        double dx = connectionEdge.getDeltaX();
        double dy = connectionEdge.getDeltaY();
        double diff = connectionEdge.getDiffDeltaXY();

        OctilinearDirection originalDirection = connectionEdge.getOriginalDirection(otherNode).toOctilinear();
        double angle = originalDirection.getAngleTo(connectionEdge.getDirection(otherNode));

        EnhancedList<OctilinearDirection> directions = getAdjacentEdgeDirections(moveableNode, connectionEdge);
        if (directions.allMatch(Direction::isVertical)) {
//            switch (originalDirection) {
//                case NORTH_EAST:
//                case SOUTH_EAST: {
//                    if (angle > 0) {
//                        return new MoveVector(0, diff);
//                    }
//                    return new MoveVector(0, -diff);
//                }
//                case SOUTH_WEST:
//                case NORTH_WEST: {
//                    if (angle > 0) {
//                        return new MoveVector(0, -diff);
//                    }
//                    return new MoveVector(0, diff);
//                }
//                default: {
//                    Contracts.fail("Should not reach this point.");
//                    return new MoveVector(0, 0);
//                }
//            }
        }
        else if (directions.allMatch(Direction::isHorizontal)) {
//            switch (originalDirection) {
//                case NORTH_EAST:
//                case NORTH_WEST: {
//                    if (angle > 0) {
//                        return new MoveVector(0, diff);
//                    }
//                    return new MoveVector(0, -diff);
//                }
//                case SOUTH_WEST:
//                case SOUTH_EAST: {
//                    if (angle > 0) {
//                        return new MoveVector(0, -diff);
//                    }
//                    return new MoveVector(0, diff);
//                }
//                default: {
//                    Contracts.fail("Should not reach this point.");
//                    return new MoveVector(0, 0);
//                }
//            }
        }
        else if (directions.size() == 2) {

        }
        else {
            // return evaluateSingleNodeDirection(moveableNode, connectionEdge);
        }

        Direction currentDirection = connectionEdge.getDirection(moveableNode);

//        double diff = connectionEdge.getDiffDeltaXY();

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
