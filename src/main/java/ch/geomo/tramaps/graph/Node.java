package ch.geomo.tramaps.graph;

import ch.geomo.tramaps.geo.util.GeomUtil;
import ch.geomo.tramaps.map.signature.NodeSignature;
import ch.geomo.tramaps.map.signature.SquareStationSignature;
import ch.geomo.util.point.NodePoint;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Point;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
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

    public Node(@NotNull Coordinate coordinate, @NotNull Function<Node, NodeSignature> nodeSignatureFactory) {
        this(GeomUtil.createPoint(coordinate), nodeSignatureFactory);
    }

    /**
     * Creates a new adjacent edge between this and the given node. Subscribes immediately
     * the new edge as an {@link java.util.Observer}.
     */
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

    /**
     * Adds a new adjacent edge but ignores given edge if neither node A nor
     * node B is equals to this instance. Subscribes the given edge as an
     * {@link Observer} but does not notify the other observers!
     */
    void addAdjacentEdge(@NotNull Edge edge) { // package-private
        if (!this.equals(edge.getNodeA()) && !this.equals(edge.getNodeB())) {
            return;
        }
        adjacentEdges.add(edge);
        addObserver(edge);
    }

    /**
     * Removes an adjacent edge. Nodes will be untouched. Unsubscribe the edge
     * as an {@link Observer}. To remove a node {@link #destroy(Graph)} should
     * be invoked instead.
     *
     * @see #destroy(Graph)
     */
    public void removeAdjacentEdge(@NotNull Edge edge, @Nullable Graph graph) {
        if (!isAdjacent(edge)) {
            return;
        }
        adjacentEdges.remove(edge);
        if (graph != null) {
            graph.removeEdges(edge);
        }
        deleteObserver(edge);
        setChanged();
        notifyObservers();
    }

    /**
     * @return an unmodifiable {@link Set} of all adjacent edges
     */
    @NotNull
    public Set<Edge> getAdjacentEdges() {
        // unmodifiable in order to avoid side effects
        return Collections.unmodifiableSet(adjacentEdges);
    }

    /**
     * Returns the node's geometry. Alias for {@link #getPoint()} in order to
     * satisfy {@link GraphElement}.
     *
     * @see #getPoint()
     */
    @NotNull
    @Override
    public Geometry getGeometry() {
        return getPoint();
    }

    /**
     * @return true if given edge is adjacent to this point
     */
    @Override
    @Contract("null->false")
    public boolean isAdjacent(@Nullable Edge edge) {
        return edge != null && getAdjacentEdges().contains(edge);
    }

    /**
     * @return true if given node is adjacent to this point
     */
    @Override
    @Contract("null->false")
    public boolean isAdjacent(@Nullable Node node) {
        return getAdjacentEdges().stream()
                .anyMatch(edge -> edge.isAdjacent(node));
    }

    /**
     * @return the node's signature
     */
    @NotNull
    public NodeSignature getNodeSignature() {
        return signature;
    }

    /**
     * @return the x-value of this node
     */
    @Override
    public double getX() {
        return point.getX();
    }

    /**
     * @return the y-value of this node
     */
    @Override
    public double getY() {
        return point.getY();
    }

    public void updatePosition(@NotNull Point point) {
        this.point = point;
        setChanged();
        notifyObservers();
    }

    public void updatePosition(@NotNull Coordinate coordinate) {
        updatePosition(GeomUtil.createPoint(coordinate));
    }

    public void updatePosition(double x, double y) {
        updatePosition(GeomUtil.createPoint(x, y));
    }

    public void updateX(double x) {
        updatePosition(x, getY());
    }

    public void updateY(double y) {
        updatePosition(getX(), y);
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

    /**
     * Destroys this instance. Removes all edges and unsubscribes them.
     */
    public void destroy(@Nullable Graph graph) {
        getAdjacentEdges()
                .forEach(edge -> removeAdjacentEdge(edge, graph));
    }

    /**
     * @return the degree of this node
     */
    public int getDegree() {
        return adjacentEdges.size();
    }

}
