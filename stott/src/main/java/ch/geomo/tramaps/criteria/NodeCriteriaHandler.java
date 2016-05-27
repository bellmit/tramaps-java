package ch.geomo.tramaps.criteria;

import ch.geomo.tramaps.criteria.node.*;
import ch.geomo.tramaps.geom.ImmutableNodePoint;
import ch.geomo.tramaps.geom.NodePoint;
import ch.geomo.tramaps.grid.Grid;
import ch.geomo.tramaps.grid.GridNode;
import sun.rmi.runtime.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

public class NodeCriteriaHandler {

    private final static Logger logger = Logger.getLogger(NodeCriteriaHandler.class.getSimpleName());

    private final List<AbstractNodeCriterion> criteria = new ArrayList<>();

    private Grid grid;

    private int iteration = 0;

    private double lastNodeCriteria;
    private double lowestNodeCriteria;

    public NodeCriteriaHandler(Grid grid, double multiplicator, long gridSpacing) {
        this.grid = grid;
        initCriteria(multiplicator, gridSpacing);
    }

    private void initCriteria(double multiplicator, long gridSpacing) {
        criteria.add(new AngularResolutionCriterion(10));
        criteria.add(new BalancedEdgeLengthCriterion(1));
        criteria.add(new EdgeCrossingCriterion(2));
        criteria.add(new EdgeLengthCriterion(2, multiplicator, gridSpacing));
        criteria.add(new LineStraightnessCriterion(5));
        criteria.add(new OctilinearityCriterion(5));
    }

    /**
     * Returns true if a better result was found.
     */
    public boolean test() {

        double currentNodeCriteria = criteria.parallelStream()
                .mapToDouble(c -> c.calculate(grid.getGraph()))
                .sum();

        lastNodeCriteria = currentNodeCriteria;

        if (iteration == 0 || currentNodeCriteria < lowestNodeCriteria) {
            lowestNodeCriteria = currentNodeCriteria;
            return true;
        }
        return false;

    }

    /**
     * Returns the node criteria value from the most recently test run.
     */
    public double getLastNodeCriteria() {
        return lastNodeCriteria;
    }

    public void runIteration(int radius) {
        iteration++;
        for (GridNode node : grid.getGraph().getNodes()) {
            NodePoint bestPoint = new ImmutableNodePoint(node.getX(), node.getY());
            Set<NodePoint> availablePoints = grid.getAvailablePoints(radius, node);
            for (NodePoint point : availablePoints) {
                node.moveTo(point);
                if (test()) {
                    bestPoint = point;
                }
            }
            node.moveTo(bestPoint);
        }
    }

}
