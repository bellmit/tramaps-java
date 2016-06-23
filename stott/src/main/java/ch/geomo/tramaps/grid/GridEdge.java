/*
 * Copyright (c) 2016 by Thomas Zuberbühler
 */

package ch.geomo.tramaps.grid;

import ch.geomo.tramaps.util.GeomUtil;
import ch.geomo.tramaps.util.point.NodePoint;
import ch.geomo.tramaps.util.tuple.Tuple;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiLineString;
import org.geotools.graph.structure.Node;
import org.geotools.graph.structure.basic.BasicEdge;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.opengis.feature.simple.SimpleFeature;

import java.awt.*;
import java.util.*;
import java.util.stream.Stream;

public class GridEdge extends BasicEdge {

    private long version;
    private String label;
    private Color color;

    public GridEdge(@NotNull GridNode nodeA, @NotNull GridNode nodeB) {
        super(nodeA, nodeB);
        version = 0;
    }

    @Override
    public GridNode getNodeA() {
        return (GridNode) super.getNodeA();
    }

    @Override
    public GridNode getNodeB() {
        return (GridNode) super.getNodeB();
    }

    void updateLineString() {
        if (getObject() != null) {
            getSimpleFeature().setDefaultGeometry(GeomUtil.createLineString(getNodeA(), getNodeB()));
        }
        version++;
    }

    public long getVersion() {
        return version;
    }

    @Nullable
    public String getLabel() {
        return label;
    }

    public void setLabel(@Nullable String label) {
        this.label = label;
    }

    @Nullable
    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public SimpleFeature getSimpleFeature() {
        return (SimpleFeature) getObject();
    }

    @NotNull
    public LineString getLineString() {
        Geometry geometry = (Geometry) getSimpleFeature().getDefaultGeometry();
        if (geometry instanceof MultiLineString) {
            return (LineString) geometry.getGeometryN(0);
        }
        return (LineString) geometry;
    }

    @Override
    public GridNode getOtherNode(Node node) {
        return (GridNode) super.getOtherNode(node);
    }

    @NotNull
    public Optional<Double> calculateAngleTo(@NotNull GridEdge edge) {
        return getSharedNode(edge)
                .map(node -> node.getEdgeAngles().get(Tuple.of(this, edge)));
    }

    public boolean isAdjacent(@NotNull GridEdge edge) {
        return getSharedNode(edge).map(e -> true).orElse(false);
    }

    @NotNull
    public Optional<GridNode> getSharedNode(@NotNull GridEdge edge) {
        return Stream.of(getNodeA(), getNodeB())
                .filter(node -> node.equals(edge.getNodeA()) || node.equals(edge.getNodeB()))
                .findFirst();
    }

    public double getLength() {
        return getLineString().getLength();
    }

    @Contract("null->false")
    public boolean intersects(GridEdge otherEdge) {
        return otherEdge != null && getLineString().intersects(otherEdge.getLineString());
    }

    @Contract("null,_->false")
    public boolean intersectsWithBuffer(GridEdge otherEdge, double bufferDistance) {
        return otherEdge != null && getLineString().buffer(bufferDistance).intersects(otherEdge.getLineString());
    }

    public boolean isLoop() {
        return getNodeA().equals(getNodeB());
    }

    @Override
    public String toString() {
        return Optional.ofNullable(getLabel()).orElse(getLineString().toString());
    }

    @NotNull
    public Stream<GridNode> getNodeStream() {
        return Stream.of(getNodeA(), getNodeB());
    }

    @Override
    public boolean equals(Object obj) {

        if (obj == null || !(obj instanceof GridEdge)) {
             return false;
        }

        GridEdge edge = (GridEdge)obj;
        return Objects.equals(getNodeA(), edge.getNodeA())
                || Objects.equals(getNodeB(), edge.getNodeB())
                || Objects.equals(getLabel(), getLabel());

    }
}

