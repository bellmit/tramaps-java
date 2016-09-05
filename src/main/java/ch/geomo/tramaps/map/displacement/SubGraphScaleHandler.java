/*
 * Copyright (c) 2016 Thomas Zuberbuehler. All rights reserved.
 */

package ch.geomo.tramaps.map.displacement;

import ch.geomo.tramaps.map.MetroMap;
import org.jetbrains.annotations.NotNull;

/**
 * This {@link MetroMapLineSpaceHandler} implementation makes space by scaling sub graphs of the
 * underlying graph of a metro map.
 */
public class SubGraphScaleHandler implements MetroMapLineSpaceHandler {

    private ScaleHandler scaleHandler;

    public SubGraphScaleHandler() {
        this.scaleHandler = new ScaleHandler();
    }

    @Override
    public void makeSpace(@NotNull MetroMap map) {
        // TODO
        // 1. find peripheries
        // 2. create sub graphs
        // 3. scale sub graphs
    }

}
