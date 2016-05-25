/*
 * Copyright (c) 2016 by Thomas Zuberbühler
 */

package ch.geomo.tramaps.grid;

import ch.geomo.tramaps.geom.Point;
import ch.geomo.tramaps.graph.EdgeOrderComparator;
import ch.geomo.tramaps.graph.Node;
import ch.geomo.tramaps.graph.NodeLabel;
import ch.geomo.tramaps.graph.geo.GeoNode;
import ch.geomo.tramaps.util.CollectionUtil;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;

public class GridNode implements Node<Long, GridEdge> {

    private GeoNode geoNode;

    private Long x;
    private Long y;

    private final Set<GridEdge> edges = new TreeSet<>(new EdgeOrderComparator<>(this));
    private Set<Pair<GridEdge, GridEdge>> edgePairs = null;

    public GridNode(GeoNode geoNode, Point<Long> point) {
        this(geoNode, point.getX(), point.getY());
    }

    public GridNode(GeoNode geoNode, long x, long y) {
        this.geoNode = geoNode;
        this.x = x;
        this.y = y;
    }

    public void addEdge(GridEdge edge) {
        if (edge.contains(this) && !getEdges().contains(edge)) {
            this.edges.add(edge);
            this.clearAdjacentEdgePairCache();
        }
    }

    /**
     * Clears the cache with the adjacent edges.
     */
    public void clearAdjacentEdgePairCache() {
        this.edgePairs = null;
    }

    public GeoNode getGeoNode() {
        return geoNode;
    }

    @NotNull
    @Override
    public String getName() {
        return geoNode.getName();
    }

    @Nullable
    @Override
    public NodeLabel getLabel() {
        return geoNode.getLabel();
    }

    @NotNull
    @Override
    public Long getX() {
        return x;
    }

    public void setX(long x) {
        this.x = x;
    }

    @NotNull
    @Override
    public Long getY() {
        return y;
    }

    public void setY(long y) {
        this.y = y;
    }

    /**
     * Returns a {@link Set} of adjacent edges. Caches values internally.
     *
     * @see #clearAdjacentEdgePairCache() to clear cache
     */
    @NotNull
    @Override
    public Set<Pair<GridEdge, GridEdge>> getAdjacentEdgePairs() {
        if (edgePairs == null) {
            edgePairs = new HashSet<>(CollectionUtil.makePairs(getEdges(), true, true));
        }
        return edgePairs;
    }

    @NotNull
    @Override
    public Set<GridEdge> getEdges() {
        return edges;
    }

    @Override
    public double calculateDistanceTo(@NotNull Point<Long> point) {
        return Math.sqrt(Math.pow(getX() - point.getX(), 2) + Math.pow(getY() - point.getY(), 2));
    }

    @Override
    public double calculateAngleBetween(@NotNull Point<Long> p1, @NotNull Point<Long> p2) {
        double angle1 = Math.atan2(p1.getY() - getY(), p1.getX() - getX());
        double angle2 = Math.atan2(p2.getY() - getY(), p2.getX() - getX());
        return angle1 - angle2;
    }

    @Override
    public String toString() {
        return "{" + getName() + "|" + getX() + "/" + getY() + "}";
    }

    @Override
    public boolean equals(Object obj) {

        if (obj == null || !(obj instanceof GridNode)) {
            return false;
        }

        GridNode node = (GridNode) obj;
        return Objects.equals(getX(), node.getX())
                && Objects.equals(getY(), node.getY())
                && Objects.equals(getName(), node.getName());

    }

    @Override
    public int hashCode() {
        return Objects.hash(getX(), getY(), getName());
    }

}
