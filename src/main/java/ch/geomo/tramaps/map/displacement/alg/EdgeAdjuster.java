/*
 * Copyright (c) 2016 Thomas Zuberbuehler. All rights reserved.
 */

package ch.geomo.tramaps.map.displacement.alg;

import ch.geomo.tramaps.conflict.ConflictFinder;
import ch.geomo.tramaps.geom.MoveVector;
import ch.geomo.tramaps.graph.Edge;
import ch.geomo.tramaps.graph.Graph;
import ch.geomo.tramaps.graph.Node;
import ch.geomo.tramaps.graph.layout.OctilinearEdge;
import ch.geomo.tramaps.graph.layout.OctilinearEdgeBuilder;
import ch.geomo.tramaps.map.MetroMap;
import ch.geomo.tramaps.map.displacement.alg.adjustment.AdjustmentCostCalculator;
import ch.geomo.tramaps.map.displacement.alg.adjustment.AdjustmentDirectionEvaluator;
import ch.geomo.tramaps.map.displacement.alg.adjustment.AdjustmentGuard;
import ch.geomo.util.collection.pair.Pair;
import ch.geomo.util.geom.GeomUtil;
import ch.geomo.util.logging.Loggers;
import com.vividsolutions.jts.geom.Point;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Provides functionality to adjust a non-octilinear edge.
 */
public class EdgeAdjuster {

    private static final double MAX_ADJUSTMENT_COSTS = 100;

    private final MetroMap map;
    private final Edge edge;
    private final double maxAdjustmentCosts;

    public EdgeAdjuster(@NotNull MetroMap map, @NotNull Edge edge) {
        this(map, edge, MAX_ADJUSTMENT_COSTS);
    }

    public EdgeAdjuster(@NotNull MetroMap map, @NotNull Edge edge, double maxAdjustmentCosts) {
        this.map = map;
        this.edge = edge;
        this.maxAdjustmentCosts = maxAdjustmentCosts;
    }

    private Node getNodeA() {
        return edge.getNodeA();
    }

    private Node getNodeB() {
        return edge.getNodeB();
    }

    private AdjustmentGuard createGuard() {
        return new AdjustmentGuard(map);
    }

