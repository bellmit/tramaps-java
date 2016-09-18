/*
 * Copyright (c) 2016 Thomas Zuberbuehler. All rights reserved.
 */

package ch.geomo.tramaps.map.displacement.alg;

import ch.geomo.tramaps.conflict.Conflict;
import ch.geomo.tramaps.graph.Edge;
import ch.geomo.tramaps.map.MetroMap;
import ch.geomo.tramaps.map.displacement.LineSpaceHandler;
import ch.geomo.util.collection.list.EnhancedList;
import ch.geomo.util.logging.Loggers;
import com.vividsolutions.jts.geom.Envelope;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class DisplaceLineSpaceHandler implements LineSpaceHandler {

    private static final int MAX_ITERATIONS = 150;

    private final MetroMap map;

    public DisplaceLineSpaceHandler(@NotNull MetroMap map) {
        this.map = map;
    }

    private void correctNonOctilinearEdges(@NotNull NodeDisplaceResult displaceResult) {
        Loggers.info(this, "Non-Octilinear edges: " + map.countNonOctilinearEdges());
        map.getEdges().stream()
                .filter(Edge::isNotOctilinear)
                .map(edge -> new EdgeAdjuster(map, edge, displaceResult))
                .forEach(EdgeAdjuster::correctEdge);
    }

    private void makeSpace(int lastIteration, @Nullable Conflict lastConflict) {

        int currentIteration = lastIteration + 1;

        EnhancedList<Conflict> conflicts = map.evaluateConflicts(true);

        Loggers.separator(this);
        Loggers.info(this, "Iteration: " + currentIteration);

        if (!conflicts.isEmpty()) {

            Loggers.warning(this, "Conflicts found: " + conflicts.size());

            Conflict conflict = conflicts.get(0);
            if (lastConflict != null
                    && conflicts.size() > 1
                    && conflict.getBufferA().getElement().equals(lastConflict.getBufferA().getElement())
                    && conflict.getBufferB().getElement().equals(lastConflict.getBufferB().getElement())) {

                // skip conflict to give another conflict a chance to be solved
                Loggers.warning(this, "Skip conflict for one iteration... Take next one.");
                conflict = conflicts.get(1);

            }

            Loggers.info(this, "Handle conflict: " + conflict);
            NodeDisplacer displacer = new NodeDisplacer(map, conflict, conflicts);
            NodeDisplaceResult displaceResult = displacer.displace();

            correctNonOctilinearEdges(displaceResult);

            Loggers.warning(this, "Uncorrected non-octilinear edges found: " + map.getEdges().stream()
                    .filter(Edge::isNotOctilinear)
                    .count());

            // repeat as long as max iteration is not reached
            if (currentIteration < MAX_ITERATIONS) {
                makeSpace(currentIteration, conflict);
            }
            else {
                Loggers.separator(this);
                Loggers.warning(this, "Max number set iteration reached. Stop algorithm.");
            }

        }
        else {
            Loggers.separator(this);
            Loggers.info(this, "No (more) conflicts found.");
        }

    }

    @NotNull
    private String getBoundingBoxString() {
        Envelope mapBoundingBox = map.getBoundingBox();
        return "Size: " + (int) Math.ceil(mapBoundingBox.getWidth()) + "x" + (int) Math.ceil(mapBoundingBox.getHeight());
    }

    private void postOctilinearConflictSolver() {
        EnhancedList<Conflict> octilinearConflicts = map.evaluateOctilinearConflicts(1, false);
        if (!octilinearConflicts.isEmpty()) {
            Loggers.info(this, "Solve octilinear conflict: " + octilinearConflicts.get(0));
            new NodeDisplacer(map, octilinearConflicts.get(0), octilinearConflicts).displace();
            postOctilinearConflictSolver();
        }
    }

    @Override
    public void makeSpace() {

        Loggers.separator(this);
        Loggers.info(this, "Start DisplaceLineSpaceHandler algorithm");
        makeSpace(0, null);
        postOctilinearConflictSolver();

        Loggers.separator(this);
        Loggers.info(this, getBoundingBoxString());
        map.evaluateConflicts(true)
                .forEach(conflict -> Loggers.warning(this, "Conflict " + conflict + " not solved!"));
        Loggers.separator(this);

    }

}
