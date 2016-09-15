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
import ch.geomo.util.Loggers;
import com.vividsolutions.jts.geom.Coordinate;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.stream.Collectors;

import static ch.geomo.tramaps.graph.util.OctilinearDirection.EAST;
import static ch.geomo.tramaps.graph.util.OctilinearDirection.NORTH;

public class Displacer {

    private enum ConnectionEdgeType {
        /**
         * Move none set the nodes. The node on the displacement side
         * has only adjacent edges in displace direction or the
         * opposite direction, the other node is complex.
         */
        NON_MOVEABLE,
        /**
         * Move other node.
         */
        OTHER,
        /**
         * Move both nodes half set the displace distance. Both nodes has
         * only adjacent edges in displace direction or the opposite
         * direction.
         */
        EASY,
        /**
         * Move both nodes full distance. The node on the displacement
         * side is complex but not the node on the other side.
         */
        BOTH,
        /**
         * Only move node on the displacement side. Does require
         * an octilinear correction in a later step.
         */
        COMPLEX
    }

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

    private boolean isDisplaceableToNorth(@NotNull Node node) {
        return node.getPoint().getY() > conflict.getDisplaceOriginPoint().y;
        // boolean displaceable = node.getPoint().getY() > conflict.getDisplaceOriginPoint().y;
        // return displaceable && (!conflict.isConflictElement(node) || conflict.isDisplaceElement(node));
    }

    private boolean isDisplaceableToEast(@NotNull Node node) {
        return node.getPoint().getX() > conflict.getDisplaceOriginPoint().x;
        // boolean displaceable = node.getPoint().getX() > conflict.getDisplaceOriginPoint().x;
        // return displaceable && (!conflict.isConflictElement(node) || conflict.isDisplaceElement(node));
    }

    @NotNull
    public DisplaceResult displace() {

        if (displaceDirection == EAST) {

            List<Node> displacedNodes = map.getNodes().stream()
                    .filter(this::isDisplaceableToEast)
                    .collect(Collectors.toList());

            displacedNodes.forEach(node -> {

                Loggers.flag(this, "Displace node " + node.getName() + " eastwards (distance=" + conflict.getBestDisplaceDistance() + ").");

                if (hasConnectionEdge(node)) {

                    double distance = conflict.getBestDisplaceDistance();
                    ConnectionEdgeType type = getConnectionEdgeType(node);

                    switch (type) {
                        case NON_MOVEABLE: {
                            // do not move
                            break;
                        }
                        case EASY: {
                            distance = distance / 2;
                        }
                        case BOTH: {
                            node.updateX(node.getX() + distance);
                        }
                        case OTHER: {
                            Edge edge = getConnectionEdges(node).get(0);
                            Node otherNode = edge.getOtherNode(node);
                            otherNode.updateX(otherNode.getX() + distance);
                            Loggers.flag(this, "Displace node (other) " + otherNode.getName() + " eastwards (distance=" + distance + ").");
                            break;
                        }
                        case COMPLEX:
                        default: {
                            node.updateX(node.getX() + distance);
                        }
                    }
                }
                else {
                    node.updateX(node.getX() + conflict.getBestDisplaceDistance());
                }

            });

            return new DisplaceResult(displaceDirection, displacedNodes, conflict, otherConflicts);

        }

        List<Node> displacedNodes = map.getNodes().stream()
                .filter(this::isDisplaceableToNorth)
                .collect(Collectors.toList());

        displacedNodes.forEach(node -> {

            Loggers.flag(this, "Displace node " + node.getName() + " to northwards (distance=" + conflict.getBestDisplaceDistance() + ").");

            if (hasConnectionEdge(node)) {

                double distance = conflict.getBestDisplaceDistance();
                ConnectionEdgeType type = getConnectionEdgeType(node);

                switch (type) {
                    case NON_MOVEABLE: {
                        // do not move
                        break;
                    }
                    case EASY: {
                        distance = distance / 2;
                    }
                    case BOTH: {
                        node.updateY(node.getY() + distance);
                    }
                    case OTHER: {
                        Edge edge = getConnectionEdges(node).get(0);
                        Node otherNode = edge.getOtherNode(node);
                        otherNode.updateY(otherNode.getY() + distance);
                        Loggers.flag(this, "Displace node " + otherNode.getName() + " northwards (distance=" + distance + ").");
                        break;
                    }
                    case COMPLEX:
                    default: {
                        node.updateY(node.getY() + distance);
                    }
                }
            }
            else {
                node.updateY(node.getY() + conflict.getBestDisplaceDistance());
            }

        });

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
        return node.getAdjacentEdges().stream()
                .anyMatch(this::isConnectionEdge);
    }

    private List<Edge> getConnectionEdges(@NotNull Node node) {
        return node.getAdjacentEdges().stream()
                .filter(this::isConnectionEdge)
                .collect(Collectors.toList());
    }

    private ConnectionEdgeType getConnectionEdgeType(@NotNull Node node) {

        if (conflict.isConflictRelated(node)) {
            // worst case set a connection edge
            return ConnectionEdgeType.COMPLEX;
        }

        if (node.getNodeDegree() == 1) {
            return ConnectionEdgeType.NON_MOVEABLE;
        }

        List<Edge> connectionEdges = getConnectionEdges(node);
        if (connectionEdges.size() > 1) {
            return ConnectionEdgeType.COMPLEX;
        }

        if (!connectionEdges.get(0).getOriginalDirection(null).isDiagonal()) {
            return ConnectionEdgeType.COMPLEX;
        }

        Edge connectionEdge = connectionEdges.get(0);
        Node otherNode = connectionEdge.getOtherNode(node);
        if (otherNode.getNodeDegree() == 1) {
            return ConnectionEdgeType.OTHER;
        }

        boolean isSimpleNode = isSimple(node, connectionEdge);
        boolean isSimpleOtherNode = isSimple(otherNode, connectionEdge);

        if (isSimpleNode && isSimpleOtherNode) {
            return ConnectionEdgeType.EASY;
        }
        else if (isSimpleOtherNode) {
            return ConnectionEdgeType.BOTH;
        }
        else if (isSimpleNode) {
            return ConnectionEdgeType.NON_MOVEABLE;
        }
        return ConnectionEdgeType.COMPLEX;

    }

    /**
     * @return true if given node is simple to move
     */
    private boolean isSimple(@NotNull Node node, @NotNull Edge connectionEdge) {
        return node.getAdjacentEdges().stream()
                .filter(connectionEdge::isNotEquals)
                .noneMatch(edge -> {
                    if (displaceDirection == NORTH) {
                        return !edge.isVertical();
                    }
                    return !edge.isHorizontal();
                });
    }

}
