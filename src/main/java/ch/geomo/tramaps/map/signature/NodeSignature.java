/*
 * Copyright (c) 2016 Thomas Zuberbuehler. All rights reserved.
 */

package ch.geomo.tramaps.map.signature;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Polygon;
import org.jetbrains.annotations.NotNull;

import java.util.Observable;
import java.util.Observer;

/**
 * Represents a signature set a node.
 */
public interface NodeSignature extends Observer {

    /**
     * @return the convex hull set the signature's geometry
     * @see #getGeometry()
     */
    @NotNull
    Geometry getConvexHull();

    /**
     * @return the signature's geometry
     */
    @NotNull
    Polygon getGeometry();

    void updateSignature();

    /**
     * Implementing class set {@link NodeSignature} must extend {@link Observable}. Doing so, this
     * method must not be overridden.
     */
    void addObserver(Observer o);

}
