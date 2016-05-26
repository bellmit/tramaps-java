package ch.geomo.tramaps.criteria.node;

import ch.geomo.tramaps.criteria.AbstractNodeCriterion;
import ch.geomo.tramaps.grid.GridEdge;
import ch.geomo.tramaps.grid.GridGraph;

import java.util.Set;

public class EdgeLengthCriterion extends AbstractNodeCriterion {

    private double l;
    private long g;

    public EdgeLengthCriterion(double weight, double multiplicator, long gridSpacing) {
        super(weight);
        this.l = multiplicator;
        this.g = gridSpacing;
    }

    @Override
    public double _calculate(GridGraph graph) {
        final Set<GridEdge> edges = graph.getEdges();
        return edges.parallelStream()
                .mapToDouble(e -> Math.abs(Math.abs(e.getLength()) / (l * g) - 1))
                .sum();
    }

}