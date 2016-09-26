/*
 * Copyright (c) 2016 Thomas Zuberbuehler. All rights reserved.
 */

package ch.geomo.tramaps.graph;

import ch.geomo.tramaps.map.signature.NodeSignature;
import ch.geomo.util.collection.set.EnhancedSet;
import ch.geomo.util.collection.set.GSet;
import ch.geomo.util.geom.GeomUtil;
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

/**
 * Represents a node within a {@link Graph}. Each node has a name, a position and a {@link NodeSignature}.
 * <p>
 * Note: When comparing two nodes, the position won't be considered.
 */
public class Node extends Observable implements GraphElement, NodePoint {

    private final EnhancedSet<Edge> adjacentEdges;
    private final NodeSignature signature;

    private String name;

    private Point point;

    private boolean destroyed = false;

    public Node(@NotNull String name, double x, double y, @NotNull Function<Node, NodeSignature> nodeSignatureFactory) {
        this(name, GeomUtil.createPoint(x, y), nodeSignatureFactory);
    }

    private Node(@NotNull String name, @NotNull Point point, @NotNull Function<Node, NodeSignature> nodeSignatureFactory) {
        this.name = name;
        this.point = point;
        adjacentEdges = GSet.createSet();
        signature = nodeSignatureFactory.apply(this);
    }

    @NotNull
    public String getName() {
        return name;
    }

    public void setName(@NotNull String name) {
        this.name = name;
    }

    /**
     * Creates a new adjacent edge between this and the given node. Subscribes immediately the new edge
     * as an {@link java.util.Observer}.
     * @return the newly connected node (allowing to chain this method)
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
     * Adds a new adjacent edge but ignores given edge if neither node A nor node B is equals to this instance.
     * Subscribes the given edge as an {@link Observer} but does not notify the other observers!
     */
    /* package-private */ void addAdjacentEdge(@NotNull Edge edge) {
        if (!equals(edge.getNodeA()) && !equals(edge.getNodeB())) {
            return;
        }
        adjacentEdges.add(edge);
        addObserver(edge);
        setChanged();
        notifyObservers();
    }

    /**
     * Removes an adjacent edge. Nodes will be untouched. Unsubscribe the edge as an {@link Observer}.
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
     * @return an {@link EnhancedSet} of all adjacent edges
     */
    @NotNull
    public EnhancedSet<Edge> getAdjacentEdges() {
        return adjacentEdges;
    }

    /**
     * @return the adjacent edge shared with this node and the given node or null if they are not adjacent
     */
    @Nullable
    public Edge getAdjacentEdgeWith(@NotNull Node otherNode) {
        return adjacentEdges.filter(edge -> edge.isAdjacent(this) && edge.isAdjacent(otherNode)).first().orElse(null);
    }

    /**
     * @return an {@link EnhancedSet} of all adjacent edges <b>without the given edge</b>
     */
    @NotNull
    public EnhancedSet<Edge> getAdjacentEdges(@NotNull Edge without) {
        return adjacentEdges.without(without::equals);
    }

    /**
     * @return an {@link EnhancedSet} of all adjacent edges <b>matching given {@link Predicate}</b>
     */
    @NotNull
    public EnhancedSet<Edge> getAdjacentEdges(@NotNull Predicate<Edge> predicate) {
        return adjacentEdges.filter(predicate);
    }

    /**
     * Returns the node's geometry. Alias for {@link #getPoint()} in order to satisfy {@link GraphElement}.
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
        return node != null && getAdjacentEdges().anyMatch(node::isAdjacent);
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
     * Updates the node's position/coordinate and notifies the {@link Observer}s.
     */
    public void updatePosition(double x, double y) {
        point = GeomUtil.createPoint(x, y);
        setChanged();
        notifyObservers();
    }

    /**
     * @see #updatePosition(double, double)
     */
    public void updatePosition(@NotNull Coordinate coordinate) {
        updatePosition(coordinate.x, coordinate.y);
    }

    /**
     * Updates the x-value of the node's position/coordinate and notifies the {@link Observer}s.
     * @see #updatePosition(double, double)
     */
    public void updateX(double x) {
        updatePosition(x, getY());
    }

