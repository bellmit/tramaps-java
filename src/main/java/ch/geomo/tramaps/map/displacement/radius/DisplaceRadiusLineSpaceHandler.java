/*
 * Copyright (c) 2016 Thomas Zuberbuehler. All rights reserved.
 */

package ch.geomo.tramaps.map.displacement.radius;

import ch.geomo.tramaps.conflict.Conflict;
import ch.geomo.tramaps.graph.Edge;
import ch.geomo.tramaps.map.MetroMap;
import ch.geomo.tramaps.map.displacement.LineSpaceHandler;
import ch.geomo.tramaps.map.displacement.alg.EdgeAdjuster;
import ch.geomo.util.logging.Loggers;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class DisplaceRadiusLineSpaceHandler implements LineSpaceHandler {

    private static final int MAX_ITERATIONS = 10;

    private final MetroMap map;

    public DisplaceRadiusLineSpaceHandler(@NotNull MetroMap map) {
        this.map = map;
    }

    private void makeSpace(int lastIteration, @Nullable Conflict lastConflict) {

        int currentIteration = lastIteration + 1;

        List<Conflict> conflicts = map.evaluateConflicts(true);

        Loggers.separator(this);
        Loggers.info(this, "Iteration: " + currentIteration);

        if (!conflicts.isEmpty()) {

            Loggers.warning(this, "Conflicts found: " + conflicts.size());

            Conflict conflict = conflicts.get(0);

            Loggers.info(this, "Handle conflict: " + conflict);
            new RadiusDisplacer(map, conflict).displace();

            // repeat as long as max iteration is not reached
            if (currentIteration < MAX_ITERATIONS) {
                makeSpace(currentIteration, conflict);
            }

        }
        else {
            Loggers.info(this, "No (more) conflicts found.");
        }

    }

    @Override
    public void makeSpace() {
        Loggers.separator(this);
        Loggers.info(this, "Start DisplaceLineSpaceHandler algorithm");
        makeSpace(0, null);
        map.getEdges().stream()
                .filter(Edge::isNotOctilinear)
                .map(edge -> new EdgeAdjuster(map, edge))
                .forEach(EdgeAdjuster::correctEdge);
        map.evaluateConflicts(true)
                .forEach(conflict -> Loggers.warning(this, "Conflict " + conflict + " not solved!"));
    }

}
