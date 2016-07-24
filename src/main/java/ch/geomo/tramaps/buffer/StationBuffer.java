package ch.geomo.tramaps.buffer;

import ch.geomo.tramaps.graph.Node;
import ch.geomo.tramaps.util.GeomUtil;
import org.jetbrains.annotations.NotNull;

public class StationBuffer extends NodeBuffer {

    public StationBuffer(@NotNull Node node, double margin) {
        super(node, margin);
        this.node.getSignature().addObserver(this);
    }

    @Override
    public void updateBuffer() {
        this.buffer = GeomUtil.createBuffer(this.node.getSignature().getConvexHull(), this.margin, true);
    }

}
