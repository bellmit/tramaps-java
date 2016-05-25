/*
 * Copyright (c) 2016 by Thomas Zuberbühler
 */

package ch.geomo.tramaps.graph.geo;

import ch.geomo.tramaps.graph.Edge;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;

public class GeoEdge implements Edge<Double, GeoNode> {

    private String name;

    private GeoNode start;
    private GeoNode end;

    /**
     * Default constructor for SuperCSV.
     */
    public GeoEdge() {
    }

    public GeoEdge(@NotNull String name, @NotNull GeoNode start, @NotNull GeoNode end) {
        this.name = name;
        this.start = start;
        this.end = end;
    }

    @NotNull
    public String getName() {
        return name;
    }

    public void setName(@NotNull String name) {
        this.name = name;
    }

    public void setStart(@NotNull GeoNode start) {
        this.start = start;
    }

    public void setEnd(@NotNull GeoNode end) {
        this.end = end;
    }

    @NotNull
    @Override
    public Pair<GeoNode, GeoNode> getNodes() {
        return Pair.of(start, end);
    }

    @Override
    public double getLength() {
        return start.calculateDistanceTo(end);
    }

}
