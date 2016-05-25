package ch.geomo.tramaps.criteria.node;

import ch.geomo.tramaps.criteria.NodeCriterion;
import ch.geomo.tramaps.graph.Graph;
import ch.geomo.tramaps.grid.GridEdge;
import ch.geomo.tramaps.grid.GridNode;

import java.util.Set;

public class BalancedEdgeLengthCriterion implements NodeCriterion {

    private double w;

    public BalancedEdgeLengthCriterion(double weight) {
        this.w = weight;
    }

    @Override
    public double calculate(Set<GridNode> nodes, Set<GridEdge> edges) {
        return w * nodes.stream()
                .filter(n -> n.hasDegreeValueOf(2))
                .mapToDouble(n -> {
                    GridEdge[] e = n.getEdges().toArray(new GridEdge[0]);
                    return Math.abs(Math.abs(e[0].getLength()) - Math.abs(e[1].getLength()));
                })
                .sum();
    }

}