package ch.geomo.tramaps.graph;

import ch.geomo.tramaps.geo.util.GeomUtil;
import ch.geomo.util.tuple.Pair;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.Point;
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

    private List<Point> vertices;

    public Edge(@NotNull Node nodeA, @NotNull Node nodeB) {
        this.nodeA = nodeA;
        this.nodeA.addAdjacentEdge(this);
        this.nodeA.addObserver(this);
        this.nodeB = nodeB;
        this.nodeB.addAdjacentEdge(this);
        this.nodeB.addObserver(this);
        this.routes = new HashSet<>();
        this.vertices = new ArrayList<>();
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

    public List<Point> getVertices() {
        return vertices;
    }

    private void updateLineString() {
        this.lineString = GeomUtil.createLineString(this.getNodeA(), this.getNodeB());
        // this.lineString = GeomUtil.createLineString(this.getNodeA().getPoint(), this.vertices, this.getNodeB().getPoint());
        this.angleToXAxis = (int) Math.ceil(GeomUtil.getAngleToXAxisAsDegree(GeomUtil.createLineString(this.getNodeA(), this.getNodeB())));
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

    public void repairEdge() {
        if (isNonOctilinear()) {
            vertices.clear();
            Node nodeA = this.getNodeA();
            Node nodeB = this.getNodeB();
            double dx = Math.abs(nodeA.getX() - nodeB.getX());
            double dy = Math.abs(nodeA.getY() - nodeB.getY());
            if (dx < dy) {
                Point vertex = GeomUtil.createPoint(nodeA.getX(), Math.ceil(nodeA.getY()+dx));
                vertices.add(vertex);
            }
            else {
                Point vertex = GeomUtil.createPoint(Math.ceil(nodeA.getX()+dy), nodeA.getY());
                vertices.add(vertex);
            }
            updateLineString();
        }
    }

}
