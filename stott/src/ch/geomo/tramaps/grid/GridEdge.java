/*
 * Copyright (c) 2016 by Thomas Zuberbühler
 */

package ch.geomo.tramaps.grid;

import ch.geomo.tramaps.graph.Edge;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;

public class GridEdge implements Edge<Long, GridNode> {

    private String name;

    private GridNode start;
    private GridNode end;

    public GridEdge(@NotNull String name, @NotNull GridNode start, @NotNull GridNode end) {
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

    public void setStart(@NotNull GridNode start) {
        this.start = start;
    }

    public void setEnd(@NotNull GridNode end) {
        this.end = end;
    }

    @NotNull
    @Override
    public Pair<GridNode, GridNode> getNodes() {
        return Pair.of(start, end);
    }

    @Override
    public double getLength() {
        return start.calculateDistanceTo(end);
    }

    @Override
    public String toString() {
        return "{" + getName() + "|" + getStart() + "--" + getEnd() + "}";
    }

}
