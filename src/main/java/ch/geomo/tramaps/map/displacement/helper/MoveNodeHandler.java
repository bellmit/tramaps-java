/*
 * Copyright (c) 2016 Thomas Zuberbuehler. All rights reserved.
 */

package ch.geomo.tramaps.map.displacement.helper;

import ch.geomo.tramaps.graph.Edge;
import ch.geomo.tramaps.graph.Node;
import ch.geomo.tramaps.graph.util.OctilinearDirection;
import ch.geomo.util.Loggers;
import org.jetbrains.annotations.NotNull;

import static ch.geomo.tramaps.graph.util.OctilinearDirection.*;

public class MoveNodeHandler {

    public MoveNodeDirection evaluateNonConflictRelated(@NotNull Node moveableNode,
                                                        @NotNull MoveNodeGuard guard,
                                                        @NotNull OctilinearDirection connectionEdgeDirection,
                                                        @NotNull OctilinearDirection firstAdjacentEdgeDirection,
                                                        double angle) {

//        OctilinearDirection moveDirection = firstAdjacentEdgeDirection;
//
//        double octilinearAngle = firstAdjacentEdgeDirection.getAngleTo(connectionEdgeDirection);
//
//        if (angle > octilinearAngle) {
//            moveDirection = firstAdjacentEdgeDirection.opposite();
//        }
//
//        return new MoveNodeDirection(moveDirection, moveableNode, guard.getLastMoveDistance());
        return new MoveNodeDirection(guard.getLastMoveDirection(), moveableNode, 0);



    }

    public MoveNodeDirection evaluateConflictRelated(@NotNull Node moveableNode,
                                                     @NotNull MoveNodeGuard guard,
                                                     @NotNull OctilinearDirection connectionEdgeDirection,
                                                     @NotNull OctilinearDirection firstAdjacentEdgeDirection,
                                                     double angle) {

//        MoveNodeDirection result = evaluateNonConflictRelated(moveableNode, guard, connectionEdgeDirection, firstAdjacentEdgeDirection, angle);
//        if (result.getMoveDirection() != guard.getDisplaceDirection().opposite()) {
//            return result;
//        }

        return new MoveNodeDirection(guard.getLastMoveDirection(), moveableNode, 0);

    }

    // TODO check/correct this use case
    public MoveNodeDirection evaluateSingleNode(@NotNull Edge connectionEdge,
                                                @NotNull Node moveableNode,
                                                @NotNull OctilinearDirection lastMoveDirection,
                                                @NotNull OctilinearDirection octilinearConnectionEdgeDirection) {

        Node otherNode = connectionEdge.getOtherNode(moveableNode);
        double dx = Math.abs(moveableNode.getX() - otherNode.getX());
        double dy = Math.abs(moveableNode.getY() - otherNode.getY());

        OctilinearDirection moveDirection = lastMoveDirection;

        switch (lastMoveDirection) {
            case NORTH:
            case SOUTH:
            case WEST:
            case EAST: {
                switch (octilinearConnectionEdgeDirection) {
                    case NORTH_WEST:
                    case SOUTH_EAST: {
                        moveDirection = NORTH;
                        break;
                    }
                    default: {
                        moveDirection = SOUTH;
                    }
                }
                break;
            }
        }

        if (lastMoveDirection == EAST || lastMoveDirection == SOUTH) {
            return new MoveNodeDirection(moveDirection.opposite(), moveableNode, dx - dy);
        }
        return new MoveNodeDirection(moveDirection, moveableNode, dx - dy);

    }

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

    }

}
