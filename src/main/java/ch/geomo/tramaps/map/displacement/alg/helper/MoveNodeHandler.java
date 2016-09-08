/*
 * Copyright (c) 2016 Thomas Zuberbuehler. All rights reserved.
 */

package ch.geomo.tramaps.map.displacement.alg.helper;

import ch.geomo.tramaps.graph.Node;
import ch.geomo.tramaps.graph.util.OctilinearDirection;
import ch.geomo.util.Contracts;
import org.jetbrains.annotations.NotNull;

import static ch.geomo.tramaps.graph.util.OctilinearDirection.*;

public class MoveNodeHandler {

    public MoveNodeDirection evaluateNode(@NotNull Node moveableNode,
                                          @NotNull MoveNodeGuard guard,
                                          @NotNull OctilinearDirection connectionEdgeDirection,
                                          @NotNull OctilinearDirection firstAdjacentEdgeDirection) {

        double moveDistance = guard.getMoveDistance();
        OctilinearDirection displaceDirection = guard.getDisplaceDirection();

        boolean eastDisplaceDirection = displaceDirection == EAST;
        boolean northDisplaceDirection = displaceDirection == NORTH;

        switch (firstAdjacentEdgeDirection) {
            case NORTH:
            case SOUTH: {
                if (eastDisplaceDirection) {
                    if (connectionEdgeDirection.isHorizontal()) {
                        return new MoveNodeDirection(NORTH, moveableNode, 0);
                    }
                    if (connectionEdgeDirection.isDiagonal135()) {
                        return new MoveNodeDirection(SOUTH, moveableNode, moveDistance);
                    }
                }
                return new MoveNodeDirection(NORTH, moveableNode, moveDistance);
            }
            case EAST:
            case WEST: {
                if (northDisplaceDirection) {
                    if (connectionEdgeDirection.isVertical()) {
                        return new MoveNodeDirection(EAST, moveableNode, 0);
                    }
                    if (connectionEdgeDirection.isDiagonal135()) {
                        return new MoveNodeDirection(WEST, moveableNode, moveDistance);
                    }
                }
                return new MoveNodeDirection(EAST, moveableNode, moveDistance);
            }
            case SOUTH_WEST:
            case NORTH_EAST: {
                if (connectionEdgeDirection.isHorizontal()) {
                    return new MoveNodeDirection(NORTH_EAST, moveableNode, 0);
                }
                return new MoveNodeDirection(NORTH_EAST, moveableNode, guard.getMoveDistance());
            }
            case NORTH_WEST:
            case SOUTH_EAST: {
                if (connectionEdgeDirection.isVertical()) {
                    return new MoveNodeDirection(NORTH_EAST, moveableNode, 0);
                }
                if (eastDisplaceDirection) {
                    return new MoveNodeDirection(SOUTH_EAST, moveableNode, guard.getMoveDistance());
                }
                return new MoveNodeDirection(NORTH_WEST, moveableNode, guard.getMoveDistance());
            }
        }

        Contracts.fail("Should never reach this point...");
        return new MoveNodeDirection(NORTH, moveableNode, 0);

    }


    public MoveNodeDirection evaluateConflictRelatedNode(@NotNull Node moveableNode,
                                                         @NotNull MoveNodeGuard guard,
                                                         @NotNull OctilinearDirection connectionEdgeDirection,
                                                         @NotNull OctilinearDirection firstAdjacentEdgeDirection) {

        // currently not different from a non-conflict related node
        return evaluateNode(moveableNode, guard, connectionEdgeDirection, firstAdjacentEdgeDirection);

    }

    public MoveNodeDirection evaluateSingleNode(@NotNull Node moveableNode,
                                                @NotNull MoveNodeGuard guard,
                                                @NotNull OctilinearDirection connectionEdgeDirection) {

        if (connectionEdgeDirection.isDiagonal()) {
            if (guard.getDisplaceDirection() == NORTH) {
                return evaluateNode(moveableNode, guard, connectionEdgeDirection, EAST);
            }
            return evaluateNode(moveableNode, guard, connectionEdgeDirection, NORTH);
        }
        if (!guard.isConflictElementRelated(moveableNode)) {
            return evaluateNode(moveableNode, guard, connectionEdgeDirection, guard.getDisplaceDirection().opposite());
        }
        return evaluateNode(moveableNode, guard, connectionEdgeDirection, guard.getDisplaceDirection());

    }

    // todo should be refactored to a non-static class
    public static class MoveNodeDirection {

        private OctilinearDirection moveDirection;
        private double moveDistance;
        private Node moveableNode;

        public MoveNodeDirection(@NotNull OctilinearDirection moveDirection, @NotNull Node moveableNode, double moveDistance) {
            this.moveDirection = moveDirection;
            this.moveDistance = moveDistance;
            this.moveableNode = moveableNode;
        }

        @NotNull
        public OctilinearDirection getMoveDirection() {
            return moveDirection;
        }

        public double getMoveDistance() {
            return moveDistance;
        }

        @NotNull
        public Node getMoveableNode() {
            return moveableNode;
        }

        @Override
        public String toString() {
            return "Node " + moveableNode.getName() + ": " + moveDirection + " (distance=" + moveDistance + ")";
        }

    }

}