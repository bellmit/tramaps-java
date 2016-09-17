/*
 * Copyright (c) 2016 Thomas Zuberbuehler. All rights reserved.
 */

package ch.geomo.tramaps.map.displacement.alg;

import ch.geomo.tramaps.conflict.Conflict;
import ch.geomo.tramaps.graph.Node;
import ch.geomo.tramaps.graph.util.OctilinearDirection;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Transfer object with information of the displacement step.
 */
public class DisplaceResult {

    private final Conflict conflict;
    private final List<Conflict> otherConflicts;
    private final OctilinearDirection displaceDirection;
    private final List<Node> displacedNodes;
    private final double displaceDistance;

    public DisplaceResult(@NotNull OctilinearDirection displaceDirection,
                          @NotNull List<Node> displacedNodes,
                          @NotNull Conflict conflict,
                          @NotNull List<Conflict> otherConflicts) {

        this.displaceDirection = displaceDirection;
        this.displacedNodes = new ArrayList<>(displacedNodes);
        displaceDistance = conflict.getBestDisplaceDistance();
        this.conflict = conflict;
        this.otherConflicts = otherConflicts;

    }

    @NotNull
    public Conflict getConflict() {
        return conflict;
    }

    @NotNull
    public List<Conflict> getOtherConflicts() {
        return otherConflicts;
    }

    @NotNull
    public OctilinearDirection getDisplaceDirection() {
        return displaceDirection;
    }

    @NotNull
    public List<Node> getDisplacedNodes() {
        return displacedNodes;
    }

    public double getDisplaceDistance() {
        return displaceDistance;
    }

}
