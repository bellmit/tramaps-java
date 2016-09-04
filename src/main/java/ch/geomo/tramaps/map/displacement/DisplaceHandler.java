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

public class DisplaceHandler implements MetroMapLineSpaceHandler {

    private static final int MAX_ITERATIONS = 500;
    private static final double MAX_ADJUSTMENT_COSTS = 5;
    private static final double CORRECT_CIRCLE_PENALTY = 1000;

    @NotNull
    private List<Node> evaluateMoveableNodes(@NotNull Node firstNode, @NotNull MetroMap map, @NotNull Conflict conflict, @NotNull OctilinearDirection displacementDirection) {

        Point centroid = conflict.getG().getCentroid();

        if (displacementDirection == OctilinearDirection.NORTH) {
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

        Loggers.info(this, "Correct edge " + edge.getName() + ".");

        DisplaceGuard guardA = new DisplaceGuard(map, conflict, evaluateMoveableNodes(edge.getNodeA(), map, conflict, displacementDirection));
        DisplaceGuard guardB = new DisplaceGuard(map, conflict, evaluateMoveableNodes(edge.getNodeB(), map, conflict, displacementDirection));

        double scoreNodeA = calculateAdjustmentCosts(edge, edge.getNodeA(), guardA);
        double scoreNodeB = calculateAdjustmentCosts(edge, edge.getNodeB(), guardB);

        Loggers.info(this, "Adjustment Costs for nodes: [" + scoreNodeA + "/" + scoreNodeB + "]");

        correctEdgeByIntroducingBendNodes(edge, map);

        if (scoreNodeA > MAX_ADJUSTMENT_COSTS && scoreNodeB > MAX_ADJUSTMENT_COSTS) {
            correctEdgeByIntroducingBendNodes(edge, map);
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
                .filter(edge -> !Direction.isOctilinear(edge.getAngle()))
                .forEach(edge -> correctNonOctilinearEdge(edge, map, conflict, displacementDirection));

        map.getEdges().stream()
                .filter(edge -> !Direction.isOctilinear(edge.getAngle()))
                .forEach(edge -> System.out.println(edge));

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
    private OctilinearDirection moveNode(@NotNull Edge connectionEdge, @NotNull Node moveableNode, double displacementLength, @NotNull OctilinearDirection lastMoveDirection) {

        Direction moveDirection = lastMoveDirection;

        if (isSimpleNode(connectionEdge, moveableNode)) {

            if (moveableNode.getDegree() == 1) {
                moveDirection = lastMoveDirection;
            }
            else {

                // get first edge
                Edge adjacentEdge = moveableNode.getAdjacentEdgeStream(connectionEdge)
                        .findFirst()
                        // should never reach this point
                        .orElseThrow(IllegalStateException::new);

                Direction direction = adjacentEdge.getDirection(moveableNode);
                if (!OctilinearDirection.isOctilinear(direction)) {
                    System.out.println("shiit");
                }
                double angle = direction.getAngleTo(connectionEdge.getDirection());
                if ((angle > 45 && angle < 135) || (angle > 225 && angle < 315)) {
                    direction = direction.opposite();
                }
                moveDirection =  direction;

            }

        }


        Loggers.info(this, "Move node " + moveableNode.getName() + " to " + moveDirection + " (" + displacementLength + ").");

        OctilinearDirection octilinearMoveDirection = moveDirection.toOctilinear();
        moveableNode.move(octilinearMoveDirection, displacementLength);
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

        OctilinearDirection movedDirection = moveNode(edge, moveableNode, guard.getMoveDistance(), lastMoveDirection);

        List<Edge> nonOctilinearEdges = moveableNode.getAdjacentEdges().stream()
                .filter(Edge::isNonOctilinear)
                .filter(edge::isNotEquals)
                .collect(Collectors.toList());

        for (Edge nonOctilinearEdge : nonOctilinearEdges) {
            Node otherNode = nonOctilinearEdge.getOtherNode(moveableNode);
            correctEdgeByMovingNode(nonOctilinearEdge, otherNode, movedDirection, guard);
        }

    }

    private boolean isSimpleNode(@NotNull Edge connectionEdge, @NotNull Node node) {

        Direction originalDirection = connectionEdge.getDirection(node).toOctilinear();

        List<Direction> directions = node.getAdjacentEdgeStream(connectionEdge)
                .map(Edge::getDirection)
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

        System.out.println("Iteration: " + count);
        System.out.println("Conflicts found: " + conflicts.size());

        if (!conflicts.isEmpty()) {

            Conflict conflict = conflicts.get(0);

            Point centroid = conflict.getConflictPolygon().getCentroid();

            if (conflict.getBestDisplacementAxis() == Axis.X) {
                map.getNodes().stream()
                        .filter(node -> node.getPoint().getX() > centroid.getX())
                        .forEach(node -> node.updateX(node.getX() + conflict.getBestDisplacementLengthAlongAxis()));

                correctNonOctilinearEdges(map, conflict, OctilinearDirection.NORTH);
            }
            else {
                map.getNodes().stream()
                        .filter(node -> node.getPoint().getY() > centroid.getY())
                        .forEach(node -> node.updateY(node.getY() + conflict.getBestDisplacementLengthAlongAxis()));

                correctNonOctilinearEdges(map, conflict, OctilinearDirection.WEST);
            }

            if (count < MAX_ITERATIONS) {
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
        Loggers.info(this, "Result Map: " + map);
    }

}
