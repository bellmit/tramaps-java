/*
 * Copyright (c) 2016 Thomas Zuberbuehler. All rights reserved.
 */

package ch.geomo.tramaps.map.displacement.alg;

import ch.geomo.tramaps.conflict.BufferConflict;
import ch.geomo.tramaps.conflict.Conflict;
import ch.geomo.tramaps.graph.Node;
import ch.geomo.tramaps.graph.util.OctilinearDirection;
import ch.geomo.util.collection.GCollection;
import ch.geomo.util.collection.list.EnhancedList;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Transfer object with information of the displacement step.
 */
public class NodeDisplaceResult {

    private final Conflict conflict;
    private final EnhancedList<Conflict> otherConflicts;
    private final OctilinearDirection displaceDirection;
    private final EnhancedList<Node> displacedNodes;
    private final double displaceDistance;

    public NodeDisplaceResult(@NotNull OctilinearDirection displaceDirection,
                              @NotNull EnhancedList<Node> displacedNodes,
                              @NotNull Conflict conflict,
                              @NotNull EnhancedList<Conflict> otherConflicts) {

        this.displaceDirection = displaceDirection;
        this.displacedNodes = GCollection.list(displacedNodes);
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
    public EnhancedList<Node> getDisplacedNodes() {
        return displacedNodes;
    }

    public double getDisplaceDistance() {
        return displaceDistance;
    }

}
