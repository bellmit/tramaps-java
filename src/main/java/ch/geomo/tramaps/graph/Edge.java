package ch.geomo.tramaps.graph;

import ch.geomo.tramaps.geo.util.GeomUtil;
import ch.geomo.util.tuple.Tuple;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.LineString;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class Edge extends Observable implements Observer, GraphElement {

    private Node nodeA;
    private Node nodeB;

    private LineString lineString;
    private Set<Route> routes;

    public Edge(@NotNull Node nodeA, @NotNull Node nodeB) {
        this.nodeA = nodeA;
        this.nodeA.addAdjacentEdge(this);
        this.nodeA.addObserver(this);
        this.nodeB = nodeB;
        this.nodeB.addAdjacentEdge(this);
        this.nodeB.addObserver(this);
        this.routes = new HashSet<>();
        this.updateLineString();
    }

    public double getEdgeWidth(double routeMargin) {
        double width = this.getRoutes().stream()
                .mapToDouble(Route::getLineWidth)
                .sum();
        return width + routeMargin * (this.getRoutes().size() - 2);
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
        this.setChanged();
        this.notifyObservers();
    }

    public void setRoutes(@NotNull Collection<Route> routes) {
        this.routes = new HashSet<>(routes);
    }

    public void addRoute(@NotNull Route route) {
        this.routes.add(route);
    }

    @NotNull
    public Set<Route> getRoutes() {
        return routes;
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

    @Override
    public String toString() {
        return lineString.toString();
    }
}
