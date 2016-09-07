/*
 * Copyright (c) 2016 Thomas Zuberbuehler. All rights reserved.
 */

package ch.geomo.tramaps.graph;

import ch.geomo.tramaps.graph.util.AnyDirection;
import ch.geomo.tramaps.graph.util.Direction;
import ch.geomo.tramaps.graph.util.OctilinearDirection;
import ch.geomo.util.Loggers;
import ch.geomo.util.pair.Pair;
import ch.geomo.util.point.NodePoint;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.LineString;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

import static ch.geomo.tramaps.geom.util.GeomUtil.getGeomUtil;

/**
 * Represents an edge within a {@link Graph}. Each edge has a name, a position,
 * a start node, an end node and a {@link Set} of routes. When comparing
 * two edges, the position of the nodes won't be considered.
 */
public class Edge extends Observable implements Observer, GraphElement {

    private final Pair<Node> nodePair;
    private final Set<Route> routes;
    private final Direction originalDirection;
    private final double originalLength;

    private String name;

    private LineString lineString;
    private Direction direction;

    private boolean deleted = false;

    /**
     * Creates a new instance of {@link Edge} with given nodes.
     */
    public Edge(@NotNull Node nodeA, @NotNull Node nodeB) {

        nodePair = Pair.of(nodeA, nodeB);
        routes = new HashSet<>();

        nodeA.addAdjacentEdge(this);
        nodeB.addAdjacentEdge(this);
        updateEdge();

        // cache original direction of this edge
        originalDirection = AnyDirection.fromAngle(calculateAngle());
        originalLength = lineString.getLength();

    }

    public Edge(@NotNull Node nodeA, @NotNull Node nodeB, @NotNull String name) {
        this(nodeA, nodeB);
        this.name = name;
    }

    /**
     * Calculates the edge width of this edge using given margin between
     * the routes.
     *
     * @return the width of this edge
     */
    public double calculateEdgeWidth(double routeMargin) {
        double width = getRoutes().stream()
                .mapToDouble(Route::getLineWidth)
                .sum();
        return width + routeMargin * (getRoutes().size() - 2);
    }

    /**
     * @return the edge's name if available
     */
    @NotNull
    public String getName() {
        return Optional.ofNullable(name)
                .orElse(getNodeA().getName() + " <-> " + getNodeB().getName());
    }

    /**
     * Sets the edge's name.
     */
    public void setName(@Nullable String name) {
        this.name = name;
    }

    /**
     * @return the first node of this edge
     */
    @NotNull
    public Node getNodeA() {
        return nodePair.getFirst();
    }

    /**
     * @return the second node of this edge
     */
    @NotNull
    public Node getNodeB() {
        return nodePair.getSecond();
    }

    /**
     * @return the angle between Y axis and this edge (starting north, clockwise)
     */
    private double calculateAngle() {
        double angle = getGeomUtil().getAngleBetweenAsDegree(getNodeA(), NodePoint.of(getNodeA().getX(), getNodeA().getY() + 5d), getNodeB());
        angle = (angle + 360) % 360;
        // we do tolerate an imprecision of 1 degree
        if (angle % 45 < 0.5 || angle % 45 > 44.5) {
            return Math.round(angle / 45) * 45;
        }
        return angle;
    }

    /**
     * Updates the {@link LineString} representation and notifies Observers.
     */
    protected final void updateEdge() {
        lineString = getGeomUtil().createLineString(getNodeA(), getNodeB());
        double angle = calculateAngle();
        direction = AnyDirection.fromAngle(angle);
        setChanged();
        notifyObservers();
    }

    /**
     * Adds given routes to this edge and notifies Observers. Ignores
     * duplicated routes.
     */
    public void addRoutes(@NotNull Collection<Route> routes) {
        this.routes.addAll(routes);
        setChanged();
        notifyObservers();
    }

    /**
     * Adds given routes to this edge and notifies Observers. Ignores
     * duplicated routes.
     */
    public void addRoutes(@NotNull Route... routes) {
        addRoutes(Arrays.asList(routes));
    }

    /**
     * @return an unmodifiable {@link Set} with all routes
     */
    @NotNull
    public Set<Route> getRoutes() {
        // unmodifiable in order to avoid side effects
        return Collections.unmodifiableSet(routes);
    }

    /**
     * Gets the adjacent {@link Node} of given {@link Node} on the other side
     * of this edge. May throws a {@link NoSuchElementException} if given
     * node is not a start or end node of this edge. To avoid this exception,
     * test first with {@link #isAdjacent(Node)}.
     *
     * @return the adjacent {@link Node} of given {@link Node}
     * @throws NoSuchElementException if given node is not an end node of this edge
     */
    @NotNull
    public Node getOtherNode(@NotNull Node node) {
        return nodePair.getOtherValue(node);
    }

    /**
     * @return true if the given {@link Edge} is adjacent to this edge
     */
    @Override
    @Contract("null->false")
    public boolean isAdjacent(@Nullable Edge edge) {
        return edge != null && (getNodeA().getAdjacentEdges().contains(edge) || getNodeB().getAdjacentEdges().contains(edge));
    }

    /**
     * @return true if the given {@link Node} is adjacent to this edge
     */
    @Override
    @Contract("null->false")
    public boolean isAdjacent(@Nullable Node node) {
        return nodePair.contains(node);
    }

    /**
     * @return the underlying {@link LineString}
     */
    @NotNull
    public LineString getLineString() {
        return lineString;
    }

    /**
     * @return the underlying {@link LineString}
     */
    @NotNull
    @Override
    public Geometry getGeometry() {
        return getLineString();
    }

    /**
     * @return true if this instance is not octilinear
     */
    @Contract(pure = true)
    public boolean isNotOctilinear() {
        return !OctilinearDirection.isOctilinear(direction);
    }

