/*
 * Copyright (c) 2016 by Thomas Zuberbühler
 */

package ch.geomo.tramaps.grid;

import ch.geomo.tramaps.graph.AbstractEdge;
import ch.geomo.tramaps.util.tuple.Tuple;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class GridEdge extends AbstractEdge<GridNode> {

    /**
     * Default constructor for SuperCSV.
     */
    public GridEdge() {
        super();
    }

    public GridEdge(@NotNull String name, @NotNull GridNode firstNode, @NotNull GridNode secondNode) {
        super(name, firstNode, secondNode);
    }

    @Override
    public void setName(@NotNull String name) {
        super.setName(name);
    }

    @Override
    public void setNodes(Tuple<GridNode> nodes) {
        super.setNodes(nodes);
    }

    public long getStartX() {
        return (long)super.getNode(0).getX();
    }

    public long getStartY() {
        return (long)super.getNode(0).getY();
    }

    public long getEndX() {
        return (long)super.getNode(1).getX();
    }

    public long getEndY() {
        return (long)super.getNode(1).getY();
    }

    @Override
    public boolean equals(Object obj) {

        if (obj == null || !(obj instanceof GridEdge)) {
            return false;
        }

        GridEdge edge = (GridEdge) obj;
        return Objects.equals(edge.getNodes(), getNodes())
                && Objects.equals(edge.getName(), getName());

    }

}
