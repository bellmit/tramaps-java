/*
 * Copyright (c) 2016 Thomas Zuberbuehler. All rights reserved.
 */

package ch.geomo.tramaps.conflict.buffer;

import ch.geomo.tramaps.graph.Edge;
import ch.geomo.tramaps.graph.GraphElement;
import ch.geomo.tramaps.graph.Node;
import com.vividsolutions.jts.geom.Polygon;
import org.jetbrains.annotations.NotNull;

import java.util.Observer;

/**
 * Represents a buffer of a {@link Node} or an {@link Edge} implementing the {@link Observer} pattern.
 */
public interface ElementBuffer extends Observer {

    /**
     * @return the buffer of the {@link #getElement()}
     */
    @NotNull
    Polygon getBuffer();

    /**
     * @return the element
     */
    @NotNull
    GraphElement getElement();

}
