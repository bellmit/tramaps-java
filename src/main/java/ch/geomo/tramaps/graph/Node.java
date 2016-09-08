/*
 * Copyright (c) 2016 Thomas Zuberbuehler. All rights reserved.
 */

package ch.geomo.tramaps.graph;

import ch.geomo.tramaps.graph.util.OctilinearDirection;
import ch.geomo.tramaps.map.signature.EmptyNodeSignature;
import ch.geomo.tramaps.map.signature.NodeSignature;
import ch.geomo.util.point.NodePoint;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Point;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Stream;

import static ch.geomo.tramaps.geom.util.GeomUtil.getGeomUtil;

/**
 * Represents a node within a {@link Graph}. Each node has a name, a position and
 * a {@link NodeSignature}. When comparing two nodes, the position won't be
 * considered.
 */
public class Node extends Observable implements GraphElement, NodePoint {

    private final Set<Edge> adjacentEdges;

    private String name;

    private Point point;
    private NodeSignature signature;

    private boolean deleted = false;

    /**
     * Creates a new instance of {@link Node} using a {@link EmptyNodeSignature}
     * instance.
     *
     * @see Node#Node(Point, Function)
     */
    public Node(@NotNull Point point) {
        this(point, EmptyNodeSignature::new);
    }

    /**
     * @see #Node(Point)
     */
    public Node(double x, double y) {
        this(getGeomUtil().createPoint(x, y), EmptyNodeSignature::new);
    }

    /**
     * @see #Node(Point, Function)
     */
    public Node(double x, double y, @NotNull Function<Node, NodeSignature> nodeSignatureFactory) {
        this(getGeomUtil().createPoint(x, y), nodeSignatureFactory);
    }

