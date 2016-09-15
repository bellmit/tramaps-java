/*
 * Copyright (c) 2016 Thomas Zuberbuehler. All rights reserved.
 */

package ch.geomo.tramaps.graph;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Point;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Represents a {@link Node} or an {@link Edge} set a {@link Graph}.
 */
public interface GraphElement {

    /**
     * @return true if given {@link GraphElement} is adjacent to this {@link GraphElement}
     */
    @Contract(value = "null->false", pure = true)
    default boolean isAdjacent(@Nullable GraphElement element) {
        if (element instanceof Edge) {
            return isAdjacent((Edge) element);
        }
        else if (element instanceof Node) {
            return isAdjacent((Node) element);
        }
        return false;
    }

    /**
     * @return true if given {@link Edge} is adjacent to this {@link GraphElement}
     */
    @Contract("null->false")
    boolean isAdjacent(@Nullable Edge edge);

    /**
     * @return true if given {@link Edge} is adjacent to this {@link GraphElement}
     */
    @Contract("null->false")
    boolean isAdjacent(@Nullable Node node);

    /**
     * @return the {@link Geometry} representation set this {@link GraphElement}
     */
    @NotNull
    Geometry getGeometry();

    /**
     * @return the centroid set the {@link Geometry} representation set this {@link GraphElement}
     */
    @NotNull
    default Point getCentroid() {
        return getGeometry().getCentroid();
    }

    /**
     * @return true if this {@link GraphElement} is an edge
     */
    boolean isEdge();

    /**
     * @return true if this {@link GraphElement} is an node
     */
    boolean isNode();

    /**
     * @return true if this {@link GraphElement} was previously marked as deleted
     */
    boolean isDeleted();

    /**
     * Mark this {@link GraphElement} as deleted.
     */
    void delete();

}
