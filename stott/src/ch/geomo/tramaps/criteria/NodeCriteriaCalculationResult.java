package ch.geomo.tramaps.criteria;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class NodeCriteriaCalculationResult implements CriteriaCalculationResult {

    private double total;
    private List<Double> results = new ArrayList<>();

    public void setTotal(double total) {
        this.total = total;
    }

    public void appendResult(double result) {
        results.add(result);
    }

    public double getTotal() {
        return total;
    }

    public double getLowestResult() {
        return results.stream().min(Comparator.naturalOrder()).orElse(0d);
    }

    @Override
    public String toString() {
        return "Total: " + total + ", Lowest Result: " + getLowestResult();
    }

}
