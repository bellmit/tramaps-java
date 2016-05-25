package ch.geomo.tramaps.criteria.node;

import ch.geomo.tramaps.criteria.NodeCriterion;
import ch.geomo.tramaps.grid.GridEdge;
import ch.geomo.tramaps.grid.GridNode;

import java.util.Set;

public class EdgeLengthCriterion implements NodeCriterion {

    private double w;
    private double l;
    private double g;

    public EdgeLengthCriterion(double weight, double multiplicator, double gridSpacing) {
        this.w = weight;
        this.l = multiplicator;
        this.g = gridSpacing;
    }

    @Override
    public double calculate(Set<GridNode> nodes, Set<GridEdge> edges) {
        return w * edges.stream()
                .mapToDouble(e -> Math.abs(Math.abs(e.getLength()) / (l * g) - 1))
                .sum();
    }

}