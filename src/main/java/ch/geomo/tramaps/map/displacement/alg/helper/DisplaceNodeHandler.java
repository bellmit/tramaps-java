/*
 * Copyright (c) 2016 Thomas Zuberbuehler. All rights reserved.
 */

package ch.geomo.tramaps.map.displacement.alg.helper;

import ch.geomo.tramaps.conflict.Conflict;
import ch.geomo.tramaps.geom.Axis;
import ch.geomo.tramaps.graph.Node;
import ch.geomo.tramaps.graph.util.OctilinearDirection;
import ch.geomo.tramaps.map.MetroMap;
import ch.geomo.util.Loggers;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static ch.geomo.tramaps.graph.util.OctilinearDirection.EAST;
import static ch.geomo.tramaps.graph.util.OctilinearDirection.NORTH;

public class DisplaceNodeHandler {

    private final MetroMap map;
    private final Conflict conflict;

    public DisplaceNodeHandler(@NotNull MetroMap map, @NotNull Conflict conflict) {
        this.map = map;
        this.conflict = conflict;
    }

    private boolean isDisplaceableToNorth(@NotNull Node node) {
        return node.getPoint().getY() > conflict.getSamplePointOnDisplaceLine().y;
    }

    private boolean isDisplaceableToEast(@NotNull Node node) {
        return node.getPoint().getX() > conflict.getSamplePointOnDisplaceLine().x;
    }

    @NotNull
    public DisplaceNodeResult displace() {

        if (conflict.getBestDisplaceAxis() == Axis.X) {

            List<Node> displacedNodes = map.getNodes().stream()
                    .filter(this::isDisplaceableToEast)
                    .collect(Collectors.toList());

            displacedNodes.forEach(node -> {
                if (!conflict.isConflictRelated(node)
                        && node.getNodeDegree() == 1
                        && node.getAdjacentEdges().stream().noneMatch(edge -> displacedNodes.contains(edge.getOtherNode(node)))) {
                    // ignore single node when not conflict related and the adjacent edge
                    // was not moved in this iteration
                    Loggers.info(this, "Ignore single node " + node.getName() + ".");
                }
                else {
                    Loggers.flag(this, "Displace node " + node.getName() + " eastwards.");
                    node.updateX(node.getX() + conflict.getBestDisplaceLength());
                }
            });

            return new DisplaceNodeResult(EAST, displacedNodes, conflict.getBestDisplaceLength());

        }

        List<Node> displacedNodes = map.getNodes().stream()
                .filter(this::isDisplaceableToNorth)
                .collect(Collectors.toList());

        displacedNodes.forEach(node -> {
            if (!conflict.isConflictRelated(node)
                    && node.getNodeDegree() == 1
                    && node.getAdjacentEdges().stream().noneMatch(edge -> displacedNodes.contains(edge.getOtherNode(node)))) {
                // ignore single node when not conflict related and the adjacent edge
                // was not moved in this iteration
                Loggers.info(this, "Ignore single node " + node.getName() + ".");
            }
            else {
                Loggers.flag(this, "Displace node " + node.getName() + " to northwards.");
                node.updateY(node.getY() + conflict.getBestDisplaceLength());
            }
        });

        return new DisplaceNodeResult(NORTH, displacedNodes, conflict.getBestDisplaceLength());

    }

    // todo should be refactored to a non-static class
    public static class DisplaceNodeResult {

        private final OctilinearDirection displaceDirection;
        private final List<Node> displacedNodes;
        private final double displaceDistance;

        public DisplaceNodeResult(@NotNull OctilinearDirection displaceDirection, @NotNull List<Node> displacedNodes, double displaceDistance) {
            this.displaceDirection = displaceDirection;
            this.displacedNodes = new ArrayList<>(displacedNodes);
            this.displaceDistance = displaceDistance;
        }

        public OctilinearDirection getDisplaceDirection() {
            return displaceDirection;
        }

        public List<Node> getDisplacedNodes() {
            return displacedNodes;
        }

        public double getDisplaceDistance() {
            return displaceDistance;
        }

    }

}