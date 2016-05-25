package ch.geomo.tramaps.criteria.node;

import ch.geomo.tramaps.criteria.NodeCriterion;
import ch.geomo.tramaps.grid.GridEdge;
import ch.geomo.tramaps.grid.GridNode;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Set;

public class AngularResolutionCriterion implements NodeCriterion {

    private double w;

    public AngularResolutionCriterion(double weight) {
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
                            int degreeValue = n.getDegreeValue();
                            return Math.abs(((2 * Math.PI) / degreeValue) - angle);
                        })
                        .sum())
                .sum();
    }

}
