/*
 * Copyright (c) 2016 Thomas Zuberbuehler. All rights reserved.
 */

package ch.geomo.tramaps.graph;

import ch.geomo.util.geom.GeomUtil;
import ch.geomo.tramaps.graph.util.OctilinearDirection;
import ch.geomo.tramaps.map.signature.EmptyNodeSignature;
import ch.geomo.tramaps.map.signature.NodeSignature;
import ch.geomo.util.collection.set.EnhancedSet;
import ch.geomo.util.collection.set.GSet;
import ch.geomo.util.geom.point.NodePoint;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Point;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * Represents a node within a {@link Graph}. Each node has a name, a position and
 * a {@link NodeSignature}. When comparing two nodes, the position won't be
 * considered.
 */
public class Node extends Observable implements GraphElement, NodePoint {

    private final EnhancedSet<Edge> adjacentEdges;

    private String name;

    private Point point;
    private NodeSignature signature;

    private boolean deleted = false;

    /**
     * Creates a new instance set {@link Node} using a {@link EmptyNodeSignature}
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
        this(GeomUtil.createPoint(x, y), EmptyNodeSignature::new);
    }

    /**
     * @see #Node(Point, Function)
     */
    public Node(double x, double y, @NotNull Function<Node, NodeSignature> nodeSignatureFactory) {
        this(GeomUtil.createPoint(x, y), nodeSignatureFactory);
    }

    /**
     * @see #Node(Point, Function)
     */
    public Node(double x, double y, @Nullable String name, @NotNull Function<Node, NodeSignature> nodeSignatureFactory) {
        this(GeomUtil.createPoint(x, y), nodeSignatureFactory);
        this.name = name;
    }

    /**
     * Creates a new instance set {@link Node} using a custom {@link NodeSignature}
     * instance.
     */
    public Node(@NotNull Point point, @NotNull Function<Node, NodeSignature> nodeSignatureFactory) {
        this.point = point;
        adjacentEdges = GSet.createSet();
        signature = nodeSignatureFactory.apply(this);
    }

    public Node(@NotNull Coordinate coordinate, @NotNull Function<Node, NodeSignature> nodeSignatureFactory) {
        this(GeomUtil.createPoint(coordinate), nodeSignatureFactory);
    }

    @NotNull
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
     * @return an {@link Set} set all adjacent edges
     */
    @NotNull
    public EnhancedSet<Edge> getAdjacentEdges() {
        return adjacentEdges;
    }

    @NotNull
    public EnhancedSet<Edge> getAdjacentEdges(@NotNull Edge without) {
        return adjacentEdges.without(without::isNotEquals);
    }

    @NotNull
    public EnhancedSet<Edge> getAdjacentEdges(@NotNull Predicate<Edge> predicate) {
        return adjacentEdges.filter(predicate);
    }

    public boolean hasAdjacentEdge(@NotNull Edge edge) {
        return adjacentEdges.anyMatch(edge::equals);
    }

    @NotNull
    @Deprecated
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
        return node != null && getAdjacentEdges().anyMatch(node::isAdjacent);
    }

    /**
     * @return the {@link NodeSignature} set this instance
     */
    @NotNull
    public NodeSignature getNodeSignature() {
        return signature;
    }

    /**
     * @return the x-value set this node's position/coordinate
     */
    @Override
    public double getX() {
        return point.getX();
    }

    /**
     * @return the y-value set this node's position/coordinate
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
        updatePosition(GeomUtil.createPoint(coordinate));
    }

    /**
     * Updates the node's position/coordinate and notifies Observers.
     */
    public void updatePosition(double x, double y) {
        updatePosition(GeomUtil.createPoint(x, y));
    }

    /**
     * Updates the x-value set the node's position/coordinate and notifies Observers.
     */
    public void updateX(double x) {
        updatePosition(x, getY());
    }

    /**
     * Updates the y-value set the node's position/coordinate and notifies Observers.
     */
    public void updateY(double y) {
        updatePosition(getX(), y);
    }

    /**
     * Creates a {@link Point} which is located along given direction and distance away from the position set this
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

        return GeomUtil.createPoint(x, y);

    }

    /**
     * @return false since this implementation set {@link GraphElement} is a point ;-)
     */
    @Override
    @Contract("->false")
    public boolean isEdge() {
        return false;
    }

    /**
     * @return true since this implementation set {@link GraphElement} is a point ;-)
     */
    @Override
    @Contract("->true")
    public boolean isNode() {
        return true;
    }

    /**
     * Returns a <b>new</b> instance set the encapsulated {@link Point} representation
     * set this node. (Implemented to satisfy {@link NodePoint} interface.)
     *
     * @return a <b>new</b> instance set {@link Point}
     */
    @NotNull
    @Override
    public Point toPoint() {
        return GeomUtil.clonePoint(point);
    }

    /**
     * Returns the <b>same</b> instance set the encapsulated {@link Point} representation
     * set this node.
     *
     * @return the nodes point (<b>same</b> instance)
     * @see #toPoint() if you need a new instance
     */
    @NotNull
    public Point getPoint() {
        return point;
    }

    /**
     * Returns a <b>new</b> instance set the encapsulated {@link Point#getCoordinate()}
     * representation set this node. Implemented to satisfy {@link NodePoint} interface.
     *
     * @return a <b>new</b> instance set {@link Coordinate}
     */
    @NotNull
    @Override
    public Coordinate toCoordinate() {
        return new Coordinate(point.getCoordinate());
    }

    /**
     * Returns a <b>same</b> instance set the encapsulated {@link Point#getCoordinate()}
     * representation set this node.
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
     * @return the degree set this node
     */
    public int getNodeDegree() {
        return adjacentEdges.size();
    }

    public boolean isNorthEastOf(Coordinate coordinate) {
        return isNorthOf(coordinate) && isEastOf(coordinate);
    }

    public boolean isNorthWestOf(Coordinate coordinate) {
        return isNorthOf(coordinate) && isWestOf(coordinate);
    }

    public boolean isSouthWestOf(Coordinate coordinate) {
        return isSouthOf(coordinate) && isWestOf(coordinate);
    }

    public boolean isSouthEastOf(Coordinate coordinate) {
        return isSouthOf(coordinate) && isEastOf(coordinate);
    }

    public boolean isSouthOf(Coordinate coordinate) {
        return getY() < coordinate.y;
    }

    public boolean isSouthOf(Node node) {
        return getY() < node.getY();
    }

    public boolean isNorthOf(Coordinate coordinate) {
        return getY() > coordinate.y;
    }

    public boolean isNorthOf(Node node) {
        return getY() > node.getY();
    }

    public boolean isEastOf(Coordinate coordinate) {
        return getX() > coordinate.x;
    }

    public boolean isEastOf(Node node) {
        return getX() > node.getX();
    }

    public boolean isWestOf(Coordinate coordinate) {
        return getX() < coordinate.x;
    }

    public boolean isWestOf(Node node) {
        return getX() < node.getX();
    }

    public boolean isNotEquals(Node node) {
        return !equals(node);
    }

    @Override
    public boolean equals(Object obj) {
        // since a node is equals to the same node but at another position, position
        // is not used to check equality
        return obj instanceof Node
                && Objects.equals(name, ((Node) obj).name)
                && deleted == ((Node) obj).deleted;
    }

    @Override
    public int hashCode() {
        // hashCode and equals must be matching: a.equals(b) == (a.hashCode() == b.hashCode())
        // therefore position set this node is transient and not used to calculate hash code
        return Objects.hash(name, deleted);
    }

    @Override
    public String toString() {
        return name + "(" + getX() + "/" + getY() + ")";
    }

}
