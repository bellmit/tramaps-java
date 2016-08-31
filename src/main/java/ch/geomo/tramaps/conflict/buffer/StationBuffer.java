package ch.geomo.tramaps.conflict.buffer;

import ch.geomo.tramaps.graph.Node;
import ch.geomo.tramaps.geo.util.GeomUtil;
import org.jetbrains.annotations.NotNull;

public class StationBuffer extends NodeBuffer {

    public StationBuffer(@NotNull Node node, double margin) {
        super(node, margin);
        node.getSignature().addObserver(this);
    }

    @Override
    public void updateBuffer() {
        this.buffer = GeomUtil.createBuffer(this.node.getSignature().getConvexHull(), this.margin, true);
    }

}
