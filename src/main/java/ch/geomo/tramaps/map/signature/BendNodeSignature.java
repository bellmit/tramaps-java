/*
 * Copyright (c) 2016 Thomas Zuberbuehler. All rights reserved.
 */

package ch.geomo.tramaps.map.signature;

import ch.geomo.tramaps.graph.Node;
import ch.geomo.util.geom.GeomUtil;
import org.jetbrains.annotations.NotNull;

public class BendNodeSignature extends AbstractNodeSignature {

    public BendNodeSignature(@NotNull Node node) {
        super(node);
    }

    /**
     * Updates the signature properties. Recalculates the signature geometry if the node's
     * x- and y-value where changed.
     */
    @Override
    public void updateSignature() {
        double width = node.getAdjacentEdges().stream()
                .filter(edge -> !edge.getOriginalDirection(edge.getNodeA()).isHorizontal())
                .map(edge -> edge.calculateEdgeWidth(0))
                .max(Double::compare)
                .orElse(0d);
        double height = node.getAdjacentEdges().stream()
                .filter(edge -> !edge.getOriginalDirection(edge.getNodeA()).isVertical())
                .map(edge -> edge.calculateEdgeWidth(0))
                .max(Double::compare)
                .orElse(0d);
        signature = GeomUtil.createPolygon(node.getPoint(), Math.max(width, 20), Math.max(height, 20));
        setChanged();
        notifyObservers();
    }

}
