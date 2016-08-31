package ch.geomo.tramaps.graph;

import ch.geomo.tramaps.geo.util.GeomUtil;
import ch.geomo.tramaps.map.MetroMap;
import ch.geomo.tramaps.map.signature.SquareStationSignature;
import ch.geomo.tramaps.map.signature.NodeSignature;
import ch.geomo.util.point.NodePoint;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Point;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Observable;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

public class Node extends Observable implements GraphElement, NodePoint {

    private Point point;

    private final NodeSignature signature;
    private final Set<Edge> adjacentEdges;

    /**
     * Creates a new instance of {@link Node} using a {@link SquareStationSignature}
     * instance.
     *
     * @see Node#Node(Point, Function)
     */
    public Node(@NotNull Point point) {
        this(point, SquareStationSignature::new);
    }

    /**
     * Creates a new instance of {@link Node} using a custom {@link NodeSignature}
     * instance.
     */
    public Node(@NotNull Point point, @NotNull Function<Node, NodeSignature> nodeSignatureFactory) {
        this.point = point;
        adjacentEdges = new HashSet<>();
        signature = nodeSignatureFactory.apply(this);
    }

    public Node(int x, int y) {
        this(GeomUtil.createPoint(x, y));
    }

    public Node(int x, int y, @NotNull Function<Node, NodeSignature> nodeSignatureFactory) {
        this(GeomUtil.createPoint(x, y), nodeSignatureFactory);
    }

    public void createAdjacentEdgeTo(@NotNull Node node, @NotNull Set<Route> routes, @Nullable Graph graph) {
        Edge edge = new Edge(this, node);
        edge.addRoutes(routes);
        adjacentEdges.add(edge);
        if (graph != null) {
            graph.addEdges(edge);
        }
        addObserver(edge);
        setChanged();
        notifyObservers();
    }

    public void removeAdjacentEdge(@NotNull Edge edge, @Nullable Graph graph) {
        adjacentEdges.remove(edge);
        deleteObserver(edge);
        if (graph != null) {
            graph.removeEdges(edge);
        }
        setChanged();
        notifyObservers();
    }

    /**
     * @return all adjacent edges
     */
    @NotNull
    public Set<Edge> getAdjacentEdges() {
        return adjacentEdges;
    }

    /**
     * Adds a new adjacent edge but ignores given edge if neither node A nor
     * node B is equals to this instance.
     */
    public void addAdjacentEdge(@NotNull Edge edge) {
        if (this.equals(edge.getNodeA()) || this.equals(edge.getNodeB())) {
            adjacentEdges.add(edge);
        }
        this.addObserver(edge);
    }

    /**
     * @see #getPoint()
     */
    @NotNull
    @Override
    public Geometry getGeometry() {
        return getPoint();
    }

    @Override
    @Contract("null->false")
    public boolean isAdjacent(@Nullable Edge edge) {
        return edge != null && getAdjacentEdges().contains(edge);
    }

    @Override
    @Contract("null->false")
    public boolean isAdjacent(@Nullable Node node) {
        return getAdjacentEdges().stream()
                .anyMatch(edge -> edge.isAdjacent(node));
    }

    @NotNull
    public NodeSignature getNodeSignature() {
        return signature;
    }

    @Override
    public double getX() {
        return getCoordinate().x;
    }

    @Override
    public double getY() {
        return getCoordinate().y;
    }

    public void setPoint(@NotNull Point point) {
        this.point = point;
        notifyObservers();
    }

    public void setCoordinate(@NotNull Coordinate coordinate) {
        this.point = GeomUtil.createPoint(coordinate);
        setChanged();
        notifyObservers();
    }

    public void setCoordinate(double x, double y) {
        setCoordinate(new Coordinate(x, y));
    }

    public void setX(double x) {
        setCoordinate(x, getY());
    }

    public void setY(double y) {
        setCoordinate(getX(), y);
    }

    @NotNull
    @Override
    public String toString() {
        return point.toString();
    }

    @Override
    @Contract("->false")
    public boolean isEdge() {
        return false;
    }

    @Override
    @Contract("->true")
    public boolean isNode() {
        return true;
    }

    /**
     * @return a <b>new</b> instance of {@link Point}
     */
    @NotNull
    public Point toPoint() {
        return GeomUtil.clonePoint(point);
    }

    /**
     * @return the nodes point (the <b>same</b> instance)
     * @see #toPoint() if you need a new instance
     */
    @NotNull
    public Point getPoint() {
        return point;
    }

    /**
     * @return the nodes coordinate (the <b>same</b> instance)
     * @see #toCoordinate() if you need a new instance
     */
    @NotNull
    public Coordinate getCoordinate() {
        return point.getCoordinate();
    }

    /**
     * @return a <b>new</b> instance of {@link Coordinate}
     */
    @NotNull
    public Coordinate toCoordinate() {
        return new Coordinate(point.getCoordinate());
    }

    public void destroy(@Nullable Graph graph) {
        getAdjacentEdges()
                .forEach(edge -> removeAdjacentEdge(edge, graph));
    }

}
