/*
 * Copyright (c) 2016 Thomas Zuberbuehler. All rights reserved.
 */

package ch.geomo.tramaps.map.displacement.alg.adjustment;

import ch.geomo.tramaps.graph.Edge;
import ch.geomo.tramaps.graph.Node;
import ch.geomo.tramaps.graph.util.Direction;
import ch.geomo.tramaps.graph.util.OctilinearDirection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

import static ch.geomo.tramaps.graph.util.OctilinearDirection.*;

public class AdjustmentDirectionEvaluator {

    public AdjustmentDirection evaluateDirection(@NotNull Node moveableNode,
                                                 @NotNull Edge connectionEdge,
                                                 @Nullable Edge firstAdjacentEdge,
                                                 @NotNull AdjustmentGuard guard) {

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
                    return new AdjustmentDirection(NORTH, moveableNode, diff);

                }
                return new AdjustmentDirection(SOUTH, moveableNode, diff);
            }
            case EAST:
            case WEST: {
                double beta = EAST.getAngleTo(currentDirection);
                if ((beta > 45 && beta < 90) || (beta > 135 && beta < 225) || (beta > 270 && beta < 335)) {
                    return new AdjustmentDirection(WEST, moveableNode, diff);
                }
                return new AdjustmentDirection(EAST, moveableNode, diff);
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
        return new AdjustmentDirection(NORTH, moveableNode, 0);

    }

}
