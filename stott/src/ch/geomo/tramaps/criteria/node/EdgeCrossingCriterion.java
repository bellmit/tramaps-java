package ch.geomo.tramaps.criteria.node;

import ch.geomo.tramaps.criteria.NodeCriterion;
import ch.geomo.tramaps.grid.GridEdge;
import ch.geomo.tramaps.grid.GridNode;

import java.util.Set;

public class EdgeCrossingCriterion implements NodeCriterion {

    private double w;

    public EdgeCrossingCriterion(double weight) {
        this.w = weight;
    }

    @Override
    public double calculate(Set<GridNode> nodes, Set<GridEdge> edges) {
        // TODO calculate number of edge crossing
        return w * 0;
    }

}