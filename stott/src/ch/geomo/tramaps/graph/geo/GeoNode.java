/*
 * Copyright (c) 2016 by Thomas Zuberbühler
 */

package ch.geomo.tramaps.graph.geo;

import ch.geomo.tramaps.graph.EdgeOrderComparator;
import ch.geomo.tramaps.graph.Node;
import ch.geomo.tramaps.geom.Point;
import ch.geomo.tramaps.graph.NodeLabel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class GeoNode implements Node<Double, GeoEdge> {

    private String name;

    private Double x;
    private Double y;

    private NodeLabel label;

    private final Set<GeoEdge> edges = new TreeSet<>(new EdgeOrderComparator<>(this));

    /**
     * Default constructor for SuperCSV.
     */
    public GeoNode() {
    }

    public GeoNode(String name, double x, double y) {
        this.name = name;
        this.x = x;
        this.y = y;
    }

    @NotNull
    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Nullable
    @Override
    public NodeLabel getLabel() {
        return label;
    }

    public void setLabel(NodeLabel label) {
        this.label = label;
    }

    @NotNull
    @Override
    public Double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    @NotNull
    @Override
    public Double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    @NotNull
    @Override
    public Set<GeoEdge> getEdges() {
        return edges;
    }

    public void addEdge(@NotNull GeoEdge edge) {
        if (edge.contains(this) && !getEdges().contains(edge)) {
            this.edges.add(edge);
        }
    }

    @Override
    public double calculateDistanceTo(@NotNull Point<Double> point) {
        return Math.sqrt(Math.pow(getX() - point.getX(), 2) + Math.pow(getY() - point.getY(), 2));
    }

    @Override
    public double calculateAngleBetween(@NotNull Point<Double> p1, @NotNull Point<Double> p2) {
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

        if (obj == null || !(obj instanceof GeoNode)) {
            return false;
        }

        GeoNode node = (GeoNode) obj;
        return Objects.equals(getX(), node.getX())
                && Objects.equals(getY(), node.getY())
                && Objects.equals(getName(), node.getName());

    }

    @Override
    public int hashCode() {
        return Objects.hash(getX(), getY(), getName());
    }

}
