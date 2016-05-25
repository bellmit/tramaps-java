/*
 * Copyright (c) 2016 by Thomas Zuberbühler
 */

package ch.geomo.tramaps.grid;

import ch.geomo.tramaps.graph.geo.GeoGraph;

public class GridBuilder {

    private Double gridSpace;
    private GeoGraph graph;

    public GridBuilder() {
    }

    public GridBuilder setGridSpace(double gridSpace) {
        this.gridSpace = gridSpace;
        return this;
    }

    public GridBuilder setGraph(GeoGraph graph) {
        this.graph = graph;
        return this;
    }

    public Grid build() {
        Grid grid = new Grid();
        grid.setGridSpace(gridSpace);
        grid.setNodes(graph.getNodes());
        return grid;
    }

}
