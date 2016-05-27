package ch.geomo.tramaps.criteria;

import ch.geomo.tramaps.grid.GridGraph;

import java.util.ArrayList;
import java.util.List;

public class LabelCriteriaCalculator {

    private List<LabelCriterion> criteria = new ArrayList<>();

    public LabelCriteriaCalculator() {
        init();
    }

    private void init() {
    }

    public void addCriterion(LabelCriterion criterion) {
        criteria.add(criterion);
    }

    public LabelCriteriaCalculationResult calculate(GridGraph graph) {
        return new LabelCriteriaCalculationResult();
    }

}
