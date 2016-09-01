/*
 * Copyright (c) 2016 Thomas Zuberbuehler. All rights reserved.
 */

package ch.geomo.tramaps.map;

import ch.geomo.tramaps.conflict.Conflict;
import ch.geomo.tramaps.conflict.ConflictFinder;
import ch.geomo.tramaps.graph.Edge;
import ch.geomo.tramaps.graph.Graph;
import org.jetbrains.annotations.NotNull;

import java.util.stream.Stream;

public class MetroMap extends Graph {

    /**
     * @return a sorted {@link Stream} of conflict
     */
    @NotNull
    public Stream<Conflict> evaluateConflicts(double routeMargin, double edgeMargin, boolean biggestConflictFirst) {
        return new ConflictFinder(routeMargin, edgeMargin).getConflicts(getEdges(), getNodes()).stream()
                .sorted((c1, c2) -> {
                    Conflict conflict1 = biggestConflictFirst ? c2 : c1;
                    Conflict conflict2 = biggestConflictFirst ? c1 : c2;
                    return conflict1.compareTo(conflict2);
                });
    }

    /**
     * @return a {@link Stream} of non-octilinear edges
     */
    @NotNull
    public Stream<Edge> evaluateNonOctilinearEdges() {
        return getEdges().stream()
                .filter(Edge::isNonOctilinear);
    }

}
