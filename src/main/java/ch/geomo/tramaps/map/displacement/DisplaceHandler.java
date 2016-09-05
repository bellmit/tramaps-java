/*
 * Copyright (c) 2016 Thomas Zuberbuehler. All rights reserved.
 */

package ch.geomo.tramaps.map.displacement;

import ch.geomo.tramaps.conflict.Conflict;
import ch.geomo.tramaps.geo.Axis;
import ch.geomo.tramaps.graph.Edge;
import ch.geomo.tramaps.graph.Node;
import ch.geomo.tramaps.graph.Route;
import ch.geomo.tramaps.graph.layout.OctilinearEdge;
import ch.geomo.tramaps.graph.layout.OctilinearEdgeBuilder;
import ch.geomo.tramaps.graph.util.Direction;
import ch.geomo.tramaps.graph.util.OctilinearDirection;
import ch.geomo.tramaps.map.MetroMap;
import ch.geomo.tramaps.map.signature.BendNodeSignature;
import ch.geomo.util.Loggers;
import ch.geomo.util.pair.Pair;
import com.vividsolutions.jts.geom.Point;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static ch.geomo.tramaps.graph.util.OctilinearDirection.*;

public class DisplaceHandler implements MetroMapLineSpaceHandler {

    private static final int MAX_ITERATIONS = 100;
    private static final double MAX_ADJUSTMENT_COSTS = 5;
    private static final double CORRECT_CIRCLE_PENALTY = 1000;

    @NotNull
    private List<Node> evaluateMoveableNodes(@NotNull Node firstNode, @NotNull MetroMap map, @NotNull Conflict conflict, @NotNull OctilinearDirection displacementDirection) {

        Point centroid = conflict.getG().getCentroid();

        if (displacementDirection == NORTH) {
            return map.getNodes().stream()
                    .filter(node -> {
                        if (firstNode.getX() < centroid.getX()) {
                            return node.getX() < centroid.getX();
                        }
                        return node.getX() > centroid.getX();
                    })
                    .collect(Collectors.toList());
        }

        return map.getNodes().stream()
                .filter(node -> {
                    if (firstNode.getY() < centroid.getY()) {
                        return node.getY() < centroid.getY();
                    }
                    return node.getY() > centroid.getY();
                })
                .collect(Collectors.toList());

    }

    private void correctNonOctilinearEdge(@NotNull Edge edge, @NotNull MetroMap map, @NotNull Conflict conflict, @NotNull OctilinearDirection displacementDirection) {

        Loggers.info(this, map.toString());

        Loggers.info(this, "Correct edge " + edge.getName() + ".");

        DisplaceGuard guardA = new DisplaceGuard(map, conflict, evaluateMoveableNodes(edge.getNodeA(), map, conflict, displacementDirection));
        DisplaceGuard guardB = new DisplaceGuard(map, conflict, evaluateMoveableNodes(edge.getNodeB(), map, conflict, displacementDirection));

        double scoreNodeA = calculateAdjustmentCosts(edge, edge.getNodeA(), guardA);
        double scoreNodeB = calculateAdjustmentCosts(edge, edge.getNodeB(), guardB);

        Loggers.info(this, "Adjustment Costs for nodes: [" + scoreNodeA + "/" + scoreNodeB + "]");

        if (scoreNodeA > MAX_ADJUSTMENT_COSTS && scoreNodeB > MAX_ADJUSTMENT_COSTS) {
            Loggers.info(this, "Adjustment Costs too high... a bend is required!");
//            correctEdgeByIntroducingBendNodes(edge, map);
        }
        else {
            OctilinearDirection lastMoveDirection = conflict.getBestDisplacementDirection();
            if (scoreNodeA < scoreNodeB) {
                correctEdgeByMovingNode(edge, edge.getNodeA(), lastMoveDirection, guardA.reuse());
            }
            else {
                correctEdgeByMovingNode(edge, edge.getNodeB(), lastMoveDirection, guardB.reuse());
            }
        }

    }

    private void correctNonOctilinearEdges(@NotNull MetroMap map, @NotNull Conflict conflict, @NotNull OctilinearDirection displacementDirection) {

        Loggers.info(this, "Non-Octilinear edges: " + map.evaluateNonOctilinearEdges().count());
        map.getEdges().stream()
                .filter(edge -> !edge.getDirection(null).isOctilinear())
                .forEach(edge -> correctNonOctilinearEdge(edge, map, conflict, displacementDirection));

        map.getEdges().stream()
                .filter(edge -> !edge.getDirection(null).isOctilinear())
                .forEach(edge -> Loggers.warning(this, "Uncorrected non-Octilinear edge: " + edge.getName()));

    }