    /**
     * @return true if vertical to x-axis
     */
    @Contract(pure = true)
    public boolean isVertical() {
        return direction.isVertical();
    }

    /**
     * @return true if horizontal to x-axis
     */
    @Contract(pure = true)
    public boolean isHorizontal() {
        return direction.isHorizontal();
    }

    /**
     * @return true if neither vertical nor horizontal to x-axis <b>but octliniear</b>
     */
    @Contract(pure = true)
    public boolean isOctilinearDiagonal() {
        return direction.isDiagonal();
    }

    /**
     * Returns the {@link Direction} of this edge starting at given {@link Node}. Returns
     * the given {@link Direction} if given {@link Node} is null.
     *
     * @see #getDirection(Node)
     * @see #getOriginalDirection(Node)
     */
    @NotNull
    @Contract(pure = true)
    private Direction getDirection(@Nullable Node node, @NotNull Direction direction) {
        if (node == null || getNodeA().equals(node)) {
            return direction;
        }
        else if (getNodeB().equals(node)) {
            return direction.opposite();
        }
        String message = "Node " + node.getName() + " must be equals to an end node of " + getName() + ".";
        throw new IllegalStateException(message);
    }

    /**
     * Returns the <b>current</b> {@link Direction} of this edge starting at given {@link Node}. Returns
     * the <b>current</b> {@link Direction} of the node A when given {@link Node} is null.
     *
     * @return the <b>current</b> direction of this edge from <b>node A or B depending on the node</b>, returns the current direction of node A if null is passed
     * @throws IllegalArgumentException if given node is neither equal to node A nor node B
     */
    @NotNull
    @Contract(pure = true)
    public Direction getDirection(@Nullable Node node) {
        return getDirection(node, direction);
    }

    /**
     * Returns the <b>original</b> {@link Direction} of this edge starting at given {@link Node}. Returns
     * the <b>original</b> {@link Direction} of the node A when given {@link Node} is null.
     *
     * @return the <b>original</b> direction of this edge from <b>node A or B depending on the node</b>, returns the original direction of node A if null is passed
     * @throws IllegalArgumentException if given node is neither equal to node A nor node B
     */
    @NotNull
    @Contract(pure = true)
    public Direction getOriginalDirection(@Nullable Node node) {
        return getDirection(node, originalDirection);
    }

    /**
     * @return true if this edge has the same {@link Direction} of given {@link Node} and {@link Direction}
     */
    @Contract(pure = true)
    public boolean hasDirectionOf(@NotNull Node startNode, @NotNull Direction direction) {
        return direction.getAngle() == getDirection(startNode).getAngle();
    }

    /**
     * @return true if this edge has the <b>opposite</b> {@link Direction} of given {@link Node} and {@link Direction}
     */
    @Contract(pure = true)
    public boolean hasOppositeDirectionOf(@NotNull Node startNode, @NotNull Direction direction) {
        return direction.isOpposite(getDirection(startNode));
    }

    /**
     * @return true since this implementation of {@link GraphElement} is an edge ;-)
     */
    @Override
    @Contract(value = "->true", pure = true)
    public boolean isEdge() {
        return true;
    }

    /**
     * @return false since this implementation of {@link GraphElement} is an edge ;-)
     */
    @Override
    @Contract(value = "->false", pure = true)
    public boolean isNode() {
        return false;
    }

    /**
     * @return true if this instance is <b>not</b> equals with given {@link Edge}
     */
    @Contract(value = "null->true", pure = true)
    public boolean isNotEquals(@Nullable Edge edge) {
        return !equals(edge);
    }

    /**
     * @return true if given edge has the same nodes than this instance
     */
    @Contract(pure = true)
    public boolean equalNodes(Edge edge) {
        return getNodeA().isAdjacent(edge) && getNodeB().isAdjacent(edge);
    }

    /**
     * Returns true if this edge was previously deleted. If deleted this edge is
     * disconnected from the start and end node.
     *
     * @return true if this edge is marked as delete
     */
    @Contract(pure = true)
    public boolean isDeleted() {
        return deleted;
    }

    /**
     * Remove this edge from the list of the adjacent edges in the start and end
     * node. Marks this edge as deleted and unsubscribe all observers.
     */
    public void delete() {
        // remove from adjacent nodes
        getNodeA().removeAdjacentEdge(this);
        getNodeB().removeAdjacentEdge(this);
        deleted = true;
        // notify observers a last time
        setChanged();
        notifyObservers();
        // unsubscribe all observers
        deleteObservers();
    }

    /**
     * @return true if this edge has routes
     */
    @Contract(pure = true)
    public boolean hasRoutes() {
        return !routes.isEmpty();
    }

    /**
     * @return the edge length based on the related {@link LineString}
     */
    public double getLength() {
        return lineString.getLength();
    }

    /**
     * @return the original edge length of this {@link Edge}
     */
    @Contract(pure = true)
    public double getOriginalLength() {
        return originalLength;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Edge
                && Objects.equals(name, ((Edge) obj).name)
                && Objects.equals(nodePair, ((Edge) obj).nodePair)
                // && CollectionUtil.equals(routes, ((Edge) obj).routes)
                && deleted == ((Edge) obj).deleted;
    }

    @Override
    public int hashCode() {
        // return Objects.hash(name, nodePair, routes, deleted);
        return Objects.hash(name, nodePair, deleted);
    }

    @Override
    public String toString() {
        return "Edge: {" + getName() + "}";
    }

    @Override
    public void update(Observable o, Object arg) {
        updateEdge();
        Loggers.info(this, "Edge " + getName() + " updated. New direction is " + direction + ".");
    }

}
