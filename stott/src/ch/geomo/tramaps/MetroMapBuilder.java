package ch.geomo.tramaps;

import ch.geomo.tramaps.criteria.LabelCriteriaCalculator;
import ch.geomo.tramaps.criteria.NodeCriteriaCalculator;
import ch.geomo.tramaps.graph.geo.GeoGraph;
import ch.geomo.tramaps.grid.Grid;
import ch.geomo.tramaps.grid.GridBuilder;

public class MetroMapBuilder {

    private NodeCriteriaCalculator nodeCriteriaCalculator;
    private LabelCriteriaCalculator labelCriteriaCalculator;

    private GeoGraph graph;
    private long gridSpacing;
    private double multiplicator;

    public MetroMapBuilder() {
        init();
    }

    private void init() {
        nodeCriteriaCalculator = new NodeCriteriaCalculator(multiplicator, gridSpacing);
        labelCriteriaCalculator = new LabelCriteriaCalculator();
    }

    public MetroMapBuilder setGraph(GeoGraph graph) {
        this.graph = graph;
        return this;
    }

    public MetroMapBuilder setGridSpacing(long gridSpacing) {
        this.gridSpacing = gridSpacing;
        return this;
    }

    public MetroMap build() {
        Grid grid = new GridBuilder()
                .setGridSpace(gridSpacing)
                .setGraph(graph)
                .build();
        return new MetroMap(grid.getGraph());
    }

}
