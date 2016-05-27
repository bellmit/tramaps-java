/*
 * Copyright (c) 2016 by Thomas Zuberbühler
 */

package ch.geomo.tramaps.criteria;

import ch.geomo.tramaps.grid.GridGraph;

public interface LabelCriterion {
    double calculate(GridGraph graph);
}
