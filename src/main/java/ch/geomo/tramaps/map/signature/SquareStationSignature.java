/*
 * Copyright (c) 2016 Thomas Zuberbuehler. All rights reserved.
 */

package ch.geomo.tramaps.map.signature;

import ch.geomo.tramaps.graph.Node;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Polygon;
import org.jetbrains.annotations.NotNull;

import static ch.geomo.tramaps.geom.util.GeomUtil.getGeomUtil;

/**
 * A simple implementation of a station signature. The signature's form
 * is a square.
 */
public class SquareStationSignature extends NodeSignature {

    private static final double ROUTE_MARGIN = 5d;

    private final Node node;
    private Polygon signature;

    public SquareStationSignature(@NotNull Node node) {
        this.node = node;
        node.addObserver(this);
        updateSignature();
    }

    /**
     * Updates the signature properties. Recalculates the signature geometry if the node's
     * x- and y-value where changed.
     */
    protected void updateSignature() {
        double width = node.getAdjacentEdges().stream()
                .map(edge -> edge.calculateEdgeWidth(ROUTE_MARGIN))
                .max(Double::compare)
                .orElse(ROUTE_MARGIN);
        signature = getGeomUtil().createBuffer(node.getPoint(), width / 2, false);
        setChanged();
        notifyObservers();
    }

    /**
     * @see NodeSignature#getConvexHull()
     */
    @NotNull
    @Override
    public Geometry getConvexHull() {
        return signature.convexHull();
    }

    /**
     * @see NodeSignature#getGeometry()
     */
    @NotNull
    @Override
    public Polygon getGeometry() {
        return signature;
    }

}
