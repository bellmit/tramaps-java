/*
 * Copyright (c) 2016 Thomas Zuberbuehler. All rights reserved.
 */

package ch.geomo.tramaps.map.displacement.alg;

import ch.geomo.tramaps.conflict.Conflict;
import ch.geomo.tramaps.geom.Axis;
import ch.geomo.tramaps.graph.Edge;
import ch.geomo.tramaps.graph.Node;
import ch.geomo.tramaps.graph.util.OctilinearDirection;
import ch.geomo.tramaps.map.MetroMap;
import ch.geomo.util.Contracts;
import ch.geomo.util.collection.list.EnhancedList;
import ch.geomo.util.collection.set.EnhancedSet;
import ch.geomo.util.collection.GCollection;
import com.vividsolutions.jts.geom.Coordinate;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static ch.geomo.tramaps.graph.util.OctilinearDirection.*;

public class Displacer {

    private final MetroMap map;
    private final Conflict conflict;
    private final List<Conflict> otherConflicts;

    private final OctilinearDirection displaceDirection;

    public Displacer(@NotNull MetroMap map, @NotNull Conflict conflict, @NotNull List<Conflict> otherConflicts) {

        this.map = map;
        this.conflict = conflict;
        this.otherConflicts = otherConflicts;

        if (conflict.getBestDisplaceAxis() == Axis.X) {
            displaceDirection = EAST;
        }
        else {
            displaceDirection = NORTH;
        }

    }

    private boolean isDisplaceDirection(OctilinearDirection direction) {
        return displaceDirection == direction;
    }

    // TODO find better method name
    private boolean isInDisplaceDirection(@NotNull Node node, @NotNull Node otherNode) {
        return (otherNode.isNorthOf(node) && isDisplaceDirection(NORTH))
                || (otherNode.isEastOf(node) && isDisplaceDirection(EAST));
    }

    private boolean checkConnectionEdge(@NotNull Node node, final boolean displaceable) {
        if (hasConnectionEdge(node) && !conflict.isConflictRelated(node)) {
            if (node.getNodeDegree() == 1) {
                return !displaceable;
            }
            EnhancedSet<Edge> connectionEdges = getConnectionEdges(node);
            if (connectionEdges.hasMoreThanOneElement()) {
                return displaceable;
            }
            Edge connectionEdge = connectionEdges.first().orElse(null);
            if (connectionEdge == null) {
                Contracts.fail("Should never happen since node has a connection edge!");
            }
            Node otherNode = connectionEdge.getOtherNode(node);
            if (isSimpleConnectionEdgeNode(node, connectionEdge)) {
                if (isInDisplaceDirection(node, otherNode)) {
                    return false;
                }
                return displaceable || otherNode.getNodeDegree() != 1;
            }
        }
        return displaceable;
    }

    /**
     * @return true if given node is simple to move
     */
    private boolean isSimpleConnectionEdgeNode(@NotNull Node node, @NotNull Edge connectionEdge) {
        return node.getAdjacentEdges().stream()
                .filter(connectionEdge::isNotEquals)
                .map(edge -> edge.getOriginalDirection(node))
                .noneMatch(direction -> {
                    if (isDisplaceDirection(NORTH)) {
                        return !direction.isVertical();
                    }
                    return !direction.isHorizontal();
                });
    }

    private boolean isDisplaceableToNorth(@NotNull Node node) {
        boolean displaceable = node.getPoint().getY() > conflict.getDisplaceOriginPoint().y;
        return checkConnectionEdge(node, displaceable);
    }

    private boolean isDisplaceableToEast(@NotNull Node node) {
        boolean displaceable = node.getPoint().getX() > conflict.getDisplaceOriginPoint().x;
        return checkConnectionEdge(node, displaceable);
    }

    @NotNull
    public DisplaceResult displace() {

        EnhancedList<Node> displacedNodes = GCollection.list();

        if (conflict.getBestDisplaceAxis() == Axis.X) {
            map.getNodes().stream()
                    .filter(this::isDisplaceableToEast)
                    .forEach(node -> {
                        node.updateX(node.getX() + conflict.getDisplaceDistanceAlongX());
                        displacedNodes.add(node);
                    });
        }
        else {
            map.getNodes().stream()
                    .filter(this::isDisplaceableToNorth)
                    .forEach(node -> {
                        node.updateY(node.getY() + conflict.getDisplaceDistanceAlongY());
                        displacedNodes.add(node);
                    });
        }

        return new DisplaceResult(displaceDirection, displacedNodes, conflict, otherConflicts);

    }

    private boolean isConnectionEdge(@NotNull Edge edge) {
        Coordinate displaceOriginPoint = conflict.getDisplaceOriginPoint();
        if (displaceDirection == NORTH) {
            return edge.getNodeA().isNorthOf(displaceOriginPoint) && edge.getNodeB().isSouthOf(displaceOriginPoint)
                    || edge.getNodeB().isNorthOf(displaceOriginPoint) && edge.getNodeA().isSouthOf(displaceOriginPoint);
        }
        return edge.getNodeA().isEastOf(displaceOriginPoint) && edge.getNodeB().isWestOf(displaceOriginPoint)
                || edge.getNodeB().isEastOf(displaceOriginPoint) && edge.getNodeA().isWestOf(displaceOriginPoint);
    }

    private boolean hasConnectionEdge(@NotNull Node node) {
        return node.getAdjacentEdges().anyMatch(this::isConnectionEdge);
    }

    @NotNull
    private EnhancedSet<Edge> getConnectionEdges(@NotNull Node node) {
        return node.getAdjacentEdges(this::isConnectionEdge);
    }

}
