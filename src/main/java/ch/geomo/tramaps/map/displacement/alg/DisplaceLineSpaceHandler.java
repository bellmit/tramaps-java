/*
 * Copyright (c) 2016 Thomas Zuberbuehler. All rights reserved.
 */

package ch.geomo.tramaps.map.displacement.alg;

import ch.geomo.tramaps.conflict.BufferConflict;
import ch.geomo.tramaps.conflict.Conflict;
import ch.geomo.util.collection.list.EnhancedList;
import ch.geomo.util.geom.GeomUtil;
import ch.geomo.tramaps.graph.Edge;
import ch.geomo.tramaps.graph.Node;
import ch.geomo.tramaps.graph.util.OctilinearDirection;
import ch.geomo.tramaps.map.MetroMap;
import ch.geomo.tramaps.map.displacement.LineSpaceHandler;
import ch.geomo.tramaps.map.displacement.alg.adjustment.AdjustmentCostCalculator;
import ch.geomo.tramaps.map.displacement.alg.adjustment.AdjustmentGuard;
import ch.geomo.tramaps.map.displacement.alg.adjustment.AdjustmentDirection;
import ch.geomo.tramaps.map.displacement.alg.adjustment.AdjustmentDirectionEvaluator;
import ch.geomo.util.Contracts;
import ch.geomo.util.logging.Loggers;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Point;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.stream.Collectors;

public class DisplaceLineSpaceHandler implements LineSpaceHandler {

    private static final int MAX_ITERATIONS = 100;
    private static final double MAX_ADJUSTMENT_COSTS = 100;

    private final MetroMap map;

    public DisplaceLineSpaceHandler(@NotNull MetroMap map) {
        this.map = map;
    }

    private void correctNonOctilinearEdge(@NotNull Edge edge, @NotNull NodeDisplaceResult displaceResult) {

        EdgeAdjuster adjuster = new EdgeAdjuster(map, edge, displaceResult);
        adjuster.correctEdge();

//        Loggers.info(this, "Correct edge " + edge.getName() + ".");
//
//        // Node A
//        AdjustmentGuard guardA = new AdjustmentGuard(map, displaceResult, edge.getNodeA());
//        double scoreNodeA = AdjustmentCostCalculator.calculateAdjustmentCosts(edge, edge.getNodeA(), guardA);
//
//        // Node B
//        AdjustmentGuard guardB = new AdjustmentGuard(map, displaceResult, edge.getNodeB());
//        double scoreNodeB = AdjustmentCostCalculator.calculateAdjustmentCosts(edge, edge.getNodeB(), guardB);
//
//        Loggers.info(this, "Adjustment Costs for nodes: [" + scoreNodeA + "/" + scoreNodeB + "]");
//
//        if (scoreNodeA > MAX_ADJUSTMENT_COSTS && scoreNodeB > MAX_ADJUSTMENT_COSTS) {
//            Loggers.info(this, "Adjustment Costs too high... a bend is required!");
//            // correctEdgeByIntroducingBendNodes(edge, map);
//        }
//        else if (scoreNodeA < scoreNodeB) {
//            correctEdgeByMovingNode(edge, edge.getNodeA(), guardA.reuse());
//        }
//        else {
//            correctEdgeByMovingNode(edge, edge.getNodeB(), guardB.reuse());
//        }

    }

    private void correctNonOctilinearEdges(@NotNull NodeDisplaceResult displaceResult) {
        Loggers.info(this, "Non-Octilinear edges: " + map.countNonOctilinearEdges());
        map.getEdges().stream()
                .filter(Edge::isNotOctilinear)
                .forEach(edge -> correctNonOctilinearEdge(edge, displaceResult));
    }

