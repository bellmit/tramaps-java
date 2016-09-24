/*
 * Copyright (c) 2016 Thomas Zuberbuehler. All rights reserved.
 */

package ch.geomo.tramaps.map.displacement.scale;

import ch.geomo.tramaps.map.MetroMap;
import ch.geomo.tramaps.map.displacement.LineSpaceHandler;
import org.jetbrains.annotations.NotNull;

/**
 * This {@link LineSpaceHandler} implementation makes space by scaling sub graphs set the underlying graph.
 */
public class SubGraphScaleHandler implements LineSpaceHandler {

    private final ScaleHandler scaleHandler;

    public SubGraphScaleHandler(@NotNull MetroMap map) {
        scaleHandler = new ScaleHandler(map);
    }

    @Override
    public void makeSpace() {
        // TODO
        // 1. find peripheries
        // 2. create sub graphs
        // 3. scale sub graphs
    }

}
