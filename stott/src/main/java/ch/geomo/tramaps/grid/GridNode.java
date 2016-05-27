/*
 * Copyright (c) 2016 by Thomas Zuberbühler
 */

package ch.geomo.tramaps.grid;

import ch.geomo.tramaps.geom.NodePoint;
import ch.geomo.tramaps.graph.*;
import ch.geomo.tramaps.graph.geo.GeoNode;
import ch.geomo.tramaps.util.CollectionUtil;
import ch.geomo.tramaps.util.tuple.Tuple;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class GridNode extends AbstractNode<GridEdge> {

    private GeoNode geoNode;

    private Set<Tuple<GridEdge>> edgePairs = null;

    private NodePoint originalPoint;

    public GridNode(@NotNull GeoNode geoNode, @NotNull NodePoint point) {
        super(geoNode.getName(), geoNode.getLabel(), (long)point.getX(), (long)point.getY());
        this.geoNode = geoNode;
        this.originalPoint = point;
    }

    public NodePoint getOriginalPoint() {
        return originalPoint;
    }

    @Override
    public void setName(@NotNull String name) {
        super.setName(name);
    }

    @Override
    public void setLabel(@NotNull NodeLabel label) {
        super.setLabel(label);
    }

    @Override
    public void addEdge(@NotNull GridEdge edge) {
        if (edge.contains(this) && !getEdges().contains(edge)) {
            getEdges().add(edge);
            clearAdjacentEdgePairCache();
        }
    }

    /**
     * Clears the cache with the adjacent edges.
     */
    public void clearAdjacentEdgePairCache() {
        this.edgePairs = null;
    }

    @NotNull
    public GeoNode getGeoNode() {
        return geoNode;
    }

    /**
     * Returns a {@link Set} of adjacent edges. Caches values internally.
     *
     * @see #clearAdjacentEdgePairCache() to clear cache
     */
    @NotNull
    @Override
    public Set<Tuple<GridEdge>> getAdjacentEdgePairs() {
        if (edgePairs == null) {
            edgePairs = Tuple.from(CollectionUtil.makePairs(getEdges(), true, true));
        }
        return edgePairs;
    }

    @NotNull
    public Set<GridNode> getAdjacentNodes() {
        return getEdges().stream()
                .map(edge -> edge.getOppositeNode(this))
                .collect(Collectors.toSet());
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

}
