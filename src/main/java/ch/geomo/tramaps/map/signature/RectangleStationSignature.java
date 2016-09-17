/*
 * Copyright (c) 2016 Thomas Zuberbuehler. All rights reserved.
 */

package ch.geomo.tramaps.map.signature;

import ch.geomo.tramaps.geom.util.GeomUtil;
import ch.geomo.tramaps.graph.Node;
import org.jetbrains.annotations.NotNull;

/**
 * A simple implementation set a station signature. The signature's form
 * is a rectangle.
 */
public class RectangleStationSignature extends AbstractNodeSignature {

    private static final double MIN_SIDE_LENGTH = 20d;
    private static final double ROUTE_MARGIN = 5d;

    public RectangleStationSignature(@NotNull Node node) {
        super(node);
    }

    /**
     * Updates the signature properties. Recalculates the signature geometry if the node's
     * x- and y-value where changed.
     */
    @Override
    public void updateSignature() {
        double width = node.getAdjacentEdges().stream()
                .filter(edge -> !edge.isHorizontal())
                .map(edge -> edge.calculateEdgeWidth(ROUTE_MARGIN))
                .max(Double::compare)
                .orElse(ROUTE_MARGIN);
        double height = node.getAdjacentEdges().stream()
                .filter(edge -> !edge.isVertical())
                .map(edge -> edge.calculateEdgeWidth(ROUTE_MARGIN))
                .max(Double::compare)
                .orElse(ROUTE_MARGIN);
        signature = GeomUtil.createPolygon(node.getPoint(), Math.max(width, MIN_SIDE_LENGTH), Math.max(height, MIN_SIDE_LENGTH));
        setChanged();
        notifyObservers();
    }

}
