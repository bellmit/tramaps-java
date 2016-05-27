/*
 * Copyright (c) 2016 by Thomas Zuberbühler
 */

package ch.geomo.tramaps.grid;

import ch.geomo.tramaps.geom.Geom;
import ch.geomo.tramaps.geom.NodePoint;
import ch.geomo.tramaps.geom.NodePointDistanceComparator;
import com.vividsolutions.jts.geom.LineString;
import org.jetbrains.annotations.NotNull;
import org.opengis.geometry.BoundingBox;

import java.util.HashSet;
import java.util.Set;
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
    private long gridSpace;

    /**
     * Alg. variable, Stott's thesis, p. 78, variable l, must be greater or equals 1
     */
    private double multiplicator;

    /**
     * Alg. variable, Stott's thesis, variable r, must be a positive number
     */
    private int initialMoveRadius;

    private BoundingBox drawingArea;

    public Grid(GridGraph graph) {
        this.graph = graph;
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
                        .<NodePoint>mapToObj(j -> NodePoint.of(x + i * gridSpace, y + j * gridSpace)));

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
                    LineString line = Geom.createLineString(p, node);
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

    public void snapNodes() {

        logger.log(Level.INFO, "Snapping nodes to grid...");

        graph.getNodes().parallelStream()
                .forEach(node -> node.moveTo(getClosestGridNode(node)));

        logger.log(Level.INFO, "Snapping done.");

    }

    /**
     * Limitation: currently only working with positive coordinates!
     */
    private NodePoint getClosestGridNode(GridNode node) {

        long x = (long) (node.getX() - (node.getX() % gridSpace));
        long y = (long) (node.getY() - (node.getY() % gridSpace));

        NodePoint p = NodePoint.of(x, y);

        return Stream.of(x, x + gridSpace)
                .parallel()
                .flatMap(i -> Stream.of(y, y + gridSpace)
                        .parallel()
                        .map(j -> NodePoint.of(i, j)))
                .filter(n -> !isOccupied(n))
                .filter(n -> !node.hasChangedEdgeOrderingWhenMovingTo(n))
                .sorted(new NodePointDistanceComparator<>(p))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("check gridSpacing"));

    }

    public void setGridSpacing(long gridSpace) {
        this.gridSpace = gridSpace;
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

}
