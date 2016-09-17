/*
 * Copyright (c) 2016 Thomas Zuberbuehler. All rights reserved.
 */

package ch.geomo.tramaps.conflict.buffer;

import ch.geomo.tramaps.geom.util.GeomUtil;
import ch.geomo.tramaps.graph.Node;
import org.jetbrains.annotations.NotNull;

public class StationBuffer extends NodeBuffer {

    public StationBuffer(@NotNull Node node, double margin) {
        super(node, margin);
        node.getNodeSignature().addObserver(this);
    }

    @Override
    public void updateBuffer() {
        buffer = GeomUtil.createBuffer(node.getNodeSignature().getConvexHull(), margin, true);
    }

    @Override
    public String toString() {
        return "StationBuffer: {node= " + node + "}";
    }

}