    public void correctEdge() {

        Loggers.info(this, "Correct edge {0}...", edge.getName());

        double scoreA = AdjustmentCostCalculator.calculate(edge, getNodeA(), createGuard());
        double scoreB = AdjustmentCostCalculator.calculate(edge, getNodeB(), createGuard());
        Loggers.info(this, "Adjustment costs for adjacent nodes: {0}/{1}", scoreA, scoreB);

        if (scoreA > maxAdjustmentCosts && scoreB > maxAdjustmentCosts) {
            correctEdgeByIntroducingBendNodes();
        }
        else if (scoreA < scoreB) {
            correctEdgeByMovingNode(edge, getNodeA(), createGuard());
        }
        else {
            correctEdgeByMovingNode(edge, getNodeB(), createGuard());
        }

        Loggers.info(this, "Correction is done.");

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

        Loggers.info(this, "Introduce bends {0} to edge {1}...", vertices, edge.getName());

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

            Loggers.info(this, "Octilinear edge created: {0}", edge);

            // remove old edge
            edge.destroy();

            // numbers set nodes has changed, edge cache must be flagged for rebuild
            map.updateGraph();

        }
        else {
            Loggers.warning(this, "No octilinear edge created: {0}", edge);
        }

    }

    /**
     * Moves given {@link Node} in a certain direction to correct the given {@link Edge}'s octilinearity. Prefers to
     * move in the given (last) move direction if two choices are equal weighted.
     */
    private void moveNode(@NotNull Edge connectionEdge, @NotNull Node moveableNode, @NotNull AdjustmentGuard guard) {

        AdjustmentDirectionEvaluator adjustmentDirectionEvaluator = guard.getNodeAdjustmentDirectionEvaluator();

        MoveVector moveVector;

        if (AdjustmentCostCalculator.isSimpleNode(connectionEdge, moveableNode)) {

            // evaluate direction
            if (!moveableNode.getAdjacentEdges(connectionEdge).isEmpty()) {
                Loggers.info(this, "Evaluate move direction of simple node {0}...", moveableNode.getName());
                moveVector = adjustmentDirectionEvaluator.evaluateDirection(moveableNode, connectionEdge);
            }
            else {
                Loggers.info(this, "Evaluate move direction of single node {0}...", moveableNode.getName());
                moveVector = adjustmentDirectionEvaluator.evaluateSingleNodeDirection(moveableNode, connectionEdge);
            }

        }
        else {
            Loggers.flag(this, "Node {0} is not a simple node. Node will not be moved.", moveableNode.getName());
            moveVector = new MoveVector(0, 0);
        }

        Loggers.info(this, "Evaluated move vector: {0}", moveVector);

        Point movePoint = GeomUtil.createMovePoint(moveableNode.getPoint(), moveVector);

        boolean overlapsOtherEdges = overlapsWithOtherEdges(moveableNode, movePoint, connectionEdge, map);
        boolean overlapsAdjacentNode = overlapsWithAdjacentNodes(moveableNode, connectionEdge, moveVector);
        boolean overlapsOtherNodes = overlapsWithOtherNodes(moveableNode, movePoint);

        if (!overlapsAdjacentNode && !overlapsOtherNodes && !overlapsOtherEdges) {

            Point originalPoint = moveableNode.toPoint(); // clone original position for possible revert operation

            Loggers.flag(this, "Move node {0} using vector {1}.", moveableNode.getName(), moveVector);
            moveableNode.updatePosition(movePoint.getCoordinate());

            Node otherNode = connectionEdge.getOtherNode(moveableNode);

            // check for new conflicts
            if (otherNode.getAdjacentEdges(connectionEdge).anyMatch(edge -> ConflictFinder.hasConflict(moveableNode, edge, map))) {
                Loggers.flag(this, "Revert node movement. Occurs edge/node conflict.");
                moveableNode.updatePosition(originalPoint.getCoordinate());
            }
            else if (moveableNode.getAdjacentEdges().anyMatch(edge -> ConflictFinder.hasConflict(moveableNode, edge.getOtherNode(moveableNode), map))) {
                Loggers.flag(this, "Revert node movement. Occurs adjacent node/node conflict.");
                moveableNode.updatePosition(originalPoint.getCoordinate());
            }
            else {
                Loggers.info(this, "New position for node {0}.", moveableNode.getName());
            }

        }
        else {
            // to be done: log reason
            Loggers.info(this, "Cannot move node {0}!", moveableNode.getName());
        }

    }

    /**
     * @return true if new position is equals to a position of another node
     */
    private boolean overlapsWithOtherNodes(@NotNull Node moveableNode, @NotNull Point movePoint) {
        return map.getNodes().stream()
                .filter(moveableNode::isNotEquals)
                .map(Node::getCoordinate)
                .anyMatch(coordinate -> movePoint.getCoordinate().equals(coordinate));
    }

    /**
     * @return true if moving the node does overlap another node after moving
     */
    private boolean overlapsWithAdjacentNodes(@NotNull Node moveableNode, @NotNull Edge connectionEdge, @NotNull MoveVector moveVector) {
        // analysis required: how to improve this method? currently only working if moved along the adjacent edge
        return moveableNode.getAdjacentEdges(connectionEdge)
                .anyMatch(adjEdge -> adjEdge.getLength() <= moveVector.length());
    }

    /**
     * @return true if new position would intersect with any other edge when moving
     */
    private boolean overlapsWithOtherEdges(@NotNull Node moveableNode, @NotNull Point movePoint, @NotNull Edge connectionEdge, @NotNull Graph graph) {
        return moveableNode.getAdjacentEdges().stream()
                .map(edge -> edge.getOtherNode(moveableNode))
                .map(node -> GeomUtil.createLineString(movePoint, node.getPoint()))
                .anyMatch(lineString -> graph.getEdges().stream()
                        // ignore adjacent edges
                        .filter(edge -> !moveableNode.getAdjacentEdges().contains(edge))
                        // test intersection
                        .filter(edge -> edge.getLineString().relate(lineString, "T********"))
                        .peek(edge -> Loggers.warning(this, "Edge {0} would intersect with {1}!", edge.getName(), connectionEdge.getName()))
                        .findAny()
                        .isPresent());
    }

    /**
     * Corrects the direction set given {@link Edge} recursively by moving the given {@link Node}.
     */
    private void correctEdgeByMovingNode(@NotNull Edge edge, @NotNull Node moveableNode, @NotNull AdjustmentGuard guard) {

        if (guard.hasAlreadyVisited(moveableNode)) {
            Loggers.warning(this, "Node {0} was already visited. Abort edge correction!", moveableNode.getName());
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
            // tail-end recursion
            correctEdgeByMovingNode(nonOctilinearEdge, otherNode, guard);
        }

    }

}
