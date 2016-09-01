/*
 * Copyright (c) 2016 Thomas Zuberbuehler. All rights reserved.
 */

package ch.geomo.tramaps.conflict.buffer;

import ch.geomo.tramaps.geo.util.GeomUtil;
import ch.geomo.tramaps.graph.Node;
import org.jetbrains.annotations.NotNull;

public class StationBuffer extends NodeBuffer {

    public StationBuffer(@NotNull Node node, double margin) {
        super(node, margin);
        node.getNodeSignature().addObserver(this);
    }

    @Override
    public void updateBuffer() {
        this.buffer = GeomUtil.createBuffer(this.node.getNodeSignature().getConvexHull(), this.margin, true);
    }

}
