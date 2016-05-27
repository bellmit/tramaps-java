/*
 * Copyright (c) 2016 by Thomas Zuberbühler
 */

package ch.geomo.tramaps.graph;

import ch.geomo.tramaps.geom.NodePoint;
import ch.geomo.tramaps.util.CollectionUtil;
import ch.geomo.tramaps.util.tuple.Tuple;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;

public abstract class AbstractNode<E extends AbstractEdge<? extends AbstractNode>> implements Node<E> {

    private long version = 0;

    private String name;
    private NodeLabel label;

    @SuppressWarnings("unchecked")
    private final Set<E> edges = new TreeSet<>(new EdgeOrderComparator(this));

    private Point point;

    protected AbstractNode() {
    }

    protected AbstractNode(@NotNull String name, double x, double y) {
        this(name, new NodeLabel(""), x, y);
    }

    protected AbstractNode(@NotNull String name, NodeLabel label, double x, double y) {
        this.label = label;
        this.updatePoint(x, y);
        this.name = Objects.toString(name, getX() + "/" + getY());
    }

    private void updatePoint(double x, double y) {
        this.point = new GeometryFactory().createPoint(new Coordinate(x, y));
        this.version++;
    }

    public void updateEdges() {
        edges.forEach(edge -> edge.updateLineString());
    }

    @Override
    public long getVersion() {
        return version;
    }

    @NotNull
    @Override
    public String getName() {
        return name;
    }

    protected void setName(String name) {
        this.name = name;
    }

    @Nullable
    @Override
    public NodeLabel getLabel() {
        return label;
    }

    protected void setLabel(NodeLabel label) {
        this.label = label;
    }

    @NotNull
    @Override
    public Set<E> getEdges() {
        return edges;
    }

    public void addEdge(@NotNull E edge) {
        if (edge.contains(this) && !getEdges().contains(edge)) {
            edges.add(edge);
        }
    }

    @NotNull
    public Point getPoint() {
        return point;
    }

    @NotNull
    @Override
    public Coordinate getCoordinate() {
        return point.getCoordinate();
    }

    public void setPoint(Point point) {
        this.point = point;
    }

    public void setPoint(double x, double y) {
        this.updatePoint(x, y);
    }

    @Override
    public double getX() {
        return point.getX();
    }

    @Override
    public double getY() {
        return point.getY();
    }

    @Override
    public int getDegreeValue() {
        return getEdges().size();
    }

    @Override
    public boolean hasDegreeValueOf(int value) {
        return getDegreeValue() == value;
    }

    @NotNull
    @Override
    public Quadrant getQuadrant(@NotNull NodePoint originPoint) {
        return Quadrant.getQuadrant(this, originPoint);
    }

    @NotNull
    public Set<Tuple<E>> getAdjacentEdgePairs() {
        return Tuple.from(CollectionUtil.makePairs(getEdges(), true, true));
    }

    @Override
    public double calculateDistanceTo(double x, double y) {
        return Math.sqrt(Math.pow(getX() - x, 2) + Math.pow(getY() - y, 2));
    }

    @Override
    public double calculateAngleBetween(@NotNull NodePoint p1, @NotNull NodePoint p2) {
        double angle1 = Math.atan2(p1.getY() - getY(), p1.getX() - getX());
        double angle2 = Math.atan2(p2.getY() - getY(), p2.getX() - getX());
        return angle1 - angle2;
    }

    @Override
    public String toString() {
        return getName();
    }

    @Override
    public boolean equals(Object obj) {

        if (obj == null || !(obj instanceof AbstractNode)) {
            return false;
        }

        AbstractNode node = (AbstractNode) obj;
        return Objects.equals(getX(), node.getX())
                && Objects.equals(getY(), node.getY())
                && Objects.equals(getName(), node.getName());

    }

    @Override
    public int hashCode() {
        return Objects.hash(getX(), getY(), getName());
    }

}