    /**
     * @see #Node(Point, Function)
     */
    public Node(double x, double y, @Nullable String name, @NotNull Function<Node, NodeSignature> nodeSignatureFactory) {
        this(getGeomUtil().createPoint(x, y), nodeSignatureFactory);
        this.name = name;
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
        this(getGeomUtil().createPoint(coordinate), nodeSignatureFactory);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * Creates a new adjacent edge between this and the given node. Subscribes immediately
     * the new edge as an {@link java.util.Observer}.
     *
     * @return the newly connected node (allows to chain this method)
     */
    public Node createAdjacentEdgeTo(@NotNull Node node, @NotNull Set<Route> routes) {
        Edge edge = new Edge(this, node);
        edge.addRoutes(routes);
        adjacentEdges.add(edge);
        addObserver(edge);
        setChanged();
        notifyObservers();
        return node;
    }

    /**
     * Adds a new adjacent edge but ignores given edge if neither node A nor
     * node B is equals to this instance. Subscribes the given edge as an
     * {@link Observer} but does not notify the other observers!
     */
    void addAdjacentEdge(@NotNull Edge edge) { // package-private
        if (!equals(edge.getNodeA()) && !equals(edge.getNodeB())) {
            return;
        }
        adjacentEdges.add(edge);
        addObserver(edge);
    }

    /**
     * Removes an adjacent edge. Nodes will be untouched. Unsubscribe the edge
     * as an {@link Observer}.
     */
    public void removeAdjacentEdge(@NotNull Edge edge) {
        if (!isAdjacent(edge)) {
            return;
        }
        adjacentEdges.remove(edge);
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

    @NotNull
    public Stream<Edge> getAdjacentEdgeStream(@Nullable Edge without) {
        return adjacentEdges.stream()
                .filter(edge -> !edge.equals(without));
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
     * @return the {@link NodeSignature} of this instance
     */
    @NotNull
    public NodeSignature getNodeSignature() {
        return signature;
    }

    /**
     * @return the x-value of this node's position/coordinate
     */
    @Override
    public double getX() {
        return point.getX();
    }

    /**
     * @return the y-value of this node's position/coordinate
     */
    @Override
    public double getY() {
        return point.getY();
    }

    /**
     * Updates the node's position/coordinate and notifies Observers.
     */
    public void updatePosition(@NotNull Point point) {
        this.point = point;
        setChanged();
        notifyObservers();
    }

    /**
     * Updates the node's position/coordinate and notifies Observers.
     */
    public void updatePosition(@NotNull Coordinate coordinate) {
        updatePosition(getGeomUtil().createPoint(coordinate));
    }

    /**
     * Updates the node's position/coordinate and notifies Observers.
     */
    public void updatePosition(double x, double y) {
        updatePosition(getGeomUtil().createPoint(x, y));
    }

    /**
     * Updates the x-value of the node's position/coordinate and notifies Observers.
     */
    public void updateX(double x) {
        updatePosition(x, getY());
    }

    /**
     * Updates the y-value of the node's position/coordinate and notifies Observers.
     */
    public void updateY(double y) {
        updatePosition(getX(), y);
    }

    /**
     * Creates a {@link Point} which is located along given direction and distance away from the position of this
     * node. Does <b>not</b> move this node.
     *
     * @return a {@link Point} located along given direction and distance
     */
    @NotNull
    public Point createMovePoint(@NotNull OctilinearDirection direction, double moveDistance) {

        double x = getX();
        double y = getY();

        switch (direction) {
            case NORTH:
            case NORTH_EAST:
            case NORTH_WEST:
                y = y + moveDistance;
                break;
            case SOUTH:
            case SOUTH_EAST:
            case SOUTH_WEST:
                y = y - moveDistance;
        }

        switch (direction) {
            case EAST:
            case NORTH_EAST:
            case SOUTH_EAST:
                x = x + moveDistance;
                break;
            case WEST:
            case NORTH_WEST:
            case SOUTH_WEST:
                x = x - moveDistance;
                break;
        }

        return getGeomUtil().createPoint(x, y);

    }

    /**
     * @return false since this implementation of {@link GraphElement} is a point ;-)
     */
    @Override
    @Contract("->false")
    public boolean isEdge() {
        return false;
    }

    /**
     * @return true since this implementation of {@link GraphElement} is a point ;-)
     */
    @Override
    @Contract("->true")
    public boolean isNode() {
        return true;
    }

    /**
     * Returns a <b>new</b> instance of the encapsulated {@link Point} representation
     * of this node. (Implemented to satisfy {@link NodePoint} interface.)
     *
     * @return a <b>new</b> instance of {@link Point}
     */
    @NotNull
    @Override
    public Point toPoint() {
        return getGeomUtil().clonePoint(point);
    }

    /**
     * Returns the <b>same</b> instance of the encapsulated {@link Point} representation
     * of this node.
     *
     * @return the nodes point (<b>same</b> instance)
     * @see #toPoint() if you need a new instance
     */
    @NotNull
    public Point getPoint() {
        return point;
    }

    /**
     * Returns a <b>new</b> instance of the encapsulated {@link Point#getCoordinate()}
     * representation of this node. Implemented to satisfy {@link NodePoint} interface.
     *
     * @return a <b>new</b> instance of {@link Coordinate}
     */
    @NotNull
    @Override
    public Coordinate toCoordinate() {
        return new Coordinate(point.getCoordinate());
    }

    /**
     * Returns a <b>same</b> instance of the encapsulated {@link Point#getCoordinate()}
     * representation of this node.
     *
     * @return the nodes coordinate (<b>same</b> instance)
     * @see #toCoordinate() if you need a new instance
     */
    @NotNull
    public Coordinate getCoordinate() {
        return point.getCoordinate();
    }

    @Override
    public boolean isDeleted() {
        return deleted;
    }

    /**
     * Deletes this instance. Removes all edges and unsubscribes observers.
     */
    @Override
    public void delete() {
        // remove adjacent nodes
        getAdjacentEdges().forEach(Edge::delete);
        deleted = true;
        // notify observers a last time
        setChanged();
        notifyObservers();
        // unsubscribe all observers
        deleteObservers();
    }

    /**
     * @return the degree of this node
     */
    public int getNodeDegree() {
        return adjacentEdges.size();
    }

    @Override
    public boolean equals(Object obj) {
        // since a node is equals to the same node but at another position, position
        // is not used to check equality
        return obj instanceof Node
                && Objects.equals(name, ((Node) obj).name)
                && signature.equals(((Node) obj).signature)
                && deleted == ((Node) obj).deleted;
    }

    @Override
    public int hashCode() {
        // hashCode and equals must be matching: a.equals(b) == (a.hashCode() == b.hashCode())
        // therefore position of this node is transient and not used to calculate hash code
        return Objects.hash(name, signature, deleted);
    }

    @Override
    public String toString() {
        return "Node: " + name + "(" + getX() + "/" + getY() + ")";
    }

}
