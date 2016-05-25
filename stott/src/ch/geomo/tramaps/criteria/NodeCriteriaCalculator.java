package ch.geomo.tramaps.criteria;

import ch.geomo.tramaps.criteria.node.*;
import ch.geomo.tramaps.graph.Graph;
import ch.geomo.tramaps.grid.GridEdge;
import ch.geomo.tramaps.grid.GridNode;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class NodeCriteriaCalculator implements CriteriaCalculator<NodeCriteriaCalculationResult> {

    private List<NodeCriterion> criteria = new ArrayList<>();

    public NodeCriteriaCalculator(double multiplicator, double gridSpacing) {
        init(multiplicator, gridSpacing);
    }

    private void init(double multiplicator, double gridSpacing) {
        addCriterion(new AngularResolutionCriterion(1));
        addCriterion(new BalancedEdgeLengthCriterion(1));
        addCriterion(new EdgeCrossingCriterion(1));
        addCriterion(new EdgeLengthCriterion(1, multiplicator, gridSpacing));
        addCriterion(new LineStraightnessCriterion(5));
        addCriterion(new OctilinearityCriterion(10));
    }

    public void addCriterion(NodeCriterion criterion) {
        criteria.add(criterion);
    }

    public NodeCriteriaCalculationResult calculate(Set<GridNode> nodes, Set<GridEdge> edges) {
        NodeCriteriaCalculationResult result = new NodeCriteriaCalculationResult();
        double total = criteria.stream()
                .mapToDouble(c -> c.calculate(nodes, edges))
                .peek(result::appendResult)
                .sum();
        result.setTotal(total);
        return result;
    }

}
