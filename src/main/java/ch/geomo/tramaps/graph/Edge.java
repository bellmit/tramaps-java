package ch.geomo.tramaps.graph;

import ch.geomo.tramaps.geo.util.GeomUtil;
import ch.geomo.tramaps.graph.util.AnyDirection;
import ch.geomo.tramaps.graph.util.Direction;
import ch.geomo.tramaps.graph.util.OctilinearDirection;
import ch.geomo.util.pair.Pair;
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
    private Pair<Node> nodePair;

    private LineString lineString;
    private List<Point> vertices;

    private Set<Route> routes;

    private Direction direction;

    public Edge(@NotNull Node nodeA, @NotNull Node nodeB) {
        this.nodeA = nodeA;
        nodeA.addAdjacentEdge(this);
        nodeA.addObserver(this);
        this.nodeB = nodeB;
        nodeB.addAdjacentEdge(this);
        nodeB.addObserver(this);
        routes = new HashSet<>();
        vertices = new ArrayList<>();
        nodePair = Pair.of(nodeA, nodeB);
        updateLineString();
    }

    public double getEdgeWidth(double routeMargin) {
        double width = getRoutes().stream()
                .mapToDouble(Route::getLineWidth)
                .sum();
        return width + routeMargin * (getRoutes().size() - 2);
    }

    @NotNull
    public Node getNodeA() {
        return nodeA;
    }

    @NotNull
    public Node getNodeB() {
        return nodeB;
    }

    @NotNull
    public List<Point> getVertices() {
        return vertices;
    }

    private void updateLineString() {
        lineString = GeomUtil.createLineString(this.getNodeA(), this.getNodeB());
        direction = AnyDirection.fromAngle(Math.ceil(GeomUtil.getAngleToXAxisAsDegree(lineString)));
        setChanged();
        notifyObservers();
    }

    public void setRoutes(@NotNull Collection<Route> routes) {
        this.routes = new HashSet<>(routes);
    }

    @SuppressWarnings("unused")
    public void addRoute(@NotNull Route route) {
        routes.add(route);
    }

    @NotNull
    public Set<Route> getRoutes() {
        return routes;
    }

    @NotNull
    @SuppressWarnings("unused")
    private Pair<Node> getNodePair() {
        return nodePair;
    }

    @Nullable
    @SuppressWarnings("unused")
    public Node getOtherNode(@NotNull Node node) {
        if (nodePair.contains(node)) {
            return nodePair.getOtherValue(node);
        }
        return null;
    }

    @Override
    @Contract("null->false")
    public boolean isAdjacent(@Nullable Edge edge) {
        return edge != null && (getNodeA().getAdjacentEdges().contains(edge) || getNodeB().getAdjacentEdges().contains(edge));
    }

    @Override
    @Contract("null->false")
    public boolean isAdjacent(@Nullable Node node) {
        return nodeA.equals(node) || nodeB.equals(node);
    }

    @NotNull
    public LineString getLineString() {
        return lineString;
    }

    @NotNull
    @Override
    public Geometry getGeometry() {
        return lineString;
    }

    @Override
    public void update(Observable o, Object arg) {
        updateLineString();
    }

    @NotNull
    @Override
    public String toString() {
        return lineString.toString();
    }

    public boolean isNonOctilinear() {
        return !OctilinearDirection.isOctilinear(direction);
    }

    public void repairEdge(double correctionDistance) {
        // TODO find an algorithm to evaluate a vertex in a way that this edge has a octilinear direction
        if (isNonOctilinear()) {
            vertices.clear();
            Node nodeA = this.getNodeA();
            Node nodeB = this.getNodeB();
            double dx = Math.abs(nodeA.getX() - nodeB.getX());
            double dy = Math.abs(nodeA.getY() - nodeB.getY());
            if (dx < dy) {
                Point vertex;
                if (nodeA.getY() < nodeB.getY()) {
                    vertex = GeomUtil.createPoint(nodeA.getX(), Math.ceil(nodeA.getY() - dx));
                }
                else {
                    vertex = GeomUtil.createPoint(nodeA.getX(), Math.ceil(nodeA.getY() + dx));
                }
                //vertices.add(vertex);
            }
            else {
                Point vertex;
                if (nodeA.getX() < nodeB.getX()) {
                    vertex = GeomUtil.createPoint(Math.ceil(nodeA.getX() + dy), nodeA.getY());
                }
                else {
                    vertex = GeomUtil.createPoint(Math.ceil(nodeA.getX() - dy), nodeA.getY());
                }
                //vertices.add(vertex);
            }
            updateLineString();
        }
    }

    /**
     * @return true if vertical to x-axis
     */
    public boolean isVertical() {
        return direction.isVertical();
    }

    /**
     * @return true if horizontal to x-axis
     */
    public boolean isHorizontal() {
        return direction.isHorizontal();
    }

    /**
     * @return true if neither vertical nor horizontal to x-axis but octliniear
     */
    @SuppressWarnings("unused")
    public boolean isDiagonal() {
        return direction.isDiagonal();
    }

    @NotNull
    @SuppressWarnings("unused")
    public Direction getDirection() {
        return direction;
    }

    @SuppressWarnings("unused")
    public double getAngle() {
        return direction.getAngle();
    }

    @Override
    @Contract("->true")
    public boolean isEdge() {
        return true;
    }

    @Override
    @Contract("->false")
    public boolean isNode() {
        return false;
    }

}
