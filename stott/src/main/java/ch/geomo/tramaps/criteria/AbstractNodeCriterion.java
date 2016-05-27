package ch.geomo.tramaps.criteria;

import ch.geomo.tramaps.grid.GridGraph;

import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class AbstractNodeCriterion {

    private final Logger logger = Logger.getLogger(this.getClass().getSimpleName());

    private boolean loggingEnabled = false;

    private double weight;
    private double lastResult;

    public AbstractNodeCriterion(double weight) {
        this.weight = weight;
    }

    public AbstractNodeCriterion(double weight, boolean loggingEnabled) {
        this.weight = weight;
        this.loggingEnabled = loggingEnabled;
    }

    public double getLastResult() {
        return lastResult;
    }

    private void updateLastResult(double result) {
        if (result != getLastResult()) {
            if (loggingEnabled) {
                logger.log(Level.INFO, "Result: {0}", result);
            }
            lastResult = result;
        }
    }

    public double getWeight() {
        return weight;
    }

    public double calculate(GridGraph graph) {
        double result = _calculate(graph);
        updateLastResult(result);
        return getWeight() * result;
    }

    protected abstract double _calculate(GridGraph graph);

}
