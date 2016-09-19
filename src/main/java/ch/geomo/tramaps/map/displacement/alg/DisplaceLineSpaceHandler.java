/*
 * Copyright (c) 2016 Thomas Zuberbuehler. All rights reserved.
 */

package ch.geomo.tramaps.map.displacement.alg;

import ch.geomo.tramaps.conflict.Conflict;
import ch.geomo.tramaps.conflict.OctilinearConflict;
import ch.geomo.tramaps.graph.Edge;
import ch.geomo.tramaps.map.MetroMap;
import ch.geomo.tramaps.map.displacement.LineSpaceHandler;
import ch.geomo.tramaps.map.displacement.alg.adjustment.EdgeAdjuster;
import ch.geomo.util.collection.list.EnhancedList;
import ch.geomo.util.doc.HelperMethod;
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

    /**
     * Iterates over all non-octilinear edges and corrects them.
     */
    private void correctNonOctilinearEdges() {
        Loggers.info(this, "Non-Octilinear edges: " + map.countNonOctilinearEdges());
        map.getEdges().stream()
                .filter(Edge::isNotOctilinear)
                .forEach(edge -> EdgeAdjuster.correctEdge(map, edge));
    }

    private void makeSpace(int lastIteration, @Nullable Conflict lastConflict) {

        int currentIteration = lastIteration + 1;

        EnhancedList<Conflict> conflicts = map.evaluateConflicts(true);

        Loggers.separator(this);
        Loggers.info(this, "Start iteration: {0}", currentIteration);

        if (!conflicts.isEmpty()) {

            Loggers.warning(this, "Conflicts found: {0}", conflicts.size());

            Conflict conflict = conflicts.get(0);
            if (lastConflict != null
                    && conflicts.size() > 1
                    && conflict.getBufferA().getElement().equals(lastConflict.getBufferA().getElement())
                    && conflict.getBufferB().getElement().equals(lastConflict.getBufferB().getElement())) {

                // skip conflict to give another conflict a chance to be solved
                Loggers.warning(this, "Skip conflict for one iteration... Take next one.");
                conflict = conflicts.get(1);

            }

            Loggers.info(this, "Handle conflict: {0}", conflict);
            NodeDisplacer.displace(map, conflict);

            Loggers.warning(this, "Uncorrected non-octilinear edges found: {0}", map.countNonOctilinearEdges());

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
    @HelperMethod
    private String getBoundingBoxString() {
        Envelope mapBoundingBox = map.getBoundingBox();
        return "Size: " + (int) Math.ceil(mapBoundingBox.getWidth()) + "x" + (int) Math.ceil(mapBoundingBox.getHeight());
    }

    /**
     * Solves non-octilinear edges by solving {@link OctilinearConflict}.
     */
    private void postOctilinearConflictSolver() {
        EnhancedList<Conflict> octilinearConflicts = map.evaluateOctilinearConflicts(1, false);
        if (!octilinearConflicts.isEmpty()) {
            Loggers.info(this, "Solve octilinear conflict: " + octilinearConflicts.get(0));
            NodeDisplacer.displace(map, octilinearConflicts.get(0));
            postOctilinearConflictSolver();
        }
    }

    /**
     * Makes space between edge and nodes if necessary.
     */
    @Override
    public void makeSpace() {

        Loggers.separator(this);
        Loggers.info(this, "Start TRAMAPS algorithm");

        makeSpace(0, null);

        Loggers.info(this, "Restore octilinearity...");
        correctNonOctilinearEdges();
        postOctilinearConflictSolver();

        Loggers.separator(this);
        Loggers.info(this, getBoundingBoxString());
        map.evaluateConflicts(true)
                .doIfNotEmpty(list -> Loggers.warning(this, "Remaining conflicts found!"))
                .forEach(conflict -> Loggers.warning(this, "- {0}", conflict));
        Loggers.separator(this);

    }

}
