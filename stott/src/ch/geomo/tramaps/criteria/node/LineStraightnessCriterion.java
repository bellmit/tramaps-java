package ch.geomo.tramaps.criteria.node;

import ch.geomo.tramaps.criteria.NodeCriterion;
import ch.geomo.tramaps.graph.Graph;
import ch.geomo.tramaps.grid.GridEdge;
import ch.geomo.tramaps.grid.GridNode;

import java.util.Set;

public class LineStraightnessCriterion implements NodeCriterion {

    private double w;

    public LineStraightnessCriterion(double weight) {
        this.w = weight;
    }

    @Override
    public double calculate(Set<GridNode> nodes, Set<GridEdge> edges) {
        return w * nodes.stream()
                .mapToDouble(n -> n.getAdjacentEdgePairs().stream()
                        .mapToDouble(p -> {
                            Double angle = p.getRight().calculateAngleTo(p.getLeft());
                            if (angle == null) {
                                // should never reach this line since both edges are adjacent
                                throw new IllegalStateException("Edges are not adjacent to each other!");
                            }
                            return Math.abs(angle);
                        })
                        .sum())
                .sum();
    }

}