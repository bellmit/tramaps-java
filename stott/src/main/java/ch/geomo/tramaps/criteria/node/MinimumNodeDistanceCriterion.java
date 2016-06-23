/*
 * Copyright (c) 2016 by Thomas Zuberbühler
 */

package ch.geomo.tramaps.criteria.node;

import ch.geomo.tramaps.criteria.AbstractNodeCriterion;
import ch.geomo.tramaps.grid.GridGraph;
import ch.geomo.tramaps.grid.GridNode;
import ch.geomo.tramaps.util.CollectionUtil;

import java.util.Set;

public class MinimumNodeDistanceCriterion extends AbstractNodeCriterion {

    private double minimumNodeDistance;

    public MinimumNodeDistanceCriterion(double weight, double minimumNodeDistance) {
        super(weight, false);
        this.minimumNodeDistance = minimumNodeDistance;
    }

    @Override
    public double _calculate(GridGraph graph) {
        final Set<GridNode> nodes = graph.getNodes();
        return CollectionUtil.makePermutations(nodes, true).parallelStream()
                .mapToDouble(tuple -> tuple.getFirst().calculateDistanceTo(tuple.getSecond()))
                .map(distance -> minimumNodeDistance - distance)
                .map(distance -> (distance < 0) ? 0 : distance)
                .sum();
    }

}