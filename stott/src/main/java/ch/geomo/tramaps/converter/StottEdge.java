/*
 * Copyright (c) 2016 by Thomas Zuberbühler
 */

package ch.geomo.tramaps.converter;

import ch.geomo.tramaps.util.CollectionUtil;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;

import java.awt.*;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class StottEdge {

    private String label;
    private List<StottNode> stations;
    private Color color;

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public List<StottNode> getStations() {
        return stations;
    }

    public void setStations(List<StottNode> stations) {
        this.stations = stations;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(String color) {
        Integer[] values = Arrays.stream(color.split(","))
                .map(Integer::parseInt)
                .toArray(Integer[]::new);
        this.color = new Color(values[0], values[1], values[2]);
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public List<LineString> toLineStrings() {

        List<Coordinate> coordinates = stations.stream()
                .map(StottNode::getCoordinate)
                .collect(Collectors.toList());

        return CollectionUtil.makePairs(coordinates, false, true).stream()
                .map(p -> new GeometryFactory().createLineString(new Coordinate[]{p.getLeft(), p.getRight()}))
                .collect(Collectors.toList());

    }
}
