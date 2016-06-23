package ch.geomo.tramaps;

import ch.geomo.tramaps.criteria.NodeCriteriaHandler;
import ch.geomo.tramaps.grid.Grid;
import ch.geomo.tramaps.grid.GridGraph;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MetroMapBuilder extends Observable {

    private final static Logger logger = Logger.getLogger(MetroMapBuilder.class.getSimpleName());

    private GridGraph graph;

    private long spacing = 10;
    private int maxIteration = 12;
    private int initialSearchDistance = 16;

    private List<Integer> searchDistanceValues;
    private NodeCriteriaHandler nodeCriteriaHandler;

    public MetroMapBuilder() {
    }

    /**
     * Sets the grid spacing. If not set, default of 10 is used.
     */
    public MetroMapBuilder setSpacing(long spacing) {
        this.spacing = spacing;
        return this;
    }

    /**
     * Sets the initial search distance. If not set, default of 16 is used.
     */
    public MetroMapBuilder setInitialSearchDistance(int initialSearchDistance) {
        this.initialSearchDistance = initialSearchDistance;
        return this;
    }

    /**
     * Sets the max iteration. If not set, default of 12 is used.
     */
    public MetroMapBuilder setMaxIteration(int maxIteration) {
        this.maxIteration = maxIteration;
        return this;
    }

    /**
     * Sets the grid graph.
     */
    public MetroMapBuilder setGraph(GridGraph graph) {
        this.graph = graph;
        return this;
    }

    private double getCurrentCriteriaValue() {
        nodeCriteriaHandler.test();
        double lastNodeCriteria = nodeCriteriaHandler.getLastNodeCriteria();
        System.out.println("Last INode Criteria Value: " + lastNodeCriteria);
        return lastNodeCriteria;
    }

    private void run(Grid grid) {

        int iteration = 0;
        double bestCriteriaValue = getCurrentCriteriaValue();
        while (true) {

            iteration++;

            System.out.println("Start Iteration: " + iteration);

            nodeCriteriaHandler.runIteration(searchDistanceValues.get(iteration - 1));
            notifyObservers();

            // test iteration
            double currentCriteriaValue = getCurrentCriteriaValue();
            if (currentCriteriaValue > bestCriteriaValue) {
                break;
            }
            bestCriteriaValue = currentCriteriaValue;

            // abort if max iteration reached
            if (iteration >= maxIteration) {
                break;
            }

        }
    }

    private List<Integer> createSearchDistanceValues() {
        List<Integer> values = new ArrayList<>();
        double step = (double) initialSearchDistance / maxIteration;
        values.add(initialSearchDistance);
        for (int i = 1; (initialSearchDistance - step * i) >= 1; i++) {
            values.add((int) (initialSearchDistance - step * i));
        }
        return values;
    }

    private Grid initGrid() {
        Grid grid = new Grid(graph);
        grid.setSpacing(spacing);
        grid.setMaxIteration(maxIteration);
        grid.setInitialSearchDistance(initialSearchDistance);
        grid.simplifyLines();
        grid.createClusters();
        grid.snapNodes();
        return grid;
    }

    public Grid build() {

        logger.log(Level.INFO, "Start building...");

        logger.log(Level.INFO, "Start creating grid...");
        Grid grid = initGrid();
        logger.log(Level.INFO, "Grid created.");

        logger.log(Level.INFO, "Prepare hill climbing algorithm.");
        searchDistanceValues = createSearchDistanceValues();
        nodeCriteriaHandler = new NodeCriteriaHandler(grid);

        logger.log(Level.INFO, "Start hill climbing algorithm.");
        run(grid);
        logger.log(Level.INFO, "Metro Map generated.");

        return grid;

    }

}
