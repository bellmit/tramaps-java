/*
 * Copyright (c) 2016 Thomas Zuberbuehler. All rights reserved.
 */

package ch.geomo.tramaps.conflict.buffer;

import ch.geomo.tramaps.graph.GraphElement;
import ch.geomo.tramaps.graph.Node;
import ch.geomo.util.geom.GeomUtil;
import com.vividsolutions.jts.geom.Polygon;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.Observable;

/**
 * Represents the buffer of a {@link Node}.
 */
public class NodeBuffer implements ElementBuffer {

    protected final Node node;
    protected final double margin;

    protected Polygon buffer;

    public NodeBuffer(@NotNull Node node, double margin) {
        this.node = node;
        this.margin = margin;
        updateBuffer();
    }

    /**
     * Initialize or updates this buffer representation.
     */
    private void updateBuffer() {
        Polygon geometry = node.getNodeSignature().getGeometry();
        buffer = GeomUtil.createBuffer(geometry, margin, true);
    }

    @NotNull
    @Override
    public Polygon getBuffer() {
        return buffer;
    }

    @NotNull
    @Override
    public GraphElement getElement() {
        return node;
    }

    /**
     * Notifies the observers.
     */
    @Override
    public void update(Observable o, Object arg) {
        updateBuffer();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof NodeBuffer
                && Objects.equals(node, ((NodeBuffer) obj).node)
                && Objects.equals(margin, ((NodeBuffer) obj).margin);
    }

    @Override
    public int hashCode() {
        return Objects.hash(node, margin);
    }

    @Override
    public String toString() {
        return "NodeBuffer: {" + node + "}";
    }

}
