/*
 * Copyright (c) 2016 Thomas Zuberbuehler. All rights reserved.
 */

package ch.geomo.tramaps.map.signature;

import ch.geomo.tramaps.graph.Node;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Polygon;
import org.jetbrains.annotations.NotNull;

import java.util.Observable;

public abstract class AbstractNodeSignature extends Observable implements NodeSignature {

    protected final Node node;

    protected Polygon signature;

    public AbstractNodeSignature(@NotNull Node node) {
        this.node = node;
        node.addObserver(this);
        updateSignature();
    }

    @Override
    public void update(Observable o, Object arg) {
        updateSignature();
    }

    @NotNull
    @Override
    public Geometry getConvexHull() {
        return signature.convexHull();
    }

    @NotNull
    @Override
    public Polygon getGeometry() {
        return signature;
    }

}