    /**
     * Updates the y-value of the node's position/coordinate and notifies the {@link Observer}s.
     * @see #updatePosition(double, double)
     */
    public void updateY(double y) {
        updatePosition(getX(), y);
    }

    /**
     * @return a <b>new</b> instance of the encapsulated {@link Point} representation of this node
     */
    @NotNull
    @Override
    public Point toPoint() {
        return GeomUtil.clonePoint(point);
    }

    /**
     * @return the <b>same</b> instance of the encapsulated {@link Point} representation of this node
     * @see #toPoint() if you need a new instance
     */
    @NotNull
    public Point getPoint() {
        return point;
    }

    /**
     * @return a <b>new</b> instance of the encapsulated {@link Point#getCoordinate()} representation of this node
     */
    @NotNull
    @Override
    public Coordinate toCoordinate() {
        Coordinate coordinate = GeomUtil.createCoordinate(point.getCoordinate());
        if (coordinate != null) {
            return coordinate;
        }
        throw new IllegalStateException("Should never reach this point. A node always has a point geometry.");
    }

    /**
     * @return the <b>same</b> instance of the encapsulated {@link Point#getCoordinate()} representation of this node
     * @see #toCoordinate() if you need a new instance
     */
    @NotNull
    public Coordinate getCoordinate() {
        return point.getCoordinate();
    }

    @Override
    public boolean destroyed() {
        return destroyed;
    }

    @Override
    public void destroy() {
        // remove adjacent nodes
        getAdjacentEdges().forEach(Edge::destroy);
        destroyed = true;
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

    public boolean isSouthOf(@NotNull Coordinate coordinate) {
        return getY() < coordinate.y;
    }

    public boolean isSouthOf(@NotNull Node... node) {
        return Arrays.stream(node)
                .allMatch(n -> isSouthOf(n.getCoordinate()));
    }

    public boolean isSouthOf(@NotNull Edge edge) {
        return isSouthOf(edge.getNodeA(), edge.getNodeB());
    }

    public boolean isNorthOf(@NotNull Coordinate coordinate) {
        return getY() > coordinate.y;
    }

    public boolean isNorthOf(@NotNull Node... node) {
        return Arrays.stream(node)
                .allMatch(n -> isNorthOf(n.getCoordinate()));
    }

    public boolean isNorthOf(@NotNull Edge edge) {
        return isNorthOf(edge.getNodeA(), edge.getNodeB());
    }

    public boolean isEastOf(@NotNull Coordinate coordinate) {
        return getX() > coordinate.x;
    }

    public boolean isEastOf(@NotNull Node... node) {
        return Arrays.stream(node)
                .allMatch(n -> isEastOf(n.getCoordinate()));
    }

    public boolean isEastOf(@NotNull Edge edge) {
        return isEastOf(edge.getNodeA(), edge.getNodeB());
    }

    public boolean isWestOf(@NotNull Coordinate coordinate) {
        return getX() < coordinate.x;
    }

    public boolean isWestOf(@NotNull Node... node) {
        return Arrays.stream(node)
                .allMatch(n -> isWestOf(n.getCoordinate()));
    }

    public boolean isWestOf(@NotNull Edge edge) {
        return isWestOf(edge.getNodeA(), edge.getNodeB());
    }

    @Contract("null -> true")
    public boolean isNotEquals(@Nullable Node node) {
        return node == null || !equals(node);
    }

    @Override
    public boolean equals(Object obj) {
        // since a node is equals to the same node but at another position, position
        // is not used to check equality
        return obj instanceof Node
                && Objects.equals(name, ((Node) obj).name)
                && destroyed == ((Node) obj).destroyed;
    }

    @Override
    public int hashCode() {
        // hashCode and equals must be matching: a.equals(b) == (a.hashCode() == b.hashCode())
        // therefore position of this node is transient and not used to calculate hash code
        return Objects.hash(name, destroyed);
    }

    @Override
    public String toString() {
        return "Node: {" + name + "(" + getX() + "/" + getY() + ")}";
    }

}
