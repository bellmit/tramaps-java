/*
 * Copyright (c) 2016 Thomas Zuberbuehler. All rights reserved.
 */

package ch.geomo.tramaps.map.displacement.alg;

import ch.geomo.tramaps.conflict.Conflict;
import ch.geomo.tramaps.graph.Edge;
import ch.geomo.tramaps.graph.Node;
import ch.geomo.tramaps.graph.Route;
import ch.geomo.tramaps.graph.layout.OctilinearEdge;
import ch.geomo.tramaps.graph.layout.OctilinearEdgeBuilder;
import ch.geomo.tramaps.graph.util.OctilinearDirection;
import ch.geomo.tramaps.map.MetroMap;
import ch.geomo.tramaps.map.displacement.MetroMapLineSpaceHandler;
import ch.geomo.tramaps.map.displacement.alg.helper.AdjustmentCostCalculator;
import ch.geomo.tramaps.map.displacement.alg.helper.DisplaceNodeHandler;
import ch.geomo.tramaps.map.displacement.alg.helper.DisplaceNodeResult;
import ch.geomo.tramaps.map.displacement.alg.helper.MoveNodeGuard;
import ch.geomo.tramaps.map.displacement.alg.helper.MoveNodeHandler;
import ch.geomo.tramaps.map.displacement.alg.helper.MoveNodeHandler.MoveNodeDirection;
import ch.geomo.tramaps.map.signature.BendNodeSignature;
import ch.geomo.util.Loggers;
import ch.geomo.util.pair.Pair;
import com.vividsolutions.jts.geom.Point;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static ch.geomo.tramaps.geom.util.GeomUtil.getGeomUtil;

public class DisplaceHandler implements MetroMapLineSpaceHandler {

    private static final int MAX_ITERATIONS = 100;
    private static final double MAX_ADJUSTMENT_COSTS = 5;

    private void correctNonOctilinearEdge(@NotNull Edge edge, @NotNull MetroMap map, @NotNull DisplaceNodeResult displaceNodeResult) {

        AdjustmentCostCalculator costCalculator = new AdjustmentCostCalculator();

        Loggers.info(this, "Correct edge " + edge.getName() + ".");

        // Node A
        MoveNodeGuard guardA = new MoveNodeGuard(map, displaceNodeResult, edge.getNodeA());
        double scoreNodeA = costCalculator.calculateAdjustmentCosts(edge, edge.getNodeA(), guardA);

        // Node B
        MoveNodeGuard guardB = new MoveNodeGuard(map, displaceNodeResult, edge.getNodeB());
        double scoreNodeB = costCalculator.calculateAdjustmentCosts(edge, edge.getNodeB(), guardB);

        Loggers.info(this, "Adjustment Costs for nodes: [" + scoreNodeA + "/" + scoreNodeB + "]");

        if (scoreNodeA > MAX_ADJUSTMENT_COSTS && scoreNodeB > MAX_ADJUSTMENT_COSTS) {
            Loggers.info(this, "Adjustment Costs too high... a bend is required!");
            // correctEdgeByIntroducingBendNodes(edge, map);
        }
        else if (scoreNodeA < scoreNodeB) {
            correctEdgeByMovingNode(edge, edge.getNodeA(), guardA.reuse());
        }
        else {
            correctEdgeByMovingNode(edge, edge.getNodeB(), guardB.reuse());
        }

    }

