package ch.geomo.tramaps.map.displacement;

import ch.geomo.tramaps.conflict.Conflict;
import ch.geomo.tramaps.geo.Axis;
import ch.geomo.tramaps.graph.Edge;
import ch.geomo.tramaps.graph.Graph;
import ch.geomo.tramaps.graph.Node;
import ch.geomo.tramaps.graph.Route;
import ch.geomo.tramaps.graph.util.OctilinearDirection;
import ch.geomo.tramaps.map.MetroMap;
import ch.geomo.tramaps.map.signature.BendNodeSignature;
import ch.geomo.util.CollectionUtil;
import ch.geomo.util.pair.Pair;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Point;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.Vector;
import java.util.stream.Collectors;

public class DisplaceHandler implements MakeSpaceHandler {

    private static final int MAX_ITERATIONS = 100;
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

    private void mergeNodes(@NotNull Node fixedNode, @NotNull Node obsoleteNode, @NotNull MetroMap map) {

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

    }

    /**
     * Evaluates an octilinear bend coordinate for given {@link Edge}.
     */
    @NotNull
    private Coordinate evaluateOctilinearBendCoordinate(@NotNull Edge edge) {

        Node nodeA = edge.getNodeA();
        Node nodeB = edge.getNodeB();

        double relX = nodeB.getX() / nodeA.getX();
        double relY = nodeB.getY() / nodeB.getY();

        if (relX < relY) {
            if (nodeA.getDegree() > nodeB.getDegree()) {
                double y = nodeA.getX() / nodeB.getX() * nodeB.getY();
                return new Coordinate(nodeB.getX(), y);
            }
            double y = nodeB.getX() / nodeA.getX() * nodeA.getY();
            return new Coordinate(nodeA.getX(), y);
        }

        // relX > relY
        if (nodeA.getDegree() > nodeB.getDegree()) {
            double x = nodeA.getY() / nodeB.getY() * nodeB.getX();
            return new Coordinate(x, nodeB.getY());
        }
        double x = nodeB.getY() / nodeA.getY() * nodeA.getX();
        return new Coordinate(x, nodeA.getY());

    }

    /**
     * Introduces a bend node for given {@link Edge}. The given {@link Edge} instance
     * will be destroyed.
     *
     * @return the bend node
     * @see Node#getAdjacentEdges() to receive the newly created edges
     */
    @NotNull
    private Node introduceBendNode(@NotNull Edge edge, @NotNull MetroMap map) {

        // create new bend node
        Coordinate coordinate = evaluateOctilinearBendCoordinate(edge);
        Node node = new Node(coordinate, BendNodeSignature::new);
        map.addNodes(node);

        // create two new edges
        edge.getNodeA().createAdjacentEdgeTo(node, edge.getRoutes());
        edge.getNodeB().createAdjacentEdgeTo(node, edge.getRoutes());

        // remove old edge
        edge.delete();

        return node;

    }

    /**
     * Listing 6
     */
    private void correctEdge(@NotNull Edge edge, @NotNull Node moveableNode, @Nullable OctilinearDirection lastMoveDirection) {

        OctilinearDirection movedDirection = moveNode(edge, moveableNode, lastMoveDirection);

        moveableNode.getAdjacentEdges().stream()
                .filter(Edge::isNonOctilinear)
                .filter(edge::isNotEquals)
                .forEach(nonOctilinearEdge -> {
                    Node otherNode = edge.getOtherNode(moveableNode);
                    correctEdge(nonOctilinearEdge, otherNode, movedDirection);
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
                        .forEach(node -> node.updateX(node.getX() + conflict.getBestMoveLengthAlongAnAxis()));
            }
            else {
                map.getNodes().stream()
                        .filter(node -> node.getPoint().getY() > centroid.getY())
                        .forEach(node -> node.updateY(node.getY() + conflict.getBestMoveLengthAlongAnAxis()));
            }

            correctMap(map);

            if (count < MAX_ITERATIONS) {
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
