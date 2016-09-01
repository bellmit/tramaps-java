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
 * Represents a {@link Node} or an {@link Edge} of a {@link Graph}.
 */
public interface GraphElement {

    default boolean isAdjacent(GraphElement element) {
        if (element instanceof Edge) {
            return isAdjacent((Edge) element);
        }
        else if (element instanceof Node) {
            return isAdjacent((Node) element);
        }
        return false;
    }

    @Contract("null->false")
    boolean isAdjacent(@Nullable Edge edge);

    @Contract("null->false")
    boolean isAdjacent(@Nullable Node node);

    @NotNull
    Geometry getGeometry();

    @NotNull
    default Point getCentroid() {
        return this.getGeometry().getCentroid();
    }

    boolean isEdge();

    boolean isNode();

    /**
     * @return true if this {@link GraphElement} was previously deleted
     */
    boolean isDeleted();

    /**
     * Deletes this {@link GraphElement} instance.
     */
    void delete();

}
