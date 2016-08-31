package ch.geomo.tramaps.map.displacement;

import ch.geomo.tramaps.conflict.Conflict;
import ch.geomo.tramaps.geo.Axis;
import ch.geomo.tramaps.graph.Edge;
import ch.geomo.tramaps.graph.Graph;
import ch.geomo.tramaps.graph.Node;
import ch.geomo.tramaps.graph.util.OctilinearDirection;
import ch.geomo.tramaps.map.MetroMap;
import ch.geomo.util.pair.Pair;
import com.vividsolutions.jts.geom.Point;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.stream.Collectors;

public class DisplaceHandler implements MakeSpaceHandler {

    private static final double MAX_ADJUSTMENT_COSTS = 25;
    private static final double CORRECT_CIRCLE_PENALTY = 1000;

    private void correctMap(MetroMap map) {
        System.out.println("Non-Octilinear Edges:" + map.evaluateNonOctilinearEdges().count());
    }

    @NotNull
    private OctilinearDirection moveNode(@NotNull Edge edge, @NotNull Node moveableNode, @Nullable OctilinearDirection lastMoveDirection) {
        // TODO
        return OctilinearDirection.NORTH;
    }

    /**
     * Listing 6
     */
    private void correctEdge(@NotNull Edge edge, @NotNull Node moveableNode, @Nullable OctilinearDirection lastMoveDirection) {

        OctilinearDirection movedDirection = moveNode(edge, moveableNode, lastMoveDirection);

        Set<Edge> nonOctilinearAdjacentEdges = moveableNode.getAdjacentEdges().stream()
                .filter(Edge::isNonOctilinear)
                .filter(edge::equals)
                .collect(Collectors.toSet());

        for (Edge nonOctilinearEdge : nonOctilinearAdjacentEdges) {
            Node otherNode = edge.getOtherNode(moveableNode);
            correctEdge(nonOctilinearEdge, otherNode, movedDirection);
        }

    }

    private double calculateDisplacementCosts(@NotNull Graph graph) {
        return graph.getNodes().size();
    }

    @NotNull
    private Pair<Graph> getSubGraphsFor(@NotNull Conflict conflict, @NotNull MetroMap map) {
        // TODO
        return Pair.of(null, null);
    }

    /**
     * Listing 5
     * Vector -> synchronized !
     */
    private double calculateAdjustmentCosts(@NotNull Edge connectionEdge, @NotNull Node node, @NotNull Vector<Node> traversedNodes) {

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

        boolean simpleAdjustmentPossible = adjacentEdges.stream()
                // TODO evaluate simple adjustment
                .noneMatch(edge -> false);

        if (simpleAdjustmentPossible) {
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
                        .forEach(node -> node.setX(node.getX() + conflict.getBestMoveLengthAlongAnAxis()));
            }
            else {
                map.getNodes().stream()
                        .filter(node -> node.getPoint().getY() > centroid.getY())
                        .forEach(node -> node.setY(node.getY() + conflict.getBestMoveLengthAlongAnAxis()));
            }

            correctMap(map);

            if (count < 100) {
                makeSpace(map, routeMargin, edgeMargin, count);
            }

        }

    }

    @Override
    public void makeSpace(@NotNull MetroMap map, double routeMargin, double edgeMargin) {
        this.makeSpace(map, routeMargin, edgeMargin, 0);
        System.out.println(map);
    }

}
