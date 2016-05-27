/*
 * Copyright (c) 2016 by Thomas Zuberbühler
 */

package ch.geomo.tramaps.criteria;

import ch.geomo.tramaps.graph.Graph;

public interface LabelCriterion {
    double calculate(Graph graph);
}