    /**
     * Merges two points. First given {@link Point} will be kept while the second {@link Point} will be
     * removed. Adjacent edges will be transferred. Possible duplications (edges with
     * same nodes) removed. Both nodes must be a bend node otherwise nothing will be merged.
     *
     * @return true if merged
     */
    private boolean mergeBendNodes(@NotNull Node fixedNode, @NotNull Node obsoleteNode, @NotNull MetroMap map) {

        if (!(fixedNode.getNodeSignature() instanceof BendNodeSignature) && !(obsoleteNode.getNodeSignature() instanceof BendNodeSignature)) {
            // merging not possible
            return false;
        }

        // add adjacent edges to fixed node
        obsoleteNode.getAdjacentEdges().forEach(edge -> {
            Node otherNode = edge.getOtherNode(obsoleteNode);
            fixedNode.createAdjacentEdgeTo(otherNode, edge.getRoutes());
        });

        // merge duplicate edges
        fixedNode.getAdjacentEdges().stream()
                .flatMap(e1 -> fixedNode.getAdjacentEdges().stream()
                        .map(e2 -> Pair.of(e1, e2)))
                .filter(p -> p.first().equalNodes(p.second()))
                .forEach(p -> {
                    Set<Route> routes = p.second().getRoutes();
                    p.first().addRoutes(routes);
                });

        // remove obsolete nodes and it's adjacent edges
        obsoleteNode.delete();

        // numbers of nodes and edges may have changed
        map.updateGraph();

        return true;

    }


    /**
     * Introduces a bend node for given {@link Edge}. The given {@link Edge} instance
     * will be destroyed.
     */
    private void correctEdgeByIntroducingBendNodes(@NotNull Edge edge, @NotNull MetroMap map) {

        // create octilinear edge
        OctilinearEdge octilinearEdge = new OctilinearEdgeBuilder()
                .setOriginalEdge(edge)
                .setGraph(map)
                .build();

        Pair<Node> vertices = octilinearEdge.getVertices();

        if (vertices.hasNonNullValues()) {

            // only one vertex
            if (vertices.second() == null) {
                map.addNodes(vertices.first());
                edge.getNodeA()
                        .createAdjacentEdgeTo(vertices.first(), edge.getRoutes())
                        .createAdjacentEdgeTo(edge.getNodeB(), edge.getRoutes());
            }
            else {
                map.addNodes(vertices.first(), vertices.second());
                edge.getNodeA()
                        .createAdjacentEdgeTo(vertices.first(), edge.getRoutes())
                        .createAdjacentEdgeTo(vertices.second(), edge.getRoutes())
                        .createAdjacentEdgeTo(edge.getNodeB(), edge.getRoutes());
            }

            Loggers.info(this, "Octilinear edge created: " + edge);

            // remove old edge
            edge.delete();

            // numbers of nodes has changed, edge cache must be flagged for rebuild
            map.updateGraph();

        }
        else {
            Loggers.warning(this, "No octilinear edge created: " + edge);
        }

    }

