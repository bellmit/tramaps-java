/*
 * Copyright (c) 2016 by Thomas Zuberbühler
 */

package ch.geomo.tramaps.grid;

import ch.geomo.tramaps.geom.DoublePoint;
import ch.geomo.tramaps.geom.LongPoint;
import ch.geomo.tramaps.geom.Point;
import ch.geomo.tramaps.graph.geo.GeoEdge;
import ch.geomo.tramaps.graph.geo.GeoNode;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

public class Grid {

    private Map<GeoNode, GridNode> nodeMap = new HashMap<>();
    private Map<GeoEdge, GridEdge> edgeMap = new HashMap<>();

    /**
     * Nodes placed in the grid.
     */
    private Set<GridNode> nodes = new HashSet<>();

    /**
     * Alg. variable, Stott's thesis, p. 74, variable g
     */
    private double gridSpace;

//    /**
//     * Alg. variable, Stott's thesis, p. 78, variable l, must be greater or equals 1
//     */
//    private double multiplicator;
//    /**
//     * Alg. variable, Stott's thesis, p. 91, variable r, must be a multiple of g (gridSpace)
//     */
//    private double moveRadius;

    Grid() {
    }

    public boolean isOccupied(long x, long y) {
        return nodes.stream()
                .filter(n -> n.getX() == x && n.getY() == y)
                .findAny()
                .isPresent();
    }

    public boolean isOccupied(Point<Long> point) {
        return isOccupied(point.getX(), point.getY());
    }

    public Set<Point<Long>> getAvailablePoints(Point<Long> point, int radius) {
        // TODO
        return Collections.emptySet();
    }

    private GridEdge toGridEdge(@NotNull GeoEdge edge) {
        GridEdge gridEdge = new GridEdge(edge.getName(), nodeMap.get(edge.getStart()), nodeMap.get(edge.getEnd()));
        edgeMap.put(edge, gridEdge);
        return gridEdge;
    }

    private GridNode toGridNode(GeoNode node) {
        GridNode gridNode = new GridNode(node, getClosestGridNode(node));
        nodeMap.put(node, gridNode);
        return gridNode;
    }

    public void setNodes(Set<GeoNode> nodes) {

        this.nodes = nodes.stream()
                .map(this::toGridNode)
                .collect(Collectors.toSet());

        for (GridNode node : this.nodes) {
            node.getGeoNode().getEdges().forEach(e -> node.addEdge(toGridEdge(e)));
        }

    }

    private Point<Long> getClosestGridNode(GeoNode node) {

        DoublePoint p = new DoublePoint(node.getX() / gridSpace, node.getY() / gridSpace);

        // note: currently only working with positive coordinates
        long x = p.getX().longValue();
        long y = p.getY().longValue();

        List<LongPoint> nodes = new ArrayList<>();
        nodes.add(new LongPoint(x, y));
        nodes.add(new LongPoint(x + 1, y));
        nodes.add(new LongPoint(x, y + 1));
        nodes.add(new LongPoint(x + 1, y + 1));

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

    void setGridSpace(Double gridSpace) {
        this.gridSpace = gridSpace;
    }

    public GridGraph getGraph() {
        return new GridGraph(nodes, edgeMap.values());
    }

}
