/*
 * Copyright (c) 2016 Thomas Zuberbuehler. All rights reserved.
 */

package ch.geomo.tramaps.map.displacement.alg;

import ch.geomo.tramaps.conflict.BufferConflict;
import ch.geomo.tramaps.conflict.Conflict;
import ch.geomo.tramaps.graph.Edge;
import ch.geomo.tramaps.graph.Node;
import ch.geomo.tramaps.graph.direction.OctilinearDirection;
import ch.geomo.tramaps.map.MetroMap;
import ch.geomo.util.collection.GCollection;
import ch.geomo.util.collection.list.EnhancedList;
import ch.geomo.util.collection.set.EnhancedSet;
import com.vividsolutions.jts.geom.Coordinate;
import org.jetbrains.annotations.NotNull;

import static ch.geomo.tramaps.graph.direction.OctilinearDirection.EAST;
import static ch.geomo.tramaps.graph.direction.OctilinearDirection.NORTH;

/**
 * Displace nodes based on the given {@link BufferConflict}.
 */
public class NodeDisplacer {

    private final MetroMap map;
    private final Conflict conflict;
    private final OctilinearDirection displaceDirection;

    // future improvement: introduce factory class in order to reuse instances
    public NodeDisplacer(@NotNull MetroMap map, @NotNull Conflict conflict) {
        this.map = map;
        this.conflict = conflict;
        displaceDirection = conflict.getBestDisplaceDirection();
    }

    /**
     * @return true if given direction is equals to the displace direction
     */
    private boolean isDisplaceDirection(@NotNull OctilinearDirection direction) {
        return displaceDirection == direction;
    }

    /**
     * Checks if given node is adjacent to a connection edge and (re-)evaluates if given node is displaceable.
     * @return true if displaceable
     */
    private boolean checkConnectionEdge(@NotNull Node node, boolean displaceable) {
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
                throw new IllegalStateException("Should never happen since this node has a connection edge!");
            }
            Node otherNode = connectionEdge.getOtherNode(node);
            if (isSimpleConnectionEdgeNode(node, connectionEdge)) {
                if ((otherNode.isNorthOf(node) && isDisplaceDirection(NORTH))
                        || (otherNode.isEastOf(node) && isDisplaceDirection(EAST))) {
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

    /**
     * @return true if node can be moved northwards.
     */
    private boolean isDisplaceableToNorth(@NotNull Node node) {
        boolean displaceable = node.getPoint().getY() > conflict.getDisplaceOriginPoint().y;
        return checkConnectionEdge(node, displaceable);
    }

    /**
     * @return true if node can be moved eastwards.
     */
    private boolean isDisplaceableToEast(@NotNull Node node) {
        boolean displaceable = node.getPoint().getX() > conflict.getDisplaceOriginPoint().x;
        return checkConnectionEdge(node, displaceable);
    }

    /**
     * Starts the displacement process and displace nodes according to {@link #isDisplaceableToNorth(Node)}
     * respectively {@link #isDisplaceableToEast(Node)}.
     */
    public void displace() {

        EnhancedList<Node> displacedNodes = GCollection.list();

        if (isDisplaceDirection(EAST)) {
            map.getNodes().stream()
                    .filter(this::isDisplaceableToEast)
                    .forEach(node -> {
                        node.updateX(node.getX() + conflict.getDisplaceDistanceAlongX());
                        displacedNodes.add(node);
                    });
        }
        else { // NORTH
            map.getNodes().stream()
                    .filter(this::isDisplaceableToNorth)
                    .forEach(node -> {
                        node.updateY(node.getY() + conflict.getDisplaceDistanceAlongY());
                        displacedNodes.add(node);
                    });
        }

    }

    /**
     * @return true if given edge is a connection edge
     */
    private boolean isConnectionEdge(@NotNull Edge edge) {
        Coordinate displaceOriginPoint = conflict.getDisplaceOriginPoint();
        if (isDisplaceDirection(NORTH)) {
            return edge.getNodeA().isNorthOf(displaceOriginPoint) && edge.getNodeB().isSouthOf(displaceOriginPoint)
                    || edge.getNodeB().isNorthOf(displaceOriginPoint) && edge.getNodeA().isSouthOf(displaceOriginPoint);
        }
        return edge.getNodeA().isEastOf(displaceOriginPoint) && edge.getNodeB().isWestOf(displaceOriginPoint)
                || edge.getNodeB().isEastOf(displaceOriginPoint) && edge.getNodeA().isWestOf(displaceOriginPoint);
    }

    /**
     * @return true if given node has at least one adjacent connection edge
     */
    private boolean hasConnectionEdge(@NotNull Node node) {
        return node.getAdjacentEdges().anyMatch(this::isConnectionEdge);
    }

    /**
     * @return a set of connection edges
     */
    @NotNull
    private EnhancedSet<Edge> getConnectionEdges(@NotNull Node node) {
        return node.getAdjacentEdges(this::isConnectionEdge);
    }

    /**
     * Starts the displacement process and displace nodes according to {@link #isDisplaceableToNorth(Node)}
     * respectively {@link #isDisplaceableToEast(Node)}.
     * <p>
     * Creates internally a new instance of {@link NodeDisplacer} and invokes {@link NodeDisplacer#displace()}.
     * @see NodeDisplacer#displace()
     */
    public static void displace(@NotNull MetroMap map, @NotNull Conflict conflict) {
        new NodeDisplacer(map, conflict).displace();
    }

}
