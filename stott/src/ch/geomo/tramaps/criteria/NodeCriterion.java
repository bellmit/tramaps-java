package ch.geomo.tramaps.criteria;

import ch.geomo.tramaps.grid.GridEdge;
import ch.geomo.tramaps.grid.GridNode;

import java.util.Set;

public interface NodeCriterion {
    double calculate(Set<GridNode> nodes, Set<GridEdge> edges);
}
