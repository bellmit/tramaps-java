package ch.geomo.tramaps.graph;

import ch.geomo.tramaps.geo.util.GeomUtil;
import ch.geomo.util.tuple.Pair;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.LineString;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class Edge extends Observable implements Observer, GraphElement {

    private Node nodeA;
    private Node nodeB;

    private LineString lineString;
    private Set<Route> routes;
    private int angleToXAxis;

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
        this.angleToXAxis = (int)Math.ceil(GeomUtil.getAngleToXAxisAsDegree(this.lineString));
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
    public Pair<Node> getNodeTuple() {
        return Pair.of(nodeA, nodeB);
    }

    @NotNull
    public Node getOtherNode(@NotNull Node node) {
        return this.getNodeTuple().getOtherValue(node);
    }

    @Override
    @Contract("null->false")
    public boolean isAdjacent(@Nullable Edge edge) {
        if (edge == null) {
            return false;
        }
        return getNodeA().getAdjacentEdges().contains(edge) || getNodeB().getAdjacentEdges().contains(edge);
    }

    @Override
    @Contract("null->false")
    public boolean isAdjacent(@Nullable Node node) {
        return getNodeA().equals(node) || getNodeB().equals(node);
    }

    @NotNull
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

    public boolean isOctilinear() {
        return angleToXAxis % 45 == 0;
    }

    public boolean isNonOctilinear() {
        return !this.isOctilinear();
    }

}
