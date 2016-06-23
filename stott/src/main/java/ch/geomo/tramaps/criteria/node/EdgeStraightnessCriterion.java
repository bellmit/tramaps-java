/*
 * Copyright (c) 2016 by Thomas Zuberbühler
 */

package ch.geomo.tramaps.criteria.node;

import ch.geomo.tramaps.criteria.AbstractNodeCriterion;
import ch.geomo.tramaps.grid.GridGraph;
import ch.geomo.tramaps.grid.GridNode;
import ch.geomo.tramaps.util.CollectionUtil;

import java.util.Set;

public class EdgeStraightnessCriterion extends AbstractNodeCriterion {

    public EdgeStraightnessCriterion(double weight) {
        super(weight, true);
    }

    @Override
    public double _calculate(GridGraph graph) {
        final Set<GridNode> nodes = graph.getNodes();
        return nodes.parallelStream()
                .filter(node -> node.hasDegree(2))
                .mapToDouble(node -> node.getEdgeAngles().values().stream().findFirst().orElse(Math.PI))
                .map(angle -> Math.abs(Math.PI - angle))
                .sum();
    }

}