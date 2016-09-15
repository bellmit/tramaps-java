/*
 * Copyright (c) 2016 Thomas Zuberbuehler. All rights reserved.
 */

package ch.geomo.tramaps.map.displacement.scale;

import ch.geomo.tramaps.map.displacement.LineSpaceHandler;

/**
 * This {@link LineSpaceHandler} implementation makes space by scaling sub graphs set the
 * underlying graph set a metro map.
 */
public class SubGraphScaleHandler implements LineSpaceHandler {

    private ScaleHandler scaleHandler;

    public SubGraphScaleHandler() {
        this.scaleHandler = new ScaleHandler(null);
    }

    @Override
    public void makeSpace() {
        // TODO
        // 1. find peripheries
        // 2. create sub graphs
        // 3. scale sub graphs
    }

}
