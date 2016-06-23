package ch.geomo.tramaps.criteria;

import ch.geomo.tramaps.criteria.node.*;
import ch.geomo.tramaps.util.point.ImmutableNodePoint;
import ch.geomo.tramaps.util.point.NodePoint;
import ch.geomo.tramaps.grid.Grid;
import ch.geomo.tramaps.grid.GridNode;

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

    public NodeCriteriaHandler(Grid grid) {
        this.grid = grid;
        initCriteria();
    }

    private void initCriteria() {
        criteria.add(new OctilinearityCriterion(0.1));
        criteria.add(new MinimumNodeDistanceCriterion(0.2, grid.getSpacing()));
        criteria.add(new EdgeCrossingCriterion(1000));
//        criteria.add(new AngularResolutionCriterion(0.25));
        criteria.add(new EdgeStraightnessCriterion(100));
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
