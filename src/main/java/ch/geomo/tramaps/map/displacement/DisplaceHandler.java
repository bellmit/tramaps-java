/*
 * Copyright (c) 2016 Thomas Zuberbuehler. All rights reserved.
 */

package ch.geomo.tramaps.map.displacement;

import ch.geomo.tramaps.conflict.Conflict;
import ch.geomo.tramaps.geo.Axis;
import ch.geomo.tramaps.graph.Edge;
import ch.geomo.tramaps.graph.Graph;
import ch.geomo.tramaps.graph.Node;
import ch.geomo.tramaps.graph.Route;
import ch.geomo.tramaps.graph.layout.OctilinearEdge;
import ch.geomo.tramaps.graph.layout.OctilinearEdgeBuilder;
import ch.geomo.tramaps.graph.util.Direction;
import ch.geomo.tramaps.graph.util.OctilinearDirection;
import ch.geomo.tramaps.map.MetroMap;
import ch.geomo.tramaps.map.signature.BendNodeSignature;
import ch.geomo.util.CollectionUtil;
import ch.geomo.util.Loggers;
import ch.geomo.util.pair.Pair;
import com.vividsolutions.jts.geom.Point;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class DisplaceHandler implements MetroMapLineSpaceHandler {

    private static final int MAX_ITERATIONS = 100;
    private static final double MAX_ADJUSTMENT_COSTS = 25;
    private static final double CORRECT_CIRCLE_PENALTY = 1000;

    private void correctNonOctilinearEdges(MetroMap map) {
        System.out.println("Non-Octilinear Edges: " + map.evaluateNonOctilinearEdges().count());
        map.getEdges().stream()
                .filter(edge -> !Direction.isOctilinear(edge.getAngle()))
                .forEach(edge -> introduceOctilinearBendNodes(edge, map));
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
    private void introduceOctilinearBendNodes(@NotNull Edge edge, @NotNull MetroMap map) {

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
    private OctilinearDirection moveNode(@NotNull Edge edge, @NotNull Node moveableNode, @Nullable OctilinearDirection lastMoveDirection) {

        if (isSimpleNode(edge, moveableNode)) {
            // TODO
        }

        return OctilinearDirection.NORTH;

    }

    /**
     * Corrects the direction of given {@link Edge} recursively by moving the given {@link Node}.
     */
    private void correctEdgeDirection(@NotNull Edge edge, @NotNull Node moveableNode, @Nullable OctilinearDirection lastMoveDirection) {

        OctilinearDirection movedDirection = moveNode(edge, moveableNode, lastMoveDirection);

        moveableNode.getAdjacentEdges().stream()
                .filter(Edge::isNonOctilinear)
                .filter(edge::isNotEquals)
                .forEach(nonOctilinearEdge -> {
                    Node otherNode = edge.getOtherNode(moveableNode);
                    correctEdgeDirection(nonOctilinearEdge, otherNode, movedDirection);
                });

    }

    private double calculateDisplacementCosts(@NotNull Graph graph) {
        // TODO wird das überhaupt benötigt?
        return graph.getNodes().size();
    }

    @NotNull
    private Pair<Graph> getSubGraphsFor(@NotNull Conflict conflict, @NotNull MetroMap map) {
        // TODO
        return Pair.of(null, null);
    }

    private boolean isSimpleNode(@NotNull Edge connectionEdge, @NotNull Node node) {

        List<OctilinearDirection> directions = new ArrayList<>(node.getAdjacentEdgeDirections(connectionEdge));

        if (directions.stream().allMatch(d -> d == connectionEdge.getDirection().toOctilinear().opposite())) {
            return false;
        }

        if (directions.size() == 1) {
            return true;
        }
        else if (directions.size() == 2) {
            return CollectionUtil.makePairs(directions, null).stream()
                    // it's a simple constellation when the angle between every pair of
                    // edges is a multiple of 180 degree
                    .map(pair -> Math.abs(pair.first().getAngle() - pair.second().getAngle()))
                    .allMatch(angle -> angle % 180 == 0);
        }

        return false;

    }

    /**
     * Calculates the costs to adjust given {@link Edge} by moving given {@link Node}. The {@link List} of traversed
     * nodes is needed to avoid correction circles.
     * <p>
     * Note: The {@link List} of traversed nodes is not synchronized.
     */
    private double calculateAdjustmentCosts(@NotNull Edge connectionEdge, @NotNull Node node, @NotNull List<Node> traversedNodes) {

        if (traversedNodes.contains(node)) {
            return CORRECT_CIRCLE_PENALTY;
        }

        traversedNodes.add(node);

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
            costs = costs + calculateAdjustmentCosts(adjacentEdge, otherNode, traversedNodes);
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

            if (conflict.getBestMoveVectorAxis() == Axis.X) {
                map.getNodes().stream()
                        .filter(node -> node.getPoint().getX() > centroid.getX())
                        .forEach(node -> node.updateX(node.getX() + conflict.getBestMoveLengthAlongAnAxis()));
            }
            else {
                map.getNodes().stream()
                        .filter(node -> node.getPoint().getY() > centroid.getY())
                        .forEach(node -> node.updateY(node.getY() + conflict.getBestMoveLengthAlongAnAxis()));
            }

            //correctNonOctilinearEdges(map);

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
        makeSpace(map, routeMargin, edgeMargin, 0);
        correctNonOctilinearEdges(map);
        System.out.println(map);
    }

}