    private void correctNonOctilinearEdges(@NotNull MetroMap map, @NotNull DisplaceNodeResult displaceNodeResult) {

        Loggers.info(this, "Non-Octilinear edges: " + map.evaluateNonOctilinearEdges().count());
        map.getEdges().stream()
                .filter(edge -> !edge.getDirection(null).isOctilinear())
                .forEach(edge -> correctNonOctilinearEdge(edge, map, displaceNodeResult));

        map.getEdges().stream()
                .filter(edge -> !edge.getDirection(null).isOctilinear())
                .forEach(edge -> Loggers.warning(this, "Uncorrected non-Octilinear edge " + edge.getName() + " with angle=" + edge.getAngle(null) + "!"));

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
     */
    private void moveNode(@NotNull Edge connectionEdge, @NotNull Node moveableNode, @NotNull MoveNodeGuard guard) {

        OctilinearDirection octilinearConnectionEdgeDirection = connectionEdge.getOriginalDirection(moveableNode).toOctilinear();

        MoveNodeHandler moveHandler = guard.getNodeMoveHandler();
        MoveNodeDirection result;

        if (AdjustmentCostCalculator.isSimpleNode(connectionEdge, moveableNode)) {

            Loggers.info(this, "Move node " + moveableNode.getName() + ".");
            Loggers.info(this, "Initial move direction is " + guard.getLastMoveDirection() + ".");

            if (moveableNode.getNodeDegree() != 1) {

                // get first edge
                Edge adjacentEdge = moveableNode.getAdjacentEdgeStream(connectionEdge)
                        .peek(edge -> Loggers.info(this, "Adjacent Edge " + edge.getName()))
                        .findFirst()
                        // should never reach this point
                        .orElseThrow(IllegalStateException::new);

                OctilinearDirection firstAdjacentEdgeDirection = adjacentEdge.getOriginalDirection(moveableNode).toOctilinear();

                boolean hadSameAlignment = firstAdjacentEdgeDirection.getAlignment() == octilinearConnectionEdgeDirection.getAlignment();
                boolean isConflictRelated = guard.getConflict().isConflictRelated(connectionEdge);

                if (!hadSameAlignment && !isConflictRelated) {
                    result = moveHandler.evaluateNode(moveableNode, guard, octilinearConnectionEdgeDirection, firstAdjacentEdgeDirection);
                }
                else if (!hadSameAlignment) {
                    Loggers.info(this, "Node " + moveableNode.getName() + " is conflict related.");
                    result = moveHandler.evaluateConflictRelatedNode(moveableNode, guard, octilinearConnectionEdgeDirection, firstAdjacentEdgeDirection);
                }
                else {
                    Loggers.info(this, "Do not move Node " + moveableNode.getName() + ".");
                    result = new MoveNodeDirection(guard.getLastMoveDirection(), moveableNode, 0);
                }

            }
            else {
                Loggers.info(this, "Handle Single Node " + moveableNode.getName() + "...");
                result = moveHandler.evaluateSingleNode(moveableNode, guard, octilinearConnectionEdgeDirection);
            }
        }
        else {
            Loggers.info(this, "Node " + moveableNode.getName() + " is too complex to move!");
            result = new MoveNodeDirection(guard.getLastMoveDirection(), moveableNode, 0);
        }

        Loggers.info(this, "Evaluated move operation: " + result);

        final double correctDistance = result.getMoveDistance();
        final OctilinearDirection octilinearMoveDirection = result.getMoveDirection();

        // evaluates if moving the node does not overlap another node after moving, otherwise we won't move the node
        boolean overlapsAdjacentNode = moveableNode.getAdjacentEdgeStream(connectionEdge)
                .anyMatch(adjEdge -> adjEdge.getDirection(moveableNode).toOctilinear() == octilinearMoveDirection && adjEdge.getLength() <= correctDistance);

        // test if new position would intersect with any other edge when moving -> if so, we do not move
        Point movePoint = moveableNode.createMovePoint(octilinearMoveDirection, correctDistance);
        boolean overlapsOtherEdges = moveableNode.getAdjacentEdgeStream(null)
                .map(edge -> edge.getOtherNode(moveableNode))
                .map(node -> getGeomUtil().createLineString(movePoint, node.getPoint()))
                .anyMatch(lineString -> guard.getMetroMap().getEdges().stream()
                        .filter(edge -> !moveableNode.getAdjacentEdges().contains(edge))
                        .anyMatch(edge -> {
                            if (edge.getLineString().relate(lineString, "T********")) {
                                Loggers.warning(this, "Edge " + edge.getName() + " would be intersecting with " + connectionEdge.getName() + "!");
                                return true;
                            }
                            return false;
                        }));

        boolean notEqualPosition = getGeomUtil().createLineString(movePoint, connectionEdge.getOtherNode(moveableNode).getPoint()).getLength() > 0;
        if (!notEqualPosition) {
            Loggers.warning(this, "It seems that the new position is equals to the adjacent node position.");
        }

        if (!overlapsAdjacentNode && !overlapsOtherEdges && notEqualPosition) {
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
     * Corrects the direction of given {@link Edge} recursively by moving the given {@link Node}.
     */
    private void correctEdgeByMovingNode(@NotNull Edge edge, @NotNull Node moveableNode, @NotNull MoveNodeGuard guard) {

        if (guard.isNotMoveable(moveableNode)) {
            Loggers.warning(this, "Node " + moveableNode.getName() + " cannot be moved!");
            return;
        }

        if (guard.hasAlreadyVisited(moveableNode)) {
            Loggers.warning(this, "Correct edge aborted due to a second visit of node " + moveableNode.getName() + "!");
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

    private void makeSpace(@NotNull MetroMap map, int lastIteration, @Nullable Conflict lastConflict) {

        int currentIteration = lastIteration + 1;

        List<Conflict> conflicts = map.evaluateConflicts(true);

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
            DisplaceNodeHandler displaceNodeHandler = new DisplaceNodeHandler(map, conflict, conflicts);
            DisplaceNodeResult displaceNodeResult = displaceNodeHandler.displace();
            correctNonOctilinearEdges(map, displaceNodeResult);

            // repeat as long as max iteration is not reached
            if (currentIteration < MAX_ITERATIONS) {
                makeSpace(map, currentIteration, conflict);
            }
            else {
                Loggers.separator(this);
                Loggers.warning(this, "Max number of iteration reached. Stop algorithm.");
                Loggers.separator(this);
            }

        }
        else {
            Loggers.info(this, "No (more) conflicts found.");
        }

    }

    @Override
    public void makeSpace(@NotNull MetroMap map) {
        Loggers.separator(this);
        Loggers.info(this, "Start DisplaceHandler algorithm");
        makeSpace(map, 0, null);
//        map.getEdges().stream()
//                .filter(Edge::isNotOctilinear)
//                .forEach(edge -> correctEdgeByIntroducingBendNodes(edge, map));
        map.evaluateConflicts(true)
                .forEach(conflict -> Loggers.warning(this, "Conflict " + conflict + " not solved!"));
    }

}
