/*
 * Copyright (c) 2016 by Thomas Zuberbühler
 */

package ch.geomo.tramaps.graph.geo;

import ch.geomo.tramaps.graph.AbstractNode;
import ch.geomo.tramaps.graph.NodeLabel;
import ch.geomo.tramaps.grid.GridNode;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class GeoNode extends AbstractNode<GeoEdge> {

    /**
     * SuperCSV only
     */
    private Double initialX;

    /**
     * SuperCSV only
     */
    private Double initialY;

    /**
     * SuperCSV only
     */
    public GeoNode() {
        super();
    }

    public GeoNode(String name, double x, double y) {
        super(name, x, y);
    }

    @Override
    public void setName(String name) {
        super.setName(name);
    }

    @Override
    public void setLabel(NodeLabel label) {
        super.setLabel(label);
    }

    /**
     * SuperCSV only
     */
    private void setPoint() {
        if (initialX != null && initialY != null) {
            super.setPoint(initialX, initialY);
        }
    }

    /**
     * SuperCSV only
     */
    public void setX(double x) {
        initialX = x;
        setPoint();
    }

    /**
     * SuperCSV only
     */
    public void setY(double y) {
        initialY = y;
        setPoint();
    }

    @NotNull
    public Set<GeoNode> getAdjacentNodes() {
        return getEdges().stream()
                .map(edge -> edge.getOppositeNode(this))
                .collect(Collectors.toSet());
    }

    @Override
    public boolean equals(Object obj) {

        if (obj == null || !(obj instanceof GeoNode)) {
            return false;
        }

        GeoNode node = (GeoNode) obj;
        return Objects.equals(getX(), node.getX())
                && Objects.equals(getY(), node.getY())
                && Objects.equals(getName(), node.getName());

    }

}
