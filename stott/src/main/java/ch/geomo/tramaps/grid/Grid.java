/*
 * Copyright (c) 2016 by Thomas Zuberbühler
 */

package ch.geomo.tramaps.grid;

import ch.geomo.tramaps.util.CollectionUtil;
import ch.geomo.tramaps.util.GeomUtil;
import ch.geomo.tramaps.util.point.NodePoint;
import ch.geomo.tramaps.util.point.NodePointDistanceComparator;
import ch.geomo.tramaps.util.point.NodePointXYComparator;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.LineString;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Grid {

    private final static Logger logger = Logger.getLogger(Grid.class.getSimpleName());

    private GridGraph graph;

    private long spacing;
    private int maxIteration = 12;
    private int initialSearchDistance = 16;

    private Envelope drawingArea;

    private Map<GridNode, Set<GridNode>> closestNodes;

    public Grid(GridGraph graph) {
        this.graph = graph;
        closestNodes = new HashMap<>();
        getNodes().stream()
                .forEach(node -> {

                    List<GridNode> excluded = getNodes().stream()
                            .filter(n -> !n.equals(node))
                            .collect(Collectors.toList());

                    Set<GridNode> nodes = new TreeSet<>(new NodePointDistanceComparator<>(node));
                    nodes.addAll(excluded);
                    closestNodes.put(node, nodes);

                });
        updateDrawingArea();
    }

    /**
     * Returns the initial search distance.
     */
    @SuppressWarnings("unused")
    public int getInitialSearchDistance() {
        return initialSearchDistance;
    }

    @SuppressWarnings("unused")
    public int getMaxIteration() {
        return maxIteration;
    }

    public boolean isOccupied(double x, double y) {
        return getNodes().stream()
                .filter(n -> n.getX() == x && n.getY() == y)
                .findAny()
                .isPresent();
    }

    @SuppressWarnings("unused")
    public boolean isOccupied(NodePoint point) {
        return isOccupied(point.getX(), point.getY());
    }

    private Stream<NodePoint> getPointStream(int radius, NodePoint originPoint) {

        long x = (long) originPoint.getX();
        long y = (long) originPoint.getY();

        return IntStream.range(-radius, radius + 1)
                .parallel()
                .mapToObj(i -> i)
                .flatMap(i -> IntStream.range(-radius, radius + 1)
                        .parallel()
                        .mapToObj(j -> NodePoint.of(x + i * spacing, y + j * spacing)));

    }

    private Stream<NodePoint> getNonOccupiedPointStream(int radius, NodePoint point) {
        return getPointStream(radius, point)
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
    public Set<NodePoint> getAvailablePoints(int searchDistance, @NotNull GridNode node) {

        List<NodePoint> availablePoints = getNonOccupiedPointStream(searchDistance, node)
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
                // not occupied
                .filter(p -> getNodes().stream()
                        .noneMatch(n -> n.getPoint().equals(p)))
                // Preservation of Edge Ordering
                .filter(p -> !node.hasChangedEdgeOrderingWhenMovingTo(p))
                // limit search to points along ocliniear lines
                .filter(p -> {
                    double angle = GeomUtil.getAngleBetweenAsDegree(node, NodePoint.of(node.getX(), 0), p);
                    return Math.abs(angle)%45 < 1; // dealing with imprecision
                })
                .collect(Collectors.toList());

        if (!availablePoints.isEmpty()) {
            logger.log(Level.INFO, "{0} testing points found for GeoPoint {1}.", new Object[]{availablePoints.size(), node});
        }

        return new HashSet<>(availablePoints);

    }

    private <N> double getMin(Collection<N> collection, Function<N, Double> map) {
        return collection.stream()
                .map(map)
                .min(Double::compare)
                .orElse(0d);
    }

    private <N> double getMax(Collection<N> collection, Function<N, Double> map) {
        return collection.stream()
                .map(map)
                .max(Double::compare)
                .orElse(0d);
    }

    /**
     * Moves coordinates to the first quadrant and reduces high coordinate values in order
     * to handle maps with mixed (negative and positive) coordinate values.
     */
    private void reduceCoordinateValues() {
        logger.log(Level.INFO, "Move map to the first quadrant of the coordinate system...");
        double minX = getMin(getNodes(), GridNode::getX);
        double minY = getMin(getNodes(), GridNode::getY);
        getNodes().parallelStream()
                .forEach(n -> n.moveTo(n.getX() - minX, n.getY() - minY, true));
        logger.log(Level.INFO, "Moving DONE.");
    }

    /**
     * Scale coordinate system.
     */
    private void scaleCoordinateSystem() {
        logger.log(Level.INFO, "Scaling coordinate system in order to fit into grid...");
        double verticalGridDistance = NodePoint.of(0, 0).calculateDistanceTo(spacing, spacing);
        double factor = CollectionUtil.makePermutations(getNodes(), true).parallelStream()
                .map(tuple -> tuple.getFirst().calculateDistanceTo(tuple.getSecond()))
                .filter(distance -> distance < verticalGridDistance)
                .min(Double::compare)
                .map(distance -> 1 / distance * verticalGridDistance)
                .orElse(1d);
        getNodes().parallelStream()
                .forEach(n -> n.moveTo(n.getX() * factor, n.getY() * factor, true));
        logger.log(Level.INFO, "Scaling DONE.");
    }

    /**
     * Get grid point candidates of given node.
     */
    @NotNull
    private List<NodePoint> getGridPointCandidates(@NotNull GridNode node) {
        double x = node.getX() - Math.abs(node.getX() % spacing);
        double y = node.getY() - Math.abs(node.getY() % spacing);
        return Stream.of(x, x + spacing)
                .parallel()
                .flatMap(i -> Stream.of(y, y + spacing)
                        .parallel()
                        .map(j -> NodePoint.of(i, j)))
                .filter(n -> !node.hasChangedEdgeOrderingWhenMovingTo(n))
                .sorted(new NodePointDistanceComparator<>(node))
                .collect(Collectors.toList());
    }

    /**
     * Returns true for solved, otherwise false.
     */
    private boolean handleConflict(GridNode node, GridNode conflictNode, NodePoint conflictPoint, Map<NodePoint, GridNode> nodeMap) {

        if (conflictNode.calculateDistanceTo(conflictPoint) > node.calculateDistanceTo(conflictPoint)) {
            return false;
        }

        nodeMap.put(conflictPoint, conflictNode);

        List<NodePoint> alternatives = getGridPointCandidates(node).stream()
                .filter(point -> !point.equals(conflictPoint))
                .collect(Collectors.toList());

        boolean placed = false;
        for (NodePoint point : alternatives) {
            if (!nodeMap.containsKey(point)) {
                nodeMap.put(point, node);
                placed = true;
                break;
            }
            if (handleConflict(nodeMap.get(point), node, point, nodeMap)) {
                placed = true;
                break;
            }
        }

        if (!placed) {
            throw new IllegalStateException("Cannot handle Snapping Conflict.");
        }
        return true;

    }

    private Set<GridNode> getNodes() {
        return getGraph().getNodes();
    }

    private void updateDrawingArea() {
        double margin = spacing * initialSearchDistance / 2;
        double x1 = getMin(getNodes(), GridNode::getX);
        double x2 = getMax(getNodes(), GridNode::getX);
        double y1 = getMin(getNodes(), GridNode::getY);
        double y2 = getMax(getNodes(), GridNode::getY);
        drawingArea = new Envelope(x1 - margin, x2 + margin, y1 - margin, y2 + margin);
    }

    @SuppressWarnings("unused")
    public Envelope getDrawingArea() {
        return drawingArea;
    }

    public void createClusters() {
        // TODO
    }

    public void simplifyLines() {
        getNodes().forEach(GridNode::simplifyEdges);
        updateDrawingArea();
    }

    public void snapNodes() {

        logger.log(Level.INFO, "Snapping nodes to grid...");

        reduceCoordinateValues();
        scaleCoordinateSystem();

        Map<NodePoint, GridNode> nodeMap = new HashMap<>();
        Set<GridNode> nodes = new TreeSet<>(new NodePointXYComparator());
        nodes.addAll(getNodes());
        nodes.forEach(node -> {
            List<NodePoint> candidates = getGridPointCandidates(node);
            for (NodePoint point : candidates) {
                if (nodeMap.containsKey(point)) {
                    if (handleConflict(nodeMap.get(point), node, point, nodeMap)) {
                        break;
                    }
                    continue;
                }
                nodeMap.put(point, node);
                break;
            }
        });
        nodeMap.entrySet().forEach(entry -> entry.getValue().moveTo(entry.getKey(), true));
        updateDrawingArea();

        logger.log(Level.INFO, "Snapping done.");

    }

    public void setSpacing(long spacing) {
        this.spacing = spacing;
    }

    public void setInitialSearchDistance(int initialSearchDistance) {
        this.initialSearchDistance = initialSearchDistance;
    }

    public void setMaxIteration(int maxIteration) {
        this.maxIteration = maxIteration;
    }

    public GridGraph getGraph() {
        return graph;
    }

    public long getSpacing() {
        return spacing;
    }

}
