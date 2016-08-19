package ch.geomo.tramaps.conflicts.buffer;

import ch.geomo.tramaps.graph.GraphElement;
import ch.geomo.tramaps.graph.Node;
import ch.geomo.tramaps.geo.util.GeomUtil;
import com.vividsolutions.jts.geom.Polygon;
import org.jetbrains.annotations.NotNull;

import java.util.Observable;

public class NodeBuffer implements ElementBuffer {

    protected final Node node;
    protected final double margin;

    protected Polygon buffer;

    public NodeBuffer(@NotNull Node node, double margin) {
        this.node = node;
        this.margin = margin;
        this.updateBuffer();
    }

    @Override
    public void updateBuffer() {
        this.buffer = GeomUtil.createBuffer(this.node.getSignature().getGeometry(), 25, true);
    }

    @NotNull
    @Override
    public Polygon getBuffer() {
        return this.buffer;
    }

    @NotNull
    @Override
    public GraphElement getElement() {
        return this.node;
    }

    @Override
    public void update(Observable o, Object arg) {
        this.updateBuffer();
    }

}