    /**
     * Moves given {@link Node} in a certain direction to correct the given {@link Edge}'s
     * octilinearity. Prefers to move in the given (last) move direction if two choices
     * are equal weighted.
     *
     * @return the applied move direction
     */
    @NotNull
    private OctilinearDirection moveNode(@NotNull Edge connectionEdge, @NotNull Node moveableNode, double lastMoveDistance, @NotNull OctilinearDirection lastMoveDirection, @NotNull MetroMap map) {

        Direction moveDirection = lastMoveDirection;
        double moveDistance = lastMoveDistance;

        if (isSimpleNode(connectionEdge, moveableNode)) {

            Loggers.info(this, "Move node " + moveableNode.getName() + ".");

            if (moveableNode.getDegree() != 1) {

                // get first edge
                Edge adjacentEdge = moveableNode.getAdjacentEdgeStream(connectionEdge)
                        .peek(edge -> Loggers.info(this, "Adjacent Edge " + edge.getName()))
                        .findFirst()
                        // should never reach this point
                        .orElseThrow(IllegalStateException::new);

                OctilinearDirection firstAdjacentEdgeDirection = adjacentEdge.getOriginalDirection(moveableNode).toOctilinear();
                OctilinearDirection octilinearConnectionEdgeDirection = connectionEdge.getOriginalDirection(moveableNode).toOctilinear();

                // angle between first adjacent edge and the non-octilinear direction of the connection edge
                double angle = firstAdjacentEdgeDirection.getAngleTo(connectionEdge.getDirection(moveableNode));

                if (firstAdjacentEdgeDirection.getAlignment() != octilinearConnectionEdgeDirection.getAlignment()) {

                    switch (firstAdjacentEdgeDirection) {
                        case SOUTH: {
                            if (angle > 315 || angle < 45) {
                                moveDirection = NORTH;
                            }
                            else {
                                moveDirection = SOUTH;
                            }
                            break;
                        }
                        case NORTH: {
                            if (angle > 315 || angle < 45) {
                                moveDirection = SOUTH;
                            }
                            else {
                                moveDirection = NORTH;
                            }
                            break;
                        }
                        case EAST: {
                            if ((angle > 45 && angle < 90) || (angle > 135 && angle < 180) || (angle > 225 && angle < 270) || angle > 335) {
                                moveDirection = WEST;
                            }
                            else {
                                moveDirection = EAST;
                            }
                        }
                        case WEST: {
                            if ((angle > 45 && angle < 90) || (angle > 135 && angle < 180) || (angle > 225 && angle < 270) || angle > 335) {
                                moveDirection = EAST;
                            }
                            else {
                                moveDirection = WEST;
                            }
                            break;
                        }
                        default: {
                            if (angle < 45 || angle > 90) {
                                moveDirection = firstAdjacentEdgeDirection;
                                // TODO
                                moveDistance = 0;
                            }
                            else {
                                moveDirection = firstAdjacentEdgeDirection.opposite();
                                // TODO
                                moveDistance = 0;
                            }
                            break;
                        }
                    }

                }
                else {
                    Loggers.info(this, "Do not move Node " + moveableNode.getName() + ".");
                    moveDirection = firstAdjacentEdgeDirection.opposite();
                    moveDistance = 0d;
                }

            }

        }
        else {
            Loggers.info(this, "Node " + moveableNode.getName() + " is too complex to move!");
            moveDistance = 0d;
        }

        final double correctDistance = moveDistance;
        OctilinearDirection octilinearMoveDirection = moveDirection.toOctilinear();

        boolean isMoveable = moveableNode.getAdjacentEdgeStream(connectionEdge)
                .noneMatch(adjEdge -> adjEdge.getDirection(moveableNode).toOctilinear() == octilinearMoveDirection && adjEdge.getLength() < correctDistance);

//        System.out.println(correctDistance);
//
//        moveableNode.getAdjacentEdgeStream(connectionEdge)
//                .filter(adjEdge -> adjEdge.getDirection(moveableNode).toOctilinear() == octilinearMoveDirection)
//                .peek(e -> System.out.println(e.getLength()))
//                .filter(adjEdge -> adjEdge.getLength() < correctDistance)
//                .map(adjEdge -> adjEdge.getOtherNode(moveableNode))
//                .forEach(node -> {
//                    Loggers.info(this, "Correction would produce an intersection with another node... Move other node too.");
//                    node.move(octilinearMoveDirection, correctDistance);
//                    Loggers.info(this, "New position for " + moveableNode + ".");
//                });

        if (isMoveable) {
            Loggers.info(this, "Apply correction: Move node " + moveableNode.getName() + " to " + moveDirection + " (" + correctDistance + ").");
            moveableNode.move(octilinearMoveDirection, correctDistance);
            Loggers.info(this, "New position for " + moveableNode + ".");
        }
        else {
            Loggers.info(this, "Node " + moveableNode.getName() + " is not moveable!");
        }

        return octilinearMoveDirection;

    }

    /**
     * Corrects the direction of given {@link Edge} recursively by moving the given {@link Node}.
     */
    private void correctEdgeByMovingNode(@NotNull Edge edge, @NotNull Node moveableNode, @NotNull OctilinearDirection lastMoveDirection, @NotNull DisplaceGuard guard) {

        if (guard.isNotMoveable(moveableNode)) {
            Loggers.warning(this, "Node " + moveableNode.getName() + " cannot be moved!");
            return;
        }

        if (guard.hasAlreadyVisited(moveableNode)) {
            Loggers.warning(this, "Correct edge aborted due to a second visit of node " + moveableNode.getName() + "!");
            return;
        }
        guard.visited(moveableNode);

        OctilinearDirection movedDirection = moveNode(edge, moveableNode, guard.getMoveDistance(), lastMoveDirection, guard.getMetroMap());

        List<Edge> nonOctilinearEdges = moveableNode.getAdjacentEdges().stream()
                .filter(Edge::isNonOctilinear)
                .filter(edge::isNotEquals)
                // .peek(e -> Loggers.info(this, e + ", " + e.getDirection()))
                .collect(Collectors.toList());

        for (Edge nonOctilinearEdge : nonOctilinearEdges) {
            Node otherNode = nonOctilinearEdge.getOtherNode(moveableNode);
            correctEdgeByMovingNode(nonOctilinearEdge, otherNode, movedDirection, guard);
        }

    }

