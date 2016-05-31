/*
 * Copyright (c) 2016 by Thomas Zuberbühler
 */

package ch.geomo.tramaps.grid;

import ch.geomo.tramaps.geom.*;
import ch.geomo.tramaps.util.tuple.Tuple;
import com.vividsolutions.jts.geom.LineString;
import org.jetbrains.annotations.NotNull;
import org.opengis.geometry.BoundingBox;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Grid {

    private final static Logger logger = Logger.getLogger(Grid.class.getSimpleName());

    private GridGraph graph;

    /**
     * Alg. variable, Stott's thesis, p. 74, variable g
     */
    private double gridSpacing;

    /**
     * Alg. variable, Stott's thesis, p. 78, variable l, must be greater or equals 1
     */
    private double multiplicator;

    /**
     * Alg. variable, Stott's thesis, variable r, must be a positive number
     */
    private int initialMoveRadius;

    private Map<GridNode, Set<GridNode>> closestNodes;

    private BoundingBox drawingArea;

    public Grid(GridGraph graph) {
        this.graph = graph;
        this.closestNodes = new HashMap<>();
        this.graph.getNodes().stream()
                .forEach(node -> {

                    List<GridNode> excluded = this.graph.getNodes().stream()
                            .filter(n -> !n.equals(node))
                            .collect(Collectors.toList());

                    Set<GridNode> nodes = new TreeSet<>(new NodePointDistanceComparator<>(node));
                    nodes.addAll(excluded);
                    this.closestNodes.put(node, nodes);

                });
    }

    public boolean isOccupied(double x, double y) {
        return graph.getNodes().stream()
                .filter(n -> n.getX() == x && n.getY() == y)
                .findAny()
                .isPresent();
    }

    public boolean isOccupied(NodePoint point) {
        return isOccupied(point.getX(), point.getY());
    }

    private Stream<NodePoint> getPointStream(int radius, NodePoint originPoint) {

        long x = (long) originPoint.getX();
        long y = (long) originPoint.getY();

        return IntStream.range(-radius, radius + 1)
                .parallel()
                .<Integer>mapToObj(i -> i)
                .flatMap(i -> IntStream.range(-radius, radius + 1)
                        .parallel()
                        .<NodePoint>mapToObj(j -> NodePoint.of(x + i * gridSpacing, y + j * gridSpacing)));

    }

    private Stream<NodePoint> getNonOccupiedPointStream(int radius, NodePoint point) {
        return getPointStream(radius, point)
                .parallel()
                .filter(p -> !isOccupied(p.getX(), p.getY()))
                .sorted(new NodePointDistanceComparator<>(point));
    }

    private Set<NodePoint> getNonOccupiedPoints(int radius, NodePoint point) {
        return getNonOccupiedPointStream(radius, point).collect(Collectors.toSet());
    }

    /**
     * Returns a set of points where the given point could be moved to in order
     * to get a better layout.
     */
    @NotNull
    public Set<NodePoint> getAvailablePoints(int radius, @NotNull GridNode node) {

        Set<NodePoint> availablePoints = getNonOccupiedPointStream(radius, node)
                // Restrict Movement to Boundary of Drawing Area
                .filter(p -> drawingArea.contains(p.getX(), p.getY()))
                // Enforcement of Geographic Relationships
                // TODO
                // Handling Node and Edge Occlusions
                .filter(p -> {
                    LineString line = GeomUtil.createLineString(p, node);
                    return getGraph().getEdges().stream()
                            .filter(e -> e.getLineString().intersects(line))
                            .noneMatch(e -> e.getLineString().contains(p.getPoint()));
                })
                .filter(p -> getGraph().getNodes().stream()
                        .noneMatch(n -> n.getPoint().equals(p)))
                // Preservation of Edge Ordering
                .filter(p -> !node.hasChangedEdgeOrderingWhenMovingTo(p))
                .collect(Collectors.toSet());

//        if (!availablePoints.isEmpty()) {
//            logger.log(Level.INFO, "{0} testing points found for GeoPoint {1}.", new Object[]{availablePoints.size(), node});
//        }

        return new HashSet<>(availablePoints);

    }

    private GridNode getClosestNodeTo(GridNode node, int index) {
        return new ArrayList<>(closestNodes.get(node)).get(index);
    }

    public void snapNodes(/*NodeCriteriaHandler criteriaHandler*/) {

        logger.log(Level.INFO, "Snapping nodes to grid...");

//        double factor = drawingArea.getHeight()/drawingArea.getWidth();

//        double averageDistanceX = graph.getNodes().stream()
//                .map(node -> Tuple.of(node, getClosestNodeTo(node, 0)))
//                .mapToDouble(tuple -> Math.abs(tuple.getSecond().getX() - tuple.getFirst().getX()))
//                .average()
//                .orElseThrow(() -> new IllegalStateException("Cannot calculate average distance between x values."));
//
//        double averageDistanceY = graph.getNodes().stream()
//                .map(node -> Tuple.of(node, getClosestNodeTo(node, 0)))
//                .mapToDouble(tuple -> Math.abs(tuple.getSecond().getY() - tuple.getFirst().getY()))
//                .average()
//                .orElseThrow(() -> new IllegalStateException("Cannot calculate average distance between y values."));

//        double averageDistance = (averageDistanceX + averageDistanceY)/2;

        List<GridNode> nodes = new ArrayList<>(graph.getNodes());

//        for (GridNode node : nodes) {
//            List<GridNode> closestX = this.getGraph().getNodes().stream()
//                    .filter(n -> !n.equals(node))
//                    .filter(n -> n.getX() > node.getX())
//                    .sorted((n1, n2) -> (n1.getX()-n2.getX() > 0) ? 1 : (n1.getX()-n2.getX() < 0) ? -1 : 0)
//                    .collect(Collectors.toList());
//            if (!closestX.isEmpty()) {
//                GridNode closest = closestX.get(0);
//                double distanceX = Math.abs(node.getX() - closest.getX());
//                double distanceY = Math.abs(node.getY() - closest.getY());
//                if (distanceX < averageDistance && distanceY > averageDistance) {
//                    closest.setX(node.getX());
//                    closestX.stream()
//                            .filter(n -> !n.equals(closest))
//                            .forEach(n -> n.setX(n.getX() - distanceX + averageDistance*factor));
//                }
//                else {
//                    closestX.forEach(n -> n.setX(n.getX() - distanceX + averageDistance*factor));
//                }
//            }
//        }
//
//        for (GridNode node : nodes) {
//            List<GridNode> closestY = this.getGraph().getNodes().stream()
//                    .filter(n -> !n.equals(node))
//                    .filter(n -> n.getY() > node.getY())
//                    .sorted((n1, n2) -> (n1.getY()-n2.getY() > 0) ? 1 : (n1.getY()-n2.getY() < 0) ? -1 : 0)
//                    .collect(Collectors.toList());
//            if (!closestY.isEmpty()) {
//                GridNode closest = closestY.get(0);
//                double distanceY = Math.abs(node.getY() - closest.getY());
//                double distanceX = Math.abs(node.getX() - closest.getX());
//                if (distanceY < averageDistance && distanceX > averageDistance) {
//                    closest.setY(node.getY());
//                    closestY.stream()
//                            .filter(n -> !n.equals(closest))
//                            .forEach(n -> n.setY(n.getY() - distanceY + averageDistance));
//                }
//                else {
//                    closestY.forEach(n -> n.setY(n.getY() - distanceY + averageDistance));
//                }
//            }
//        }

        graph.getNodes().stream()
                .sorted(new NodePointXYComparator())
                .forEach(node -> node.moveTo(getClosestGridNode(node)));

        nodes.forEach(GridNode::simplifyEdges);

        logger.log(Level.INFO, "Snapping done.");

    }

    /**
     * Limitation: currently only working with positive coordinates!
     */
    private NodePoint getClosestGridNode(GridNode node) {

        double x = node.getX() - Math.abs(node.getX() % gridSpacing);
        double y = node.getY() - Math.abs(node.getY() % gridSpacing);

        NodePoint p = NodePoint.of(x, y);

        return Stream.of(x, x + gridSpacing)
                .parallel()
                .flatMap(i -> Stream.of(y, y + gridSpacing)
                        .parallel()
                        .map(j -> NodePoint.of(i, j)))
                .filter(n -> !isOccupied(n))
                .filter(n -> !node.hasChangedEdgeOrderingWhenMovingTo(n))
                .sorted(new NodePointDistanceComparator<>(p))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("check gridSpacing"));

    }

    public void setGridSpacing(double gridSpace) {
        this.gridSpacing = gridSpace;
    }

    public void setDrawingArea(BoundingBox drawingArea) {
        this.drawingArea = drawingArea;
    }

    public void setMultiplicator(double multiplicator) {
        this.multiplicator = multiplicator;
    }

    public void setInitialMoveRadius(int initialMoveRadius) {
        this.initialMoveRadius = initialMoveRadius;
    }

    public GridGraph getGraph() {
        return graph;
    }

    public double getGridSpacing() {
        return gridSpacing;
    }
}
