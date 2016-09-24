/*
 * Copyright (c) 2016 Thomas Zuberbuehler. All rights reserved.
 */

package ch.geomo.tramaps.map.displacement;

import ch.geomo.tramaps.MainApp;

/**
 * Convenience interface for {@link MainApp} in order to easily replace algorithms/approaches.
 */
public interface LineSpaceHandler {

    /**
     * Starts algorithm and makes space for line and station signatures.
     */
    void makeSpace();

}
