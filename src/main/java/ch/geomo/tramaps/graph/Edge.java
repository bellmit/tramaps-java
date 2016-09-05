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

    private String name;

    private Node nodeA;
    private Node nodeB;
    private Pair<Node> nodePair;

    private Set<Route> routes;

    private LineString lineString;
    private Direction direction;

    private Direction originalDirection;

    private boolean deleted;

    /**
     * Creates a new instance of {@link Edge} with given nodes.
     */
    public Edge(@NotNull Node nodeA, @NotNull Node nodeB) {

        this.nodeA = nodeA;
        this.nodeB = nodeB;
        nodeA.addAdjacentEdge(this);
        nodeB.addAdjacentEdge(this);
        routes = new HashSet<>();
        nodePair = Pair.of(nodeA, nodeB);
        updateEdge();

        // cache original direction of this edge
        originalDirection = AnyDirection.fromAngle(calculateAngle());

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
        return nodeA;
    }

    /**
     * @return the second node of this edge
     */
    @NotNull
    public Node getNodeB() {
        return nodeB;
    }

    /**
     * @return the angle between Y axis and this edge (starting north, clockwise)
     */
    private double calculateAngle() {
        double angle = getGeomUtil().getAngleBetweenAsDegree(nodeA, NodePoint.of(nodeA.getX() + 5d, nodeA.getY()), nodeB);
        angle = (angle + 360) % 360;
//        // we do accept an imprecision of 1 degree
//        if (angle % 45 < 0.5 || angle % 45 > 44.5) {
//            return Math.round(angle / 45) * 45;
//        }
        return angle;
    }

    /**
     * Updates the {@link LineString} representation and notifies Observers.
     */
    protected final void updateEdge() {
        lineString = getGeomUtil().createLineString(nodeA, nodeB);
        double angle = calculateAngle();
        direction = AnyDirection.fromAngle(angle);
        Loggers.info(this, "Edge " + getName() + " updated. New direction is " + direction + ".");
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
     * @throws NoSuchElementException if given node is not an end node of this edge
     */
    @NotNull
    public Node getOtherNode(@NotNull Node node) {
        return nodePair.getOtherValue(node);
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
        updateEdge();
    }

    public boolean isNonOctilinear() {
        return !OctilinearDirection.isOctilinear(direction);
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
    public boolean isDiagonal() {
        return direction.isDiagonal();
    }

    /**
     * @see #getDirection(Node)
     * @see #getOriginalDirection(Node)
     */
    @NotNull
    private Direction getDirection(@Nullable Node node, @NotNull Direction direction) {
        if (node == null || nodeA.equals(node)) {
            return direction;
        }
        else if (nodeB.equals(node)) {
            return direction.opposite();
        }
        String message = "Node " + node.getName() + " must be equals to an end node of " + getName() + ".";
        throw new IllegalArgumentException(message);
    }

    /**
     * @return the <b>current</b> direction of this edge from <b>node A or B depending on the node</b>,
     * returns the current direction of node A if null is passed
     * @throws IllegalArgumentException if given node is neither equal to node A nor node B
     */
    @NotNull
    public Direction getDirection(@Nullable Node node) {
        return getDirection(node, direction);
    }

    /**
     * @return the <b>original</b> direction of this edge from <b>node A or B depending on the node</b>,
     * returns the original direction of node A if null is passed
     * @throws IllegalArgumentException if given node is neither equal to node A nor node B
     */
    @NotNull
    public Direction getOriginalDirection(@Nullable Node node) {
        return getDirection(node, originalDirection);
    }

    @Contract(pure = true)
    public boolean hasDirectionOf(@NotNull Node startNode, @NotNull Direction direction) {
        return direction.getAngle() == getDirection(startNode).getAngle();
    }

    @Contract(pure = true)
    public boolean hasOppositeDirectionOf(@NotNull Node startNode, @NotNull Direction direction) {
        return direction.isOpposite(getDirection(startNode));
    }

    /**
     * @return true since this implementation of {@link GraphElement} is an edge ;-)
     */
    @Override
    @Contract("->true")
    public boolean isEdge() {
        return true;
    }

    /**
     * @return false since this implementation of {@link GraphElement} is an edge ;-)
     */
    @Override
    @Contract("->false")
    public boolean isNode() {
        return false;
    }

    @Contract("null->true")
    public boolean isNotEquals(@Nullable Edge edge) {
        return !equals(edge);
    }

    /**
     * @return true if given edge has the same nodes than this instance
     */
    public boolean equalNodes(Edge edge) {
        return getNodeA().isAdjacent(edge) && getNodeB().isAdjacent(edge);
    }

    public boolean isDeleted() {
        return deleted;
    }

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

    @Override
    public boolean equals(Object obj) {
        // TODO compare routes as well
        return obj instanceof Edge
                && Objects.equals(name, ((Edge) obj).name)
                && nodeA.equals(((Edge) obj).nodeA)
                && nodeB.equals(((Edge) obj).nodeB)
                && deleted == ((Edge) obj).deleted;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, nodeA, nodeB, deleted);
    }

    @Override
    public String toString() {
        return "Edge: {" + getName() + "}";
    }

    @Contract(pure = true)
    public boolean hasRoutes() {
        return !routes.isEmpty();
    }

    public double getLength() {
        return lineString.getLength();
    }

}