    /**
     * Moves given {@link Node} in a certain direction to correct the given {@link Edge}'s
     * octilinearity. Prefers to move in the given (last) move direction if two choices
     * are equal weighted.
     */
    private void moveNode(@NotNull Edge connectionEdge, @NotNull Node moveableNode, @NotNull AdjustmentGuard guard) {

        AdjustmentDirectionEvaluator adjustmentDirectionEvaluator = guard.getNodeAdjustmentDirectionEvaluator();
        AdjustmentDirection result;

        if (AdjustmentCostCalculator.isSimpleNode(connectionEdge, moveableNode)) {

            Loggers.info(this, "Move node " + moveableNode.getName() + ".");
            Loggers.info(this, "Initial move direction is " + guard.getLastMoveDirection() + ".");

            // get first edge
            Edge adjacentEdge = moveableNode.getAdjacentEdgeStream(connectionEdge)
                    .peek(edge -> Loggers.info(this, "Adjacent Edge " + edge.getName()))
                    .findFirst()
                    .orElse(null);

            result = adjustmentDirectionEvaluator.evaluateDirection(moveableNode, connectionEdge, adjacentEdge, guard);

        }
        else {
            Loggers.info(this, "Node " + moveableNode.getName() + " is too complex to move!");
            result = new AdjustmentDirection(guard.getLastMoveDirection(), moveableNode, 0);
        }

        Loggers.info(this, "Evaluated move operation: " + result);

        final double correctDistance = result.getMoveDistance();
        final OctilinearDirection octilinearMoveDirection = result.getMoveDirection();

        Point movePoint = moveableNode.createMovePoint(octilinearMoveDirection, correctDistance);

        // evaluates if moving the node does not overlap another node after moving, otherwise we won't move the node
        boolean overlapsAdjacentNode = moveableNode.getAdjacentEdgeStream(connectionEdge)
                .anyMatch(adjEdge -> {
                    if (adjEdge.getDirection(moveableNode).toOctilinear() != octilinearMoveDirection) {
                        return false;
                    }
                    Node otherNode = adjEdge.getOtherNode(moveableNode);
                    if (adjEdge.getLength() > correctDistance) {
                        return false;
                    }
                    Coordinate coordinate = movePoint.getCoordinate();
                    switch (result.getMoveDirection()) {
                        case EAST:
                            return !otherNode.isEastOf(coordinate);
                        case NORTH:
                            return !otherNode.isNorthOf(coordinate);
                        case SOUTH:
                            return !otherNode.isSouthOf(coordinate);
                        case WEST:
                            return !otherNode.isWestOf(coordinate);
                        case NORTH_EAST:
                            return !otherNode.isNorthEastOf(coordinate);
                        case NORTH_WEST:
                            return !otherNode.isNorthWestOf(coordinate);
                        case SOUTH_EAST:
                            return !otherNode.isSouthEastOf(coordinate);
                        case SOUTH_WEST:
                            return !otherNode.isSouthWestOf(coordinate);
                        default: {
                            Contracts.fail();
                            return true;
                        }
                    }
                });

        // test if new position is equals to a position set another node
        boolean overlapsOtherNodes = map.getNodes().stream()
                .filter(moveableNode::isNotEquals)
                .map(Node::getCoordinate)
                .anyMatch(coordinate -> movePoint.getCoordinate().equals(coordinate));

        // test if new position would intersect with any other edge when moving -> if so, we do not move
        boolean overlapsOtherEdges = moveableNode.getAdjacentEdgeStream(null)
                .map(edge -> edge.getOtherNode(moveableNode))
                .map(node -> GeomUtil.createLineString(movePoint, node.getPoint()))
                .anyMatch(lineString -> guard.getMetroMap().getEdges().stream()
                        .filter(edge -> !moveableNode.getAdjacentEdges().contains(edge))
                        .anyMatch(edge -> {
                            if (edge.getLineString().relate(lineString, "T********")) {
                                Loggers.warning(this, "Edge " + edge.getName() + " would be intersecting with " + connectionEdge.getName() + "!");
                                return true;
                            }
                            return false;
                        }));

        boolean notEqualPosition = GeomUtil.createLineString(movePoint, connectionEdge.getOtherNode(moveableNode).getPoint()).getLength() > 0;
        if (!notEqualPosition) {
            Loggers.warning(this, "It seems that the new position is equals to the adjacent node position.");
        }

        if (!overlapsAdjacentNode && !overlapsOtherNodes && !overlapsOtherEdges && notEqualPosition) {
            Loggers.flag(this, "Move node " + moveableNode.getName() + " to " + octilinearMoveDirection + " (distance=" + correctDistance + ").");
            moveableNode.updatePosition(movePoint);
            Loggers.info(this, "New position for Node " + moveableNode.getName() + ".");
        }
        else {
            Loggers.info(this, "Node " + moveableNode.getName() + " is not moveable!");
        }

        // update guard
        guard.setLastMoveDirection(octilinearMoveDirection);
        guard.setLastMoveDistance(correctDistance);

    }

