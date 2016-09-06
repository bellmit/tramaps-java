/*
 * Copyright (c) 2016 Thomas Zuberbuehler. All rights reserved.
 */

package ch.geomo.tramaps.map.signature;

import ch.geomo.tramaps.geom.util.GeomUtil;
import ch.geomo.tramaps.graph.Node;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Polygon;
import org.jetbrains.annotations.NotNull;

import static ch.geomo.tramaps.geom.util.GeomUtil.getGeomUtil;

/**
 * Null object implementation of a {@link NodeSignature}.
 */
public class EmptyNodeSignature extends NodeSignature {

    protected final Node node;
    protected Polygon signature;

    public EmptyNodeSignature(@NotNull Node node) {
        this.node = node;
        node.addObserver(this);
        updateSignature();
    }

    /**
     * Updates the signature properties. Recalculates the signature geometry if the node's
     * x- and y-value where changed.
     */
    protected void updateSignature() {
        signature = getGeomUtil().createPolygon(node.getPoint(), 0.0001, 0.0001);
        setChanged();
        notifyObservers();
    }

    @NotNull
    @Override
    public Geometry getConvexHull() {
        return getGeometry().convexHull();
    }

    @NotNull
    @Override
    public Polygon getGeometry() {
        return signature;
    }

}
