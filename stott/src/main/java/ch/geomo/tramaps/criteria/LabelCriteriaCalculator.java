package ch.geomo.tramaps.criteria;

import ch.geomo.tramaps.criteria.label.NodeProximityCriterion;
import ch.geomo.tramaps.criteria.label.OcclusionCriteria;
import ch.geomo.tramaps.criteria.label.PositionConsistencyCriterion;
import ch.geomo.tramaps.criteria.label.PositionCriterion;
import ch.geomo.tramaps.graph.Graph;

import java.util.ArrayList;
import java.util.List;

public class LabelCriteriaCalculator {

    private List<LabelCriterion> criteria = new ArrayList<>();

    public LabelCriteriaCalculator() {
        init();
    }

    private void init() {
        addCriterion(new OcclusionCriteria());
        addCriterion(new PositionCriterion());
        addCriterion(new PositionConsistencyCriterion());
        addCriterion(new NodeProximityCriterion());
        addCriterion(new NodeProximityCriterion());
    }

    public void addCriterion(LabelCriterion criterion) {
        criteria.add(criterion);
    }

    public LabelCriteriaCalculationResult calculate(Graph graph) {
        return new LabelCriteriaCalculationResult();
    }

}
