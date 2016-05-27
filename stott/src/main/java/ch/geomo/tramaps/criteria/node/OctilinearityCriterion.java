package ch.geomo.tramaps.criteria.node;

import ch.geomo.tramaps.criteria.AbstractNodeCriterion;
import ch.geomo.tramaps.grid.GridEdge;
import ch.geomo.tramaps.grid.GridGraph;

import java.util.Set;

public class OctilinearityCriterion extends AbstractNodeCriterion {

    public OctilinearityCriterion(double weight) {
        super(weight);
    }

    @Override
    public double _calculate(GridGraph graph) {
        final Set<GridEdge> edges = graph.getEdges();
        return edges.parallelStream()
                .mapToDouble(e -> {
                    double dY = e.getNodeA().getY() - e.getNodeA().getY();
                    double dX = e.getNodeA().getX() - e.getNodeB().getX();
                    if (dX == 0) {
                        // TODO how to avoid / by zero?
                        return 0;
                    }
                    return Math.abs(Math.sin(4 * (Math.atan(Math.abs(dY) / Math.abs(dX)))));
                })
                .sum();
    }

}