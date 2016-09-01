package ch.geomo.tramaps.conflict.buffer;

import ch.geomo.tramaps.geo.util.GeomUtil;
import ch.geomo.tramaps.graph.Edge;
import ch.geomo.tramaps.graph.GraphElement;
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
        edge.addObserver(this);
        this.routeMargin = routeMargin;
        this.edgeMargin = edgeMargin;
        updateBuffer();
    }

    @Override
    public void updateBuffer() {
        double width = edge.calculateEdgeWidth(routeMargin) + edgeMargin * 2;
        buffer = GeomUtil.createBuffer(edge.getLineString(), width / 2, true);
    }

    @NotNull
    @Override
    public Polygon getBuffer() {
        return buffer;
    }

    @NotNull
    @Override
    public GraphElement getElement() {
        return edge;
    }

    @Override
    public void update(Observable o, Object arg) {
        updateBuffer();
    }

}
