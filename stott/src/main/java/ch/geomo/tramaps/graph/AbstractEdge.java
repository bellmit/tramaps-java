/*
 * Copyright (c) 2016 by Thomas Zuberbühler
 */

package ch.geomo.tramaps.graph;

import ch.geomo.tramaps.util.tuple.Tuple;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.Optional;

public abstract class AbstractEdge<N extends AbstractNode<? extends AbstractEdge>> implements Edge<N> {

    private long version;

    private String name;
    private Tuple<N> nodes;

    private LineString lineString;

    protected AbstractEdge() {
    }

    protected AbstractEdge(@NotNull String name, @NotNull N firstNode, @NotNull N secondNode) {
        this(name, Tuple.of(firstNode, secondNode));
    }

    protected AbstractEdge(@NotNull String name, @NotNull Tuple<N> nodes) {
        this.nodes = nodes;
        this.name = Objects.toString(name, getNode(0).getName() + "<-->" + getNode(1).getName());
        this.updateLineString();
    }

    protected void updateLineString() {
        this.lineString = new GeometryFactory().createLineString(new Coordinate[]{
                getNode(0).getPoint().getCoordinate(),
                getNode(1).getPoint().getCoordinate()
        });
        this.version++;
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

    public N getNode(int index) {
        return nodes.get(index);
    }

    public N getFirstNode() {
        return getNode(0);
    }

    public N getSecondNode() {
        return getNode(1);
    }

    @NotNull
    public LineString getLineString() {
        return lineString;
    }

    protected void setNodes(Tuple<N> nodes) {
        this.nodes = nodes;
        this.updateLineString();
    }

    @Override
    @Contract("null -> false")
    public boolean contains(@Nullable Node node) {

        return node != null && (node.equals(getNode(0)) || node.equals(getNode(1)));
    }

    @Override
    public Tuple<N> getNodes() {
        return nodes;
    }

    @NotNull
    @Override
    public N getOppositeNode(@NotNull N node) {
        return getNodes().getOtherValue(node);
    }

    @NotNull
    @Override
    public Optional<Double> calculateAngleTo(@NotNull Edge<N> edge) {
        return Optional.ofNullable(getSharedNode(edge))
                .map(node1 -> {
                    N node2 = getOppositeNode(node1);
                    N node3 = edge.getOppositeNode(node1);
                    return node1.calculateAngleBetween(node2, node3);
                });
    }

    @Override
    public boolean isAdjacent(@NotNull Edge<N> edge) {
        return getNodes().hasSharedValue(edge.getNodes());
    }

    @Nullable
    @Override
    public N getSharedNode(@NotNull Edge<N> edge) {
        return getNodes().getSharedValue(edge.getNodes());
    }

    @Override
    public double getLength() {
        return getLineString().getLength();
    }

    @Override
    @Contract("null->false")
    public boolean intersects(Edge<N> otherEdge) {
        return otherEdge != null && getLineString().intersects(otherEdge.getLineString());
    }

    @Contract("null,_->false")
    public boolean intersectsWithBuffer(Edge<N> otherEdge, double bufferDistance) {
        if (otherEdge == null) {
            return false;
        }
        return getLineString().buffer(bufferDistance).intersects(otherEdge.getLineString());
    }

    @Override
    public String toString() {
        return getName();
    }

    @Override
    public boolean equals(Object obj) {

        if (obj == null || !(obj instanceof AbstractEdge)) {
            return false;
        }

        AbstractEdge edge = (AbstractEdge) obj;
        return Objects.equals(edge.getNodes(), getNodes())
                && Objects.equals(edge.getName(), getName());

    }

    @Override
    public int hashCode() {
        return Objects.hash(getNodes(), getName());
    }

}
