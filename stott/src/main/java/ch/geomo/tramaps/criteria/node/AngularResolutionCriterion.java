package ch.geomo.tramaps.criteria.node;

import ch.geomo.tramaps.criteria.AbstractNodeCriterion;
import ch.geomo.tramaps.grid.GridGraph;
import ch.geomo.tramaps.grid.GridNode;

import java.util.Set;

public class AngularResolutionCriterion extends AbstractNodeCriterion {

    public AngularResolutionCriterion(double weight) {
        super(weight);
    }

    @Override
    public double _calculate(GridGraph graph) {
        final Set<GridNode> nodes = graph.getNodes();
        return nodes.parallelStream()
                .mapToDouble(n -> n.getAdjacentEdgePairs().parallelStream()
                        .mapToDouble(p ->
                            p.get(0).calculateAngleTo(p.get(1))
                                    .map(angle -> Math.abs(((2 * Math.PI) / n.getDegree()) - angle))
                                    .orElse(0d)
                        )
                        .sum())
                .sum();
    }

}
