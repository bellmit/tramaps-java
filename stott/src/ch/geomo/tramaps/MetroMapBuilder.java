package ch.geomo.tramaps;

import ch.geomo.tramaps.criteria.NodeCriteriaHandler;
import ch.geomo.tramaps.graph.geo.GeoGraph;
import ch.geomo.tramaps.grid.Grid;

import java.util.logging.Level;
import java.util.logging.Logger;

public class MetroMapBuilder {

    private final static Logger logger = Logger.getLogger(MetroMapBuilder.class.getSimpleName());

    private GeoGraph graph;

    private long gridSpacing = 100;
    private double multiplicator = 1;

    private int radius = 2;
    private int minIteration = 1;
    private int maxIteration = 50;

    private NodeCriteriaHandler nodeCriteriaHandler;

    public MetroMapBuilder() {
    }

    public MetroMapBuilder setGraph(GeoGraph graph) {
        this.graph = graph;
        return this;
    }

    public MetroMapBuilder setGridSpacing(long gridSpacing) {
        this.gridSpacing = gridSpacing;
        return this;
    }

    public MetroMapBuilder setRadius(int radius) {
        this.radius = radius;
        return this;
    }

    public MetroMapBuilder setMaxIteration(int maxIteration) {
        this.maxIteration = maxIteration;
        return this;
    }

    public MetroMapBuilder setMinIteration(int minIteration) {
        this.minIteration = minIteration;
        return this;
    }

    public MetroMapBuilder setMultiplicator(double multiplicator) {
        this.multiplicator = multiplicator;
        return this;
    }

    private double getCurrentCriteriaValue() {
        nodeCriteriaHandler.test();
        double lastNodeCriteria = nodeCriteriaHandler.getLastNodeCriteria();
        System.out.println("Last Node Criteria Value: " + lastNodeCriteria);
        return lastNodeCriteria;
    }

    private void run() {
        int iteration = 0;
        double bestCriteriaValue = getCurrentCriteriaValue();
        while (true) {

            iteration++;

            System.out.println("Start Iteration: " + iteration);

            nodeCriteriaHandler.runIteration(radius);

            // test iteration
            double currentCriteriaValue = getCurrentCriteriaValue();
            if (currentCriteriaValue > bestCriteriaValue && iteration > minIteration) {
                break;
            }
            bestCriteriaValue = currentCriteriaValue;

            // abort if max iteration reached
            if (iteration >= maxIteration) {
                break;
            }

        }

    }

    public MetroMap build() {

        logger.log(Level.INFO, "Start building...");
        logger.log(Level.INFO, "Start creating grid...");

        Grid grid = new Grid();
        grid.setGridSpace(gridSpacing);
        grid.setMultiplicator(multiplicator);
        grid.setInitialMoveRadius(radius);
        grid.setNodes(graph.getNodes());

        logger.log(Level.INFO, "Grid created.");

        nodeCriteriaHandler = new NodeCriteriaHandler(grid, multiplicator, gridSpacing);

        logger.log(Level.INFO, "Start hill climbing algorithm.");
        run();

        logger.log(Level.INFO, "Metro Map generated.");
        return new MetroMap(grid.getGraph());

    }

}
