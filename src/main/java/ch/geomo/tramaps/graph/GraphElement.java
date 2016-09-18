/*
 * Copyright (c) 2016 Thomas Zuberbuehler. All rights reserved.
 */

package ch.geomo.tramaps.graph;

import ch.geomo.util.doc.HelperMethod;
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
    @HelperMethod
    default boolean isAdjacent(@NotNull GraphElement element) {
        if (element instanceof Edge) {
            return isAdjacent((Edge) element);
        }
        else if (element instanceof Node) {
            return isAdjacent((Node) element);
        }
        return false;
    }

    @HelperMethod
    default boolean isNotAdjacent(@NotNull GraphElement edge) {
        return !isAdjacent(edge);
    }

    /**
     * @return true if given {@link Edge} is adjacent to this {@link GraphElement}
     */
    boolean isAdjacent(@NotNull Edge edge);

    /**
     * @return true if given {@link Edge} is adjacent to this {@link GraphElement}
     */
    boolean isAdjacent(@NotNull Node node);

    @NotNull
    String getName();

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
    default boolean isEdge() {
        return false;
    }

    /**
     * @return true if this {@link GraphElement} is an node
     */
    default boolean isNode() {
        return false;
    }

    /**
     * @return true if this {@link GraphElement} was previously destroyed
     */
    boolean destroyed();

    /**
     * Mark this {@link GraphElement} as destroyed.
     */
    void destroy();

}
