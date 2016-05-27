/*
 * Copyright (c) 2016 by Thomas Zuberbühler
 */

package ch.geomo.tramaps.grid;

import ch.geomo.tramaps.geom.Geom;
import ch.geomo.tramaps.geom.NodePoint;
import ch.geomo.tramaps.graph.GridEdgeOrderComparator;
import ch.geomo.tramaps.graph.NodeLabel;
import ch.geomo.tramaps.graph.Quadrant;
import ch.geomo.tramaps.util.CollectionUtil;
import ch.geomo.tramaps.util.tuple.Tuple;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.Point;
import org.apache.commons.lang3.tuple.Pair;
import org.geotools.geometry.jts.JTSFactoryFinder;
import org.geotools.graph.structure.basic.BasicNode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class GridNode extends BasicNode implements NodePoint {

    private static final Logger logger = Logger.getLogger(GridNode.class.getSimpleName());

    private String name;
    private NodeLabel label;
    private long version;

    private Set<Tuple<GridEdge>> edgePairs;
    private Map<Tuple<GridEdge>, Double> edgeAngles;

    public GridNode() {
    }

    public long getVersion() {
        return version;
    }

    public Point getPoint() {
        return (Point) getObject();
    }

    public double getX() {
        return getPoint().getX();
    }

    public double getY() {
        return getPoint().getY();
    }

    public void moveTo(Coordinate coordinate) {
        setObject(JTSFactoryFinder.getGeometryFactory().createPoint(coordinate));
        getEdges().forEach(GridEdge::updateLineString);
        version++;
    }

    public void moveTo(double x, double y) {
        moveTo(new Coordinate(x, y));
    }

    public void moveTo(NodePoint point) {
        moveTo(point.getCoordinate());
    }

    /**
     * Checks the edge ordering of current node did not change. (Node Movement Rule: Preservation of Edge Ordering)
     */
    public boolean hasChangedEdgeOrderingWhenMovingTo(@NotNull NodePoint point) {

        for (GridEdge edge : getEdges()) {

            GridNode node = edge.getOtherNode(this);

            List<LineString> lines = node.getEdges().stream()
                    .map(GridEdge::getLineString)
                    .collect(Collectors.toList());

            LineString test = Geom.createLineString(point.getCoordinate(), node.getCoordinate());
            lines.add(test);

            int edgeIndex = lines.indexOf(edge.getLineString());
            int testIndex = lines.indexOf(test);

            if (edgeIndex - testIndex > 1) {
                return true;
            }

        }

        return false;

    }

    @Override
    public Coordinate getCoordinate() {
        return getPoint().getCoordinate();
    }

    public void setName(@Nullable String name) {
        this.name = name;
    }

    @Nullable
    public String getName() {
        return name;
    }

    public void setLabel(@Nullable NodeLabel label) {
        this.label = label;
    }

    @Nullable
    public NodeLabel getLabel() {
        return label;
    }

    public void add(@NotNull GridEdge e) {
        super.add(e);
        clearCaches();
    }

    public void remove(@NotNull GridEdge e) {
        super.remove(e);
        clearCaches();
    }

    @NotNull
    @Override
    @SuppressWarnings("unchecked")
    public List<GridEdge> getEdges() {
        return super.getEdges();
    }

    @NotNull
    public Optional<GridEdge> getEdge(GridNode other) {
        return Optional.ofNullable(super.getEdge(other)).map(edge -> (GridEdge) edge);
    }

    public boolean hasDegree(int degree) {
        return getDegree() == degree;
    }

    @NotNull
    @SuppressWarnings("unchecked")
    public List<GridEdge> getEdges(GridNode other) {
        return super.getEdges(other);
    }

    @NotNull
    @Override
    @SuppressWarnings("unchecked")
    public Iterator<GridNode> getRelated() {
        return super.getRelated();
    }

    @NotNull
    public Stream<GridNode> getRelatedStream() {
        Iterable<GridNode> iterable = this::getRelated;
        return StreamSupport.stream(iterable.spliterator(), false);
    }

    /**
     * Clears all caches.
     */
    public void clearCaches() {
        this.edgePairs = null;
        this.edgeAngles = null;
    }

    @NotNull
    public Set<Tuple<GridEdge>> getAdjacentEdgePairs() {
        if (edgePairs == null) {
            // edges are already sorted
            edgePairs = GridNode.createAdjacentEdgePairs(getEdges(), this, true);
        }
        return edgePairs;
    }

    @NotNull
    public Map<Tuple<GridEdge>, Double> getEdgeAngles() {
        if (edgeAngles == null) {
            edgeAngles = getAdjacentEdgePairs().stream()
                    .map(pair -> {
                        double angle = calculateAngleBetween(pair.getFirst().getOtherNode(this), pair.getSecond().getOtherNode(this));
                        return Pair.of(pair, angle);
                    })
                    .collect(Collectors.toMap(Pair::getLeft, Pair::getRight));
        }
        return edgeAngles;
    }

    @NotNull
    public static Set<Tuple<GridEdge>> createAdjacentEdgePairs(@NotNull List<GridEdge> edges, @NotNull GridNode originPoint, boolean sort) {
        List<GridEdge> sortedEdges = new ArrayList<>(edges);
        if (sort) {
            sortedEdges.sort(new GridEdgeOrderComparator(originPoint));
        }
        return Tuple.from(CollectionUtil.makePairs(sortedEdges, true, true));
    }

    @NotNull
    public Set<GridNode> getAdjacentNodes() {
        return getRelatedStream().collect(Collectors.toSet());
    }

    @NotNull
    public Quadrant getQuadrant(@NotNull NodePoint originPoint) {
        return Quadrant.getQuadrant(this, originPoint);
    }

    @Override
    public double calculateDistanceTo(double x, double y) {
        return Math.sqrt(Math.pow(getX() - x, 2) + Math.pow(getY() - y, 2));
    }

    @Override
    public double calculateDistanceTo(@NotNull NodePoint point) {
        return calculateDistanceTo(point.getX(), point.getY());
    }

    @Override
    public double calculateAngleBetween(@NotNull NodePoint p1, @NotNull NodePoint p2) {
        double angle1 = Math.atan2(p1.getY() - getY(), p1.getX() - getX());
        double angle2 = Math.atan2(p2.getY() - getY(), p2.getX() - getX());
        return angle1 - angle2;
    }

    @Override
    public String toString() {
        return Optional.ofNullable(getName()).orElse(getPoint().toString());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getX(), getY(), getName(), getLabel());
    }

    public boolean equals(Object obj) {

        if (obj == null || !(obj instanceof GridNode)) {
            return false;
        }

        GridNode node = (GridNode) obj;
        return Objects.equals(getX(), node.getX())
                && Objects.equals(getY(), node.getY())
                && Objects.equals(getName(), node.getName())
                && Objects.equals(getLabel(), getLabel());

    }

}
