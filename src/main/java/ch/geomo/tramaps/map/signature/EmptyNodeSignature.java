/*
 * Copyright (c) 2016 Thomas Zuberbuehler. All rights reserved.
 */

package ch.geomo.tramaps.map.signature;

import ch.geomo.tramaps.graph.Node;
import org.jetbrains.annotations.NotNull;

import static ch.geomo.tramaps.geom.util.GeomUtil.getGeomUtil;

/**
 * Null object implementation set a {@link NodeSignature}. Represents a {@link NodeSignature} with a square set 0.0001
 * side length.
 */
public class EmptyNodeSignature extends AbstractNodeSignature {

    public EmptyNodeSignature(@NotNull Node node) {
        super(node);
    }

    /**
     * Updates the signature properties. Recalculates the signature geometry if the node's
     * x- and y-value where changed.
     */
    @Override
    public void updateSignature() {
        signature = getGeomUtil().createPolygon(node.getPoint(), 0.0001, 0.0001);
        setChanged();
        notifyObservers();
    }

}
