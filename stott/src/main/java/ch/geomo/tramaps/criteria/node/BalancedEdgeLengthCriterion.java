package ch.geomo.tramaps.criteria.node;

import ch.geomo.tramaps.criteria.AbstractNodeCriterion;
import ch.geomo.tramaps.grid.GridEdge;
import ch.geomo.tramaps.grid.GridGraph;
import ch.geomo.tramaps.grid.GridNode;

import java.util.Set;

public class BalancedEdgeLengthCriterion extends AbstractNodeCriterion {

    public BalancedEdgeLengthCriterion(double weight) {
        super(weight);
    }

    @Override
    public double _calculate(GridGraph graph) {
        final Set<GridNode> nodes = graph.getNodes();
        return nodes.parallelStream()
                .filter(n -> n.hasDegreeValueOf(2))
                .mapToDouble(n -> {
                    GridEdge[] e = n.getEdges().toArray(new GridEdge[0]);
                    return Math.abs(Math.abs(e[0].getLength()) - Math.abs(e[1].getLength()));
                })
                .sum();
    }

}