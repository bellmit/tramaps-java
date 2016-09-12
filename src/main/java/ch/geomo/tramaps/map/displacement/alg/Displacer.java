/*
 * Copyright (c) 2016 Thomas Zuberbuehler. All rights reserved.
 */

package ch.geomo.tramaps.map.displacement.alg;

import ch.geomo.tramaps.conflict.Conflict;
import ch.geomo.tramaps.conflict.ConflictType;
import ch.geomo.tramaps.geom.Axis;
import ch.geomo.tramaps.graph.Node;
import ch.geomo.tramaps.map.MetroMap;
import ch.geomo.tramaps.map.displacement.radius.RadiusDisplacer;
import ch.geomo.util.Loggers;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static ch.geomo.tramaps.graph.util.OctilinearDirection.EAST;
import static ch.geomo.tramaps.graph.util.OctilinearDirection.NORTH;

public class Displacer {

    private final MetroMap map;
    private final Conflict conflict;
    private final List<Conflict> otherConflicts;

    public Displacer(@NotNull MetroMap map, @NotNull Conflict conflict, @NotNull List<Conflict> otherConflicts) {
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
    public DisplaceResult displace() {

        if (conflict.getConflictType() == ConflictType.ADJACENT_NODE_NODE_DIAGONAL) {

            RadiusDisplacer radiusDisplacer = new RadiusDisplacer(map, conflict);
            radiusDisplacer.displace();

            return new DisplaceResult(NORTH, new ArrayList<>(map.getNodes()), conflict, otherConflicts);

        }

        if (conflict.getBestDisplaceAxis() == Axis.X) {

            List<Node> displacedNodes = map.getNodes().stream()
                    .filter(this::isDisplaceableToEast)
                    .collect(Collectors.toList());

            displacedNodes.forEach(node -> {
                Loggers.flag(this, "Displace node " + node.getName() + " eastwards (distance=" + conflict.getBestDisplaceDistance() + ").");
                node.updateX(node.getX() + conflict.getBestDisplaceDistance());
            });

            return new DisplaceResult(EAST, displacedNodes, conflict, otherConflicts);

        }

        List<Node> displacedNodes = map.getNodes().stream()
                .filter(this::isDisplaceableToNorth)
                .collect(Collectors.toList());

        displacedNodes.forEach(node -> {
            Loggers.flag(this, "Displace node " + node.getName() + " to northwards (distance=" + conflict.getBestDisplaceDistance() + ").");
            node.updateY(node.getY() + conflict.getBestDisplaceDistance());
        });

        return new DisplaceResult(NORTH, displacedNodes, conflict, otherConflicts);

    }

}
