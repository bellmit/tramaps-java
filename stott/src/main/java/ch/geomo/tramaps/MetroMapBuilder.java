package ch.geomo.tramaps;

import ch.geomo.tramaps.criteria.NodeCriteriaHandler;
import ch.geomo.tramaps.grid.Grid;
import ch.geomo.tramaps.grid.GridGraph;
import org.opengis.geometry.BoundingBox;

import java.util.Observable;
import java.util.Observer;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MetroMapBuilder extends Observable {

    private final static Logger logger = Logger.getLogger(MetroMapBuilder.class.getSimpleName());

    private GridGraph graph;

    private long gridSpacing = 100;
    private double multiplicator = 1;

    private int radius = 2;
    private int minIteration = 1;
    private int maxIteration = 10;

    private NodeCriteriaHandler nodeCriteriaHandler;

    private BoundingBox drawingArea;

    public MetroMapBuilder() {
    }

    public MetroMapBuilder setGraph(GridGraph graph) {
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

    public MetroMapBuilder setDrawingArea(BoundingBox drawingArea) {
        this.drawingArea = drawingArea;
        return this;
    }

    private double getCurrentCriteriaValue() {
        nodeCriteriaHandler.test();
        double lastNodeCriteria = nodeCriteriaHandler.getLastNodeCriteria();
        System.out.println("Last INode Criteria Value: " + lastNodeCriteria);
        return lastNodeCriteria;
    }

    private void run() {
        int iteration = 0;
        double bestCriteriaValue = getCurrentCriteriaValue();
        while (true) {

            iteration++;

            System.out.println("Start Iteration: " + iteration);

            nodeCriteriaHandler.runIteration(radius);
            notifyObservers();

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

    public synchronized MetroMapBuilder addBuildObserver(Observer o) {
        super.addObserver(o);
        return this;
    }

    public Grid build() {

        logger.log(Level.INFO, "Start building...");
        logger.log(Level.INFO, "Start creating grid...");

        Grid grid = new Grid(graph);

        grid.setGridSpacing(gridSpacing);
        grid.setMultiplicator(multiplicator);
        grid.setInitialMoveRadius(radius);
        grid.setDrawingArea(drawingArea);

        grid.snapNodes();

        logger.log(Level.INFO, "Grid created.");

        nodeCriteriaHandler = new NodeCriteriaHandler(grid, multiplicator, gridSpacing);

        logger.log(Level.INFO, "Start hill climbing algorithm.");
        run();

        logger.log(Level.INFO, "Metro Map generated.");
        return grid;

    }

}
