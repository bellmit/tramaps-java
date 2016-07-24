package ch.geomo.tramaps.graph;

import ch.geomo.tramaps.util.GeomUtil;
import ch.geomo.tramaps.util.tuple.Tuple;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.Point;
import org.geotools.geometry.jts.JTSFactoryFinder;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;

public class Edge extends Observable implements Observer, GraphElement {

    private Node nodeA;
    private Node nodeB;

    private LineString lineString;

    public Edge(@NotNull Node nodeA, @NotNull Node nodeB) {
        this.nodeA = nodeA;
        this.nodeA.addObserver(this);
        this.nodeB = nodeB;
        this.nodeB.addObserver(this);
        this.updateLineString();
    }

    @NotNull
    public Node getNodeA() {
        return nodeA;
    }

    @NotNull
    public Node getNodeB() {
        return nodeB;
    }

    private void updateLineString() {
        this.lineString = GeomUtil.createLineString(this.getNodeA(), this.getNodeB());
        this.notifyObservers();
    }

    @NotNull
    public Set<Route> getRoutes() {
        return Collections.emptySet();
    }

    @NotNull
    public Tuple<Node> getNodeTuple() {
        return Tuple.of(nodeA, nodeB);
    }

    @NotNull
    public Node getOtherNode(@NotNull Node node) {
        return this.getNodeTuple().getOtherValue(node);
    }

    @Override
    public boolean isAdjacent(Edge edge) {
        return getNodeA().getAdjacentEdges().contains(edge) || getNodeB().getAdjacentEdges().contains(edge);
    }

    @Override
    public boolean isAdjacent(Node node) {
        return getNodeA().equals(node) || getNodeB().equals(node);
    }

    public LineString getLineString() {
        return this.lineString;
    }

    @NotNull
    @Override
    public Geometry getGeometry() {
        return this.lineString;
    }

    @Override
    public void update(Observable o, Object arg) {
        this.updateLineString();
    }

}
