package ch.geomo.tramaps.map.signature;

import ch.geomo.tramaps.geo.util.GeomUtil;
import ch.geomo.tramaps.graph.Node;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Polygon;
import org.jetbrains.annotations.NotNull;

public class BendNodeSignature extends NodeSignature {

    private final Node node;
    private Polygon signature;

    public BendNodeSignature(@NotNull Node node) {
        this.node = node;
        node.addObserver(this);
        updateSignature();
    }

    /**
     * Updates the signature properties. Recalculates the signature geometry if the node's
     * x- and y-value where changed.
     */
    protected void updateSignature() {
        signature = GeomUtil.createPolygon(node.getPoint(), 2, 2);
        setChanged();
        notifyObservers();
    }

    @NotNull
    @Override
    public Geometry getConvexHull() {
        return getGeometry().convexHull();
    }

    @NotNull
    @Override
    public Polygon getGeometry() {
        return signature;
    }

}
