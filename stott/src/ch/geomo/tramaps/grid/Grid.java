/*
 * Copyright (c) 2016 by Thomas Zuberbühler
 */

package ch.geomo.tramaps.grid;

import ch.geomo.tramaps.geom.ImmutableNodePoint;
import ch.geomo.tramaps.geom.NodePoint;
import ch.geomo.tramaps.geom.NodePointDistanceComparator;
import ch.geomo.tramaps.graph.geo.GeoEdge;
import ch.geomo.tramaps.graph.geo.GeoNode;
import ch.geomo.tramaps.util.tuple.Tuple;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class Grid {

    private final static Logger logger = Logger.getLogger(Grid.class.getSimpleName());

    private GridGraph graph;

    private Map<GeoNode, GridNode> nodeMap = new HashMap<>();
    private Map<GeoEdge, GridEdge> edgeMap = new HashMap<>();

    /**
     * Nodes placed in the grid.
     */
    private Set<GridNode> nodes = new HashSet<>();

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

    public Grid() {
    }

    public boolean isOccupied(double x, double y) {
        return nodes.parallelStream()
                .filter(n -> n.getX() == x && n.getY() == y)
                .findAny()
                .isPresent();
    }

    public boolean isOccupied(NodePoint point) {
        return isOccupied(point.getX(), point.getY());
    }

    private Set<NodePoint> getNonOccupiedPoints(NodePoint point, int radius) {

        Set<NodePoint> nonOccupiedPoints = new TreeSet<>(new NodePointDistanceComparator<>(point));

        for (int i = -radius; i<=radius; i++) {
            for (int j = -radius; j<=radius; j++) {

                long x = (long)point.getX() + i*gridSpace;
                long y = (long)point.getY() + j*gridSpace;

                if (!isOccupied(x, y)) {
                    nonOccupiedPoints.add(new ImmutableNodePoint(x, y));
                }

            }
        }

        return nonOccupiedPoints;

    }

    /**
     * Returns a set of points where the given point could be moved to in order
     * to get a better layout.
     */
    @NotNull
    public Set<NodePoint> getAvailablePoints(@NotNull GridNode point, int radius) {

        Set<NodePoint> nonOccupiedPoints = getNonOccupiedPoints(point, radius);

        Set<NodePoint> nonIntersectingPoints = Tuple.from(nonOccupiedPoints, NodePoint.cast(point.getAdjacentNodes())).parallelStream()
                .filter(tuple -> {
                    LineString line = new GeometryFactory().createLineString(new Coordinate[] {tuple.get(0).getCoordinate(), tuple.get(1).getCoordinate()});
                    return getGraph().getEdges().stream()
                            .filter(edge -> !new ArrayList<>(point.getEdges()).contains(edge))
                            .noneMatch(edge -> edge.getLineString().intersects(line));
                })
                .map(tuple -> tuple.get(0))
                .collect(Collectors.toSet());

        if (!nonIntersectingPoints.isEmpty()) {
            logger.log(Level.INFO, "{0} testing points found.", nonIntersectingPoints.size());
        }

        return new HashSet<>(nonIntersectingPoints);

    }

    @NotNull
    private GridEdge toGridEdge(@NotNull GeoEdge edge) {
        GridEdge gridEdge = new GridEdge(edge.getName(), nodeMap.get(edge.getFirstNode()), nodeMap.get(edge.getSecondNode()));
        edgeMap.put(edge, gridEdge);
        return gridEdge;
    }

    public void setNodes(Set<GeoNode> nodes) {

        logger.log(Level.INFO, "Snapping nodes to grid...");

        this.nodes = nodes.parallelStream()
                .map(n -> new GridNode(n, getClosestGridNode(n)))
                .collect(Collectors.toSet());

        nodeMap = this.nodes.parallelStream()
                .collect(Collectors.toMap(GridNode::getGeoNode, n -> n));

        this.nodes.stream()
                .forEach(node -> node.getGeoNode().getEdges()
                        .forEach(edge -> node.addEdge(toGridEdge(edge))));

        graph = new GridGraph(this.nodes, edgeMap.values(), gridSpace, initialMoveRadius);

        logger.log(Level.INFO, "Snapping done.");

    }

    private NodePoint getClosestGridNode(GeoNode node) {

        NodePoint p = NodePoint.of(node.getX() / gridSpace, node.getY() / gridSpace);

        // note: currently only working with positive coordinates
        long x = (long)p.getX();
        long y = (long)p.getY();

        List<ImmutableNodePoint> nodes = new ArrayList<>();
        nodes.add(new ImmutableNodePoint(x, y));
        nodes.add(new ImmutableNodePoint(x + 1, y));
        nodes.add(new ImmutableNodePoint(x, y + 1));
        nodes.add(new ImmutableNodePoint(x + 1, y + 1));

        nodes.sort((n1, n2) -> {
            if (isOccupied(n1)) {
                return 1;
            }
            if (isOccupied(n2)) {
                return -1;
            }
            double diff = p.calculateDistanceTo(n1.getX(), n1.getY()) - p.calculateDistanceTo(n1.getX(), n2.getY());
            return (diff > 0) ? 1 : (diff < 0) ? -1 : 0;
        });

        return nodes.get(0);

    }

    public void setGridSpace(long gridSpace) {
        this.gridSpace = gridSpace;
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
