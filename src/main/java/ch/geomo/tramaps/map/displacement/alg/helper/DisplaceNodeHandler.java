/*
 * Copyright (c) 2016 Thomas Zuberbuehler. All rights reserved.
 */

package ch.geomo.tramaps.map.displacement.alg.helper;

import ch.geomo.tramaps.conflict.Conflict;
import ch.geomo.tramaps.conflict.ConflictType;
import ch.geomo.tramaps.geom.Axis;
import ch.geomo.tramaps.graph.Node;
import ch.geomo.tramaps.map.MetroMap;
import ch.geomo.tramaps.map.displacement.radius.DisplaceRadiusNodeHandler;
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
    private final List<Conflict> otherConflicts;

    public DisplaceNodeHandler(@NotNull MetroMap map, @NotNull Conflict conflict, @NotNull List<Conflict> otherConflicts) {
        this.map = map;
        this.conflict = conflict;
        this.otherConflicts = otherConflicts;
    }

    private boolean isDisplaceableToNorth(@NotNull Node node) {
        boolean displaceable = node.getPoint().getY() > conflict.getSamplePointOnDisplaceLine().y;
        return displaceable && (!conflict.isConflictElement(node) || conflict.isDisplaceElement(node));
    }

    private boolean isDisplaceableToEast(@NotNull Node node) {
        boolean displaceable = node.getPoint().getX() > conflict.getSamplePointOnDisplaceLine().x;
        return displaceable && (!conflict.isConflictElement(node) || conflict.isDisplaceElement(node));
    }

    @NotNull
    public DisplaceNodeResult displace() {

        if (conflict.getConflictType() == ConflictType.ADJACENT_NODE_NODE_DIAGONAL) {

            DisplaceRadiusNodeHandler displaceRadiusNodeHandler = new DisplaceRadiusNodeHandler(map, conflict);
            displaceRadiusNodeHandler.displace();

            return new DisplaceNodeResult(NORTH, new ArrayList<>(map.getNodes()), conflict, otherConflicts);

        }

        if (conflict.getBestDisplaceAxis() == Axis.X) {

            List<Node> displacedNodes = map.getNodes().stream()
                    .filter(this::isDisplaceableToEast)
                    .collect(Collectors.toList());

            displacedNodes.forEach(node -> {
                Loggers.flag(this, "Displace node " + node.getName() + " eastwards (distance=" + conflict.getBestDisplaceDistance() + ").");
                node.updateX(node.getX() + conflict.getBestDisplaceDistance());
            });

            return new DisplaceNodeResult(EAST, displacedNodes, conflict, otherConflicts);

        }

        List<Node> displacedNodes = map.getNodes().stream()
                .filter(this::isDisplaceableToNorth)
                .collect(Collectors.toList());


        if (displacedNodes.isEmpty()) {
            System.out.println("shiit");
        }

        displacedNodes.forEach(node -> {
            Loggers.flag(this, "Displace node " + node.getName() + " to northwards (distance=" + conflict.getBestDisplaceDistance() + ").");
            node.updateY(node.getY() + conflict.getBestDisplaceDistance());
        });

        return new DisplaceNodeResult(NORTH, displacedNodes, conflict, otherConflicts);

    }

}
