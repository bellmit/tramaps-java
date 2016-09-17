/*
 * Copyright (c) 2016 Thomas Zuberbuehler. All rights reserved.
 */

package ch.geomo.tramaps.map.displacement.alg.adjustment;

import ch.geomo.tramaps.graph.Node;
import ch.geomo.tramaps.graph.util.OctilinearDirection;
import org.jetbrains.annotations.NotNull;

public class AdjustmentDirection {

    private final OctilinearDirection moveDirection;
    private final double moveDistance;
    private final Node moveableNode;

    public AdjustmentDirection(@NotNull OctilinearDirection moveDirection, @NotNull Node moveableNode, double moveDistance) {
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
