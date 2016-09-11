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

        Direction direction = connectionEdge.getDirection(moveableNode);
        OctilinearDirection originalDirection = connectionEdge.getOriginalDirection(moveableNode).toOctilinear();

        double angle = originalDirection.getAngleTo(direction);
        double dx = connectionEdge.getDeltaX();
        double dy = connectionEdge.getDeltaY();
        double diff = Math.abs(dx - dy);

        boolean northDisplaceDirection = dy > dx;
        boolean eastDisplaceDirection = !northDisplaceDirection;

        OctilinearDirection adjacentEdgeDirection = Optional.ofNullable(firstAdjacentEdge)
                .map(edge -> edge.getDirection(moveableNode).toOctilinear())
                .orElse(northDisplaceDirection ? EAST : NORTH);

        switch (adjacentEdgeDirection) {
            case NORTH:
            case SOUTH: {
                if (eastDisplaceDirection) {
                    if (originalDirection.isHorizontal()) {
                        return new MoveNodeDirection(NORTH, moveableNode, 0);
                    }
                    if (originalDirection.isDiagonal135()) {
                        return new MoveNodeDirection(SOUTH, moveableNode, diff);
                    }
                }
                return new MoveNodeDirection(NORTH, moveableNode, diff);
            }
            case EAST:
            case WEST: {
                if (northDisplaceDirection) {
                    if (originalDirection.isVertical()) {
                        return new MoveNodeDirection(EAST, moveableNode, 0);
                    }
                    if (originalDirection.isDiagonal135()) {
                        return new MoveNodeDirection(WEST, moveableNode, diff);
                    }
                }
                return new MoveNodeDirection(EAST, moveableNode, diff);
            }
            case SOUTH_WEST:
            case NORTH_EAST: {
                if (originalDirection.isHorizontal()) {
                    return new MoveNodeDirection(NORTH_EAST, moveableNode, 0);
                }
                return new MoveNodeDirection(NORTH_EAST, moveableNode, guard.getMoveDistance());
            }
            case NORTH_WEST:
            case SOUTH_EAST: {
                if (originalDirection.isVertical()) {
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
