/*
 * Copyright (c) 2016 by Thomas Zuberbühler
 */

package ch.geomo.tramaps.graph.geo;

import ch.geomo.tramaps.graph.AbstractEdge;
import ch.geomo.tramaps.util.tuple.Tuple;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class GeoEdge extends AbstractEdge<GeoNode> {


    /**
     * SuperCSV only
     */
    private GeoNode initialFirstNode;

    /**
     * SuperCSV only
     */
    private GeoNode initialSecondNode;

    /**
     * SuperCSV only
     */
    public GeoEdge() {
        super();
    }

    public GeoEdge(@NotNull String name, @NotNull GeoNode firstNode, @NotNull GeoNode secondNode) {
        super(name, firstNode, secondNode);
    }

    @Override
    public void setName(@NotNull String name) {
        super.setName(name);
    }

    /**
     * SuperCSV only
     */
    private void setNodes() {
        if (initialFirstNode != null && initialSecondNode != null) {
            super.setNodes(Tuple.of(initialFirstNode, initialSecondNode));
        }
    }

    /**
     * SuperCSV only
     */
    public void setFirstNode(@NotNull GeoNode firstNode) {
        this.initialFirstNode = firstNode;
        setNodes();
    }

    /**
     * SuperCSV only
     */
    public void setSecondNode(@NotNull GeoNode secondNode) {
        this.initialSecondNode = secondNode;
        setNodes();
    }

    @Override
    public boolean equals(Object obj) {

        if (obj == null || !(obj instanceof GeoEdge)) {
            return false;
        }

        GeoEdge edge = (GeoEdge) obj;
        return Objects.equals(edge.getNodes(), getNodes())
                && Objects.equals(edge.getName(), getName());

    }

}