    private boolean isSimpleNode(@NotNull Edge connectionEdge, @NotNull Node node) {

        Direction originalDirection = connectionEdge.getDirection(node).toOctilinear();

        List<Direction> directions = node.getAdjacentEdgeStream(connectionEdge)
                .map(edge -> edge.getDirection(node))
                .collect(Collectors.toList());

        if (directions.size() == 0) {
            return true;
        }
        else if (directions.size() > 2) {
            return false;
        }
        else if (directions.stream().anyMatch(originalDirection::isOpposite)) {
            return false;
        }

        return directions.size() == 1 || directions.get(0).isOpposite(directions.get(1));

    }

    /**
     * Calculates the costs to adjust given {@link Edge} by moving given {@link Node}. The {@link List} of traversed
     * nodes is needed to avoid correction circles.
     * <p>
     * Note: The {@link List} of traversed nodes is not synchronized.
     */
    private double calculateAdjustmentCosts(@NotNull Edge connectionEdge, @NotNull Node node, @NotNull DisplaceGuard guard) {

        if (guard.isNotMoveable(node) || guard.hasAlreadyVisited(node)) {
            return CORRECT_CIRCLE_PENALTY;
        }

        guard.visited(node);

        if (node.getAdjacentEdges().size() == 1) {
            return 0;
        }

        Set<Edge> adjacentEdges = node.getAdjacentEdges().stream()
                .filter(edge -> !edge.equals(connectionEdge))
                .collect(Collectors.toSet());

        if (isSimpleNode(connectionEdge, node)) {
            return 1;
        }

        double costs = 1 + adjacentEdges.size();

        for (Edge adjacentEdge : adjacentEdges) {
            Node otherNode = adjacentEdge.getOtherNode(node);
            costs = costs + calculateAdjustmentCosts(adjacentEdge, otherNode, guard);
        }

        return costs;

    }

    private void makeSpace(MetroMap map, double routeMargin, double edgeMargin, int count) {

        count++;

        List<Conflict> conflicts = map.evaluateConflicts(routeMargin, edgeMargin, true)
                .collect(Collectors.toList());

        Loggers.info(this, "Iteration: " + count);
        Loggers.info(this, "Conflicts found: " + conflicts.size());

        if (!conflicts.isEmpty()) {

            Conflict conflict = conflicts.get(0);

            Point centroid = conflict.getConflictPolygon().getCentroid();

            if (conflict.getBestDisplacementAxis() == Axis.X) {
                map.getNodes().stream()
                        .filter(node -> node.getPoint().getX() > centroid.getX())
                        .forEach(node -> node.updateX(node.getX() + conflict.getBestDisplacementLengthAlongAxis()));

                correctNonOctilinearEdges(map, conflict, NORTH);
            }
            else {
                map.getNodes().stream()
                        .filter(node -> node.getPoint().getY() > centroid.getY())
                        .forEach(node -> node.updateY(node.getY() + conflict.getBestDisplacementLengthAlongAxis()));

                correctNonOctilinearEdges(map, conflict, WEST);
            }

            if (count <= MAX_ITERATIONS) {
                makeSpace(map, routeMargin, edgeMargin, count);
            }
            else {
                Loggers.warning(this, "Abort -> max. iteration reached!");
            }

        }

        map.evaluateConflicts(routeMargin, edgeMargin, true)
                .forEach(c -> Loggers.warning(this, "Conflict not solved: " + c));

    }

    @Override
    public void makeSpace(@NotNull MetroMap map, double routeMargin, double edgeMargin) {
        Loggers.info(this, "Initial Map: " + map);
        makeSpace(map, routeMargin, edgeMargin, 0);
//        map.getEdges().stream()
//                .filter(edge -> !edge.getDirection(null).isOctilinear())
//                .forEach(edge -> correctEdgeByIntroducingBendNodes(edge, map));
        Loggers.info(this, "Result Map: " + map);
    }

}