    /**
     * Corrects the direction set given {@link Edge} recursively by moving the given {@link Node}.
     */
    private void correctEdgeByMovingNode(@NotNull Edge edge, @NotNull Node moveableNode, @NotNull AdjustmentGuard guard) {

        if (guard.isNotMoveable(moveableNode)) {
            Loggers.warning(this, "Node " + moveableNode.getName() + " cannot be moved!");
            return;
        }

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

    private void makeSpace(int lastIteration, @Nullable Conflict lastConflict) {

        int currentIteration = lastIteration + 1;

        EnhancedList<Conflict> conflicts = map.evaluateConflicts(true);

        Loggers.separator(this);
        Loggers.info(this, "Iteration: " + currentIteration);

        if (!conflicts.isEmpty()) {

            Loggers.warning(this, "Conflicts found: " + conflicts.size());

            Conflict conflict = conflicts.get(0);
            if (lastConflict != null
                    && conflicts.size() > 1
                    && conflict.getBufferA().getElement().equals(lastConflict.getBufferA().getElement())
                    && conflict.getBufferB().getElement().equals(lastConflict.getBufferB().getElement())) {

                // skip conflict to give another conflict a chance to be solved
                Loggers.warning(this, "Skip conflict for one iteration... Take next one.");
                conflict = conflicts.get(1);

            }

            Loggers.info(this, "Handle conflict: " + conflict);
            NodeDisplacer displacer = new NodeDisplacer(map, conflict, conflicts);
            NodeDisplaceResult displaceResult = displacer.displace();

            // correctNonOctilinearEdges(displaceResult);

            Loggers.warning(this, "Uncorrected non-octilinear edges found: " +  map.getEdges().stream()
                    .filter(edge -> !edge.getDirection(null).isOctilinear())
                    .count());

            // repeat as long as max iteration is not reached
            if (currentIteration < MAX_ITERATIONS) {
                makeSpace(currentIteration, conflict);
            }
            else {
                Loggers.separator(this);
                Envelope mapBoundingBox = map.getBoundingBox();
                Loggers.info(this, "Size: " + (int)Math.ceil(mapBoundingBox.getWidth()) + "x" + (int)Math.ceil(mapBoundingBox.getHeight()));
                Loggers.warning(this, "Max number set iteration reached. Stop algorithm.");
                Loggers.separator(this);
            }

        }
        else {
            Envelope mapBoundingBox = map.getBoundingBox();
            Loggers.info(this, "Size: " + (int)Math.ceil(mapBoundingBox.getWidth()) + "x" + (int)Math.ceil(mapBoundingBox.getHeight()));
            Loggers.info(this, "No (more) conflicts found.");
        }

    }

    @Override
    public void makeSpace() {
        Loggers.separator(this);
        Loggers.info(this, "Start DisplaceLineSpaceHandler algorithm");
        makeSpace(0, null);
//        map.getEdges().stream()
//                .filter(Edge::isNotOctilinear)
//                .map(edge -> new EdgeAdjuster(map, edge))
//                .forEach(EdgeAdjuster::correctEdge);
        map.evaluateConflicts(true)
                .forEach(conflict -> Loggers.warning(this, "Conflict " + conflict + " not solved!"));
    }

}
