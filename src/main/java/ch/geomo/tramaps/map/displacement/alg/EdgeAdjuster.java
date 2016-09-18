/*
 * Copyright (c) 2016 Thomas Zuberbuehler. All rights reserved.
 */

package ch.geomo.tramaps.map.displacement.alg;

import ch.geomo.tramaps.geom.MoveVector;
import ch.geomo.tramaps.graph.Edge;
import ch.geomo.tramaps.graph.Graph;
import ch.geomo.tramaps.graph.Node;
import ch.geomo.tramaps.graph.layout.OctilinearEdge;
import ch.geomo.tramaps.graph.layout.OctilinearEdgeBuilder;
import ch.geomo.tramaps.map.displacement.alg.adjustment.AdjustmentCostCalculator;
import ch.geomo.tramaps.map.displacement.alg.adjustment.AdjustmentDirectionEvaluator;
import ch.geomo.tramaps.map.displacement.alg.adjustment.AdjustmentGuard;
import ch.geomo.util.collection.pair.Pair;
import ch.geomo.util.geom.GeomUtil;
import ch.geomo.util.logging.Loggers;
import com.vividsolutions.jts.geom.Point;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Adjusts edges.
 */
public class EdgeAdjuster {

    private static final double MAX_ADJUSTMENT_COSTS = 100;

    private final Graph graph;
    private final Edge edge;
    private final NodeDisplaceResult displaceResult;

    public EdgeAdjuster(@NotNull Graph graph, @NotNull Edge edge, @NotNull NodeDisplaceResult displaceResult) {
        this.graph = graph;
        this.edge = edge;
        this.displaceResult = displaceResult;
    }

    private Node getNodeA() {
        return edge.getNodeA();
    }

    private Node getNodeB() {
        return edge.getNodeB();
    }

    private AdjustmentGuard createGuard(@NotNull Node startNode) {
        return new AdjustmentGuard(graph, displaceResult, startNode);
    }

    public void correctEdge() {

        Loggers.info(this, "Correct edge " + edge.getName() + "...");

        double scoreA = AdjustmentCostCalculator.calculate(edge, getNodeA(), createGuard(getNodeA()));
        double scoreB = AdjustmentCostCalculator.calculate(edge, getNodeB(), createGuard(getNodeB()));
        Loggers.info(this, "Adjustment Costs for nodes: [" + scoreA + "/" + scoreB + "]");

        if (scoreA > MAX_ADJUSTMENT_COSTS && scoreB > MAX_ADJUSTMENT_COSTS) {
            correctEdgeByIntroducingBendNodes();
        }
        else if (scoreA < scoreB) {
            correctEdgeByMovingNode(edge, getNodeA(), createGuard(getNodeA()));
        }
        else {
            correctEdgeByMovingNode(edge, getNodeB(), createGuard(getNodeB()));
        }

        Loggers.info(this, "Correction done.");

    }

    /**
     * Introduces a bend node for given {@link Edge}. The given {@link Edge} instance
     * will be destroyed.
     */
    private void correctEdgeByIntroducingBendNodes() {

        // create octilinear edge
        OctilinearEdge octilinearEdge = new OctilinearEdgeBuilder()
                .setOriginalEdge(edge)
                .build();

        Pair<Node> vertices = octilinearEdge.getVertices();

        Loggers.info(this, "Introduce bends " + vertices + " to edge " + edge.getName() + "...");

        if (vertices.hasNonNullValues()) {

            // only one vertex
            if (vertices.second() == null) {
                graph.addNodes(vertices.first());
                edge.getNodeA()
                        .createAdjacentEdgeTo(vertices.first(), edge.getRoutes())
                        .createAdjacentEdgeTo(edge.getNodeB(), edge.getRoutes());
            }
            else {
                graph.addNodes(vertices.first(), vertices.second());
                edge.getNodeA()
                        .createAdjacentEdgeTo(vertices.first(), edge.getRoutes())
                        .createAdjacentEdgeTo(vertices.second(), edge.getRoutes())
                        .createAdjacentEdgeTo(edge.getNodeB(), edge.getRoutes());
            }

            Loggers.info(this, "Octilinear edge created: " + edge);

            // remove old edge
            edge.destroy();

            // numbers set nodes has changed, edge cache must be flagged for rebuild
            graph.updateGraph();

        }
        else {
            Loggers.warning(this, "No octilinear edge created: " + edge);
        }

    }

