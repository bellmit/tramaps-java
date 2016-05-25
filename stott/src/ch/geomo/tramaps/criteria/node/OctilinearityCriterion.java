package ch.geomo.tramaps.criteria.node;

import ch.geomo.tramaps.criteria.NodeCriterion;
import ch.geomo.tramaps.graph.Graph;
import ch.geomo.tramaps.grid.GridEdge;
import ch.geomo.tramaps.grid.GridNode;

import java.util.Set;

public class OctilinearityCriterion implements NodeCriterion {

    private double w;

    public OctilinearityCriterion(double weight) {
        this.w = weight;
    }

    @Override
    public double calculate(Set<GridNode> nodes, Set<GridEdge> edges) {
        return w * edges.stream()
                .mapToDouble(e -> {
                    long dY = e.getStart().getY() - e.getEnd().getY();
                    long dX = e.getStart().getX() - e.getEnd().getX();
                    if (dX == 0) {
                        // TODO how to avoid / by zero?
                        return 0;
                    }
                    return Math.abs(Math.sin(4 * (Math.atan(Math.abs(dY) / Math.abs(dX)))));
                })
                .sum();
    }

}