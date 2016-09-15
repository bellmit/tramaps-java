/*
 * Copyright (c) 2016 Thomas Zuberbuehler. All rights reserved.
 */

package ch.geomo.tramaps.map.signature;

import ch.geomo.tramaps.graph.Node;
import org.jetbrains.annotations.NotNull;

import static ch.geomo.tramaps.geom.util.GeomUtil.getGeomUtil;

public class BendNodeSignature extends AbstractNodeSignature {

    private static final double ROUTE_MARGIN = 5d;

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
                .map(edge -> edge.calculateEdgeWidth(ROUTE_MARGIN))
                .max(Double::compare)
                .orElse(ROUTE_MARGIN);
        signature = getGeomUtil().createBuffer(node.getPoint(), width / 2, false);
        setChanged();
        notifyObservers();
    }

}