    /**
     * Moves given {@link Node} in a certain direction to correct the given {@link Edge}'s
     * octilinearity. Prefers to move in the given (last) move direction if two choices
     * are equal weighted.
     */
    private void moveNode(@NotNull Edge connectionEdge, @NotNull Node moveableNode, @NotNull AdjustmentGuard guard) {

        AdjustmentDirectionEvaluator adjustmentDirectionEvaluator = guard.getNodeAdjustmentDirectionEvaluator();
        MoveVector moveVector;

        if (AdjustmentCostCalculator.isSimpleNode(connectionEdge, moveableNode)) {

            Loggers.info(this, "Move node " + moveableNode.getName() + ".");

            // get first edge
            Optional<Edge> adjacentEdge = moveableNode.getAdjacentEdges(connectionEdge).first();

            // evaluate direction
            if (adjacentEdge.isPresent()) {
                moveVector = adjustmentDirectionEvaluator.evaluateDirection(moveableNode, connectionEdge, adjacentEdge.get());
            }
            else {
                Loggers.info(this, "Evaluate single node move direction...");
                // moveVector = adjustmentDirectionEvaluator.evaluateSingleNodeDirection(moveableNode, connectionEdge);
                moveVector = new MoveVector(0, 0);
            }

        }
        else {
            Loggers.flag(this, "Node " + moveableNode.getName() + " is too complex to move!");
            moveVector = new MoveVector(0, 0);
        }

        Loggers.info(this, "Evaluated move vector: " + moveVector);
        if (moveVector.getX() == 0 && moveVector.getY() == 0) {
            Loggers.info(this, "Vector's length is 0. Do not move node.");
        }

        Point movePoint = GeomUtil.createMovePoint(moveableNode.getPoint(), moveVector);

        // test if new position would intersect with any other edge when moving -> if so, we do not move
        boolean overlapsOtherEdges = moveableNode.getAdjacentEdges().stream()
                .map(edge -> edge.getOtherNode(moveableNode))
                .map(node -> GeomUtil.createLineString(movePoint, node.getPoint()))
                .anyMatch(lineString -> guard.getGraph().getEdges().stream()
                        .filter(edge -> !moveableNode.getAdjacentEdges().contains(edge))
                        .anyMatch(edge -> {
                            if (edge.getLineString().relate(lineString, "T********")) {
                                Loggers.warning(this, "Edge " + edge.getName() + " would be intersecting with " + connectionEdge.getName() + "!");
                                return true;
                            }
                            return false;
                        }));


        // evaluates if moving the node does not overlap another node after moving, otherwise we won't move the node
        boolean overlapsAdjacentNode = moveableNode.getAdjacentEdges(connectionEdge)
                .anyMatch(adjEdge -> adjEdge.getLength() <= moveVector.length());

        // test if new position is equals to a position set another node
        boolean overlapsOtherNodes = graph.getNodes().stream()
                .filter(moveableNode::isNotEquals)
                .map(Node::getCoordinate)
                .anyMatch(coordinate -> movePoint.getCoordinate().equals(coordinate));

        boolean notEqualPosition = GeomUtil.createLineString(movePoint, connectionEdge.getOtherNode(moveableNode).getPoint()).getLength() > 0;
        if (!notEqualPosition) {
            Loggers.warning(this, "It seems that the new position is equals to the adjacent node position.");
        }

        if (!overlapsAdjacentNode && !overlapsOtherNodes && !overlapsOtherEdges && notEqualPosition) {
            Loggers.flag(this, "Move node " + moveableNode.getName() + " using vector " + moveVector + ".");
            moveableNode.updatePosition(movePoint.getCoordinate());
            Loggers.info(this, "New position for Node " + moveableNode.getName() + ".");
        }
        else {
            Loggers.info(this, "Node " + moveableNode.getName() + " is not moveable!");
        }


//        // evaluates if moving the node does not overlap another node after moving, otherwise we won't move the node
//        boolean overlapsAdjacentNode = moveableNode.getAdjacentEdgeStream(connectionEdge)
//                .anyMatch(adjEdge -> {
//                    if (adjEdge.getDirection(moveableNode).toOctilinear() != octilinearMoveDirection) {
//                        return false;
//                    }
//                    Node otherNode = adjEdge.getOtherNode(moveableNode);
//                    if (adjEdge.getLength() > correctDistance) {
//                        return false;
//                    }
//                    Coordinate coordinate = movePoint.getCoordinate();
//                    switch (result.getMoveDirection()) {
//                        case EAST:
//                            return !otherNode.isEastOf(coordinate);
//                        case NORTH:
//                            return !otherNode.isNorthOf(coordinate);
//                        case SOUTH:
//                            return !otherNode.isSouthOf(coordinate);
//                        case WEST:
//                            return !otherNode.isWestOf(coordinate);
//                        case NORTH_EAST:
//                            return !otherNode.isNorthEastOf(coordinate);
//                        case NORTH_WEST:
//                            return !otherNode.isNorthWestOf(coordinate);
//                        case SOUTH_EAST:
//                            return !otherNode.isSouthEastOf(coordinate);
//                        case SOUTH_WEST:
//                            return !otherNode.isSouthWestOf(coordinate);
//                        default: {
//                            Contracts.fail();
//                            return true;
//                        }
//                    }
//                });
//
//        // test if new position is equals to a position set another node
//        boolean overlapsOtherNodes = graph.getNodes().stream()
//                .filter(moveableNode::isNotEquals)
//                .map(Node::getCoordinate)
//                .anyMatch(coordinate -> movePoint.getCoordinate().equals(coordinate));
//
//        // test if new position would intersect with any other edge when moving -> if so, we do not move
//        boolean overlapsOtherEdges = moveableNode.getAdjacentEdgeStream(null)
//                .map(edge -> edge.getOtherNode(moveableNode))
//                .map(node -> GeomUtil.createLineString(movePoint, node.getPoint()))
//                .anyMatch(lineString -> guard.getGraph().getEdges().stream()
//                        .filter(edge -> !moveableNode.getAdjacentEdges().contains(edge))
//                        .anyMatch(edge -> {
//                            if (edge.getLineString().relate(lineString, "T********")) {
//                                Loggers.warning(this, "Edge " + edge.getName() + " would be intersecting with " + connectionEdge.getName() + "!");
//                                return true;
//                            }
//                            return false;
//                        }));
//
//        boolean notEqualPosition = GeomUtil.createLineString(movePoint, connectionEdge.getOtherNode(moveableNode).getPoint()).getLength() > 0;
//        if (!notEqualPosition) {
//            Loggers.warning(this, "It seems that the new position is equals to the adjacent node position.");
//        }
//
//        if (!overlapsAdjacentNode && !overlapsOtherNodes && !overlapsOtherEdges && notEqualPosition) {
//            Loggers.flag(this, "Move node " + moveableNode.getName() + " to " + octilinearMoveDirection + " (distance=" + correctDistance + ").");
//            moveableNode.updatePosition(movePoint);
//            Loggers.info(this, "New position for Node " + moveableNode.getName() + ".");
//        }
//        else {
//            Loggers.info(this, "Node " + moveableNode.getName() + " is not moveable!");
//        }
//
//        // update guard
//        guard.setLastMoveDirection(octilinearMoveDirection);
//        guard.setLastMoveDistance(correctDistance);

    }

    /**
     * Corrects the direction set given {@link Edge} recursively by moving the given {@link Node}.
     */
    private void correctEdgeByMovingNode(@NotNull Edge edge, @NotNull Node moveableNode, @NotNull AdjustmentGuard guard) {

        if (guard.hasAlreadyVisited(moveableNode)) {
            Loggers.warning(this, "Correct edge aborted due to a second visit set node " + moveableNode.getName() + "!");
            return;
        }

        guard.visited(moveableNode);

        moveNode(edge, moveableNode, guard);

        List<Edge> nonOctilinearEdges = moveableNode.getAdjacentEdges().stream()
                .filter(Edge::isNotOctilinear)
                .filter(edge::isNotEquals)
                .collect(Collectors.toList());

        for (Edge nonOctilinearEdge : nonOctilinearEdges) {
            Node otherNode = nonOctilinearEdge.getOtherNode(moveableNode);
            correctEdgeByMovingNode(nonOctilinearEdge, otherNode, guard);
        }

    }

}
