/*
 * Copyright (c) 2016 Thomas Zuberbuehler. All rights reserved.
 */

package ch.geomo.tramaps.map.displacement.alg.helper;

import ch.geomo.tramaps.graph.Edge;
import ch.geomo.tramaps.graph.Node;
import ch.geomo.tramaps.graph.util.Direction;
import ch.geomo.tramaps.graph.util.OctilinearDirection;
import ch.geomo.util.Contracts;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

import static ch.geomo.tramaps.graph.util.OctilinearDirection.*;

public class MoveNodeHandler {

    public MoveNodeDirection evaluateNode2(@NotNull Node moveableNode,
                                           @NotNull Edge connectionEdge,
                                           @Nullable Edge firstAdjacentEdge,
                                           @NotNull MoveNodeGuard guard) {

        Direction currentDirection = connectionEdge.getDirection(moveableNode);

        double dx = connectionEdge.getDeltaX();
        double dy = connectionEdge.getDeltaY();
        double diff = Math.abs(dx - dy);

        OctilinearDirection adjacentEdgeDirection = Optional.ofNullable(firstAdjacentEdge)
                .map(edge -> edge.getDirection(moveableNode).toOctilinear())
                .orElse(guard.getDisplaceDirection());

        switch (adjacentEdgeDirection) {
            case NORTH:
            case SOUTH: {
                double beta = NORTH.getAngleTo(currentDirection);
                if ((beta > 45 && beta < 90) || (beta > 135 && beta < 225) || (beta > 270 && beta < 335)) {
                    return new MoveNodeDirection(SOUTH, moveableNode, diff);

                }
                return new MoveNodeDirection(NORTH, moveableNode, diff);
            }
            case EAST:
            case WEST: {
                double beta = EAST.getAngleTo(currentDirection);
                if ((beta > 45 && beta < 90) || (beta > 135 && beta < 225) || (beta > 270 && beta < 335)) {
                    return new MoveNodeDirection(WEST, moveableNode, diff);
                }
                return new MoveNodeDirection(EAST, moveableNode, diff);
            }
//            case NORTH_EAST:
//            case SOUTH_WEST: {
//                double beta = NORTH_EAST.getAngleTo(currentDirection);
//                if (beta > 90 && beta < 270) {
//                    // TODO handle displace direction
//                    return new MoveNodeDirection(SOUTH_WEST, moveableNode, diff);
//                }
//                return new MoveNodeDirection(NORTH_EAST, moveableNode, diff);
//            }
//            case SOUTH_EAST:
//            case NORTH_WEST: {
//                double beta = SOUTH_EAST.getAngleTo(currentDirection);
//                if (beta > 90 && beta < 270) {
//                    // TODO handle displace direction
//                    return new MoveNodeDirection(NORTH_WEST, moveableNode, diff);
//                }
//                return new MoveNodeDirection(SOUTH_EAST, moveableNode, diff);
//            }
        }

        // Contracts.fail("Should never reach this point...");
        return new MoveNodeDirection(NORTH, moveableNode, 0);

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
