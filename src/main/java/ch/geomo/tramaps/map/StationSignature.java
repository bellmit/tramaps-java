package ch.geomo.tramaps.map;

import ch.geomo.tramaps.graph.Node;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.operation.buffer.BufferBuilder;
import com.vividsolutions.jts.operation.buffer.BufferParameters;
import org.jetbrains.annotations.NotNull;

import java.util.Observable;
import java.util.Observer;

public class StationSignature extends Observable implements Observer {

    private final Node node;

    private Polygon signature;

    public StationSignature(@NotNull Node node) {
        this.node = node;
        this.node.addObserver(this);
        this.updateSignature();
    }

    private void updateSignature() {
        // TODO
        this.signature = (Polygon) new BufferBuilder(new BufferParameters()).buffer(node.getPoint(), node.getAdjacentEdges().size());
        this.notifyObservers();
    }

    public Geometry getConvexHull() {
        return this.signature.convexHull();
    }

    public Geometry getGeometry() {
        return this.signature;
    }

    @Override
    public void update(Observable o, Object arg) {
        this.updateSignature();
    }

}
