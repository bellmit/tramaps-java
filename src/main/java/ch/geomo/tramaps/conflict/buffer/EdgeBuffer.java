package ch.geomo.tramaps.conflict.buffer;

import ch.geomo.tramaps.graph.Edge;
import ch.geomo.tramaps.graph.GraphElement;
import ch.geomo.tramaps.geo.util.GeomUtil;
import com.vividsolutions.jts.geom.Polygon;
import org.jetbrains.annotations.NotNull;

import java.util.Observable;
import java.util.Observer;

public class EdgeBuffer implements ElementBuffer, Observer {

    private final Edge edge;
    private final double routeMargin;
    private final double edgeMargin;

    private Polygon buffer;

    public EdgeBuffer(@NotNull Edge edge, double routeMargin, double edgeMargin) {
        this.edge = edge;
        this.edge.addObserver(this);
        this.routeMargin = routeMargin;
        this.edgeMargin = edgeMargin;
        this.updateBuffer();
    }

    @Override
    public void updateBuffer() {
        double width = this.edge.getEdgeWidth(this.routeMargin) + this.edgeMargin * 2;
        this.buffer = GeomUtil.createBuffer(this.edge.getLineString(), width / 2, true);
    }

    @NotNull
    @Override
    public Polygon getBuffer() {
        return this.buffer;
    }

    @NotNull
    @Override
    public GraphElement getElement() {
        return this.edge;
    }

    @Override
    public void update(Observable o, Object arg) {
        this.updateBuffer();
    }

}
