/*
 * Copyright (c) 2016 Thomas Zuberbuehler. All rights reserved.
 */

package ch.geomo.tramaps.map;

import ch.geomo.tramaps.conflict.Conflict;
import ch.geomo.tramaps.conflict.ConflictFinder;
import ch.geomo.tramaps.graph.Edge;
import ch.geomo.tramaps.graph.Graph;
import ch.geomo.util.CollectionUtil;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.stream.Stream;

public class MetroMap extends Graph {

    private double routeMargin;
    private double edgeMargin;

    private ConflictFinder conflictFinder;

    public MetroMap(double routeMargin, double edgeMargin) {
        super();
        this.routeMargin = routeMargin;
        this.edgeMargin = edgeMargin;
        conflictFinder = new ConflictFinder(routeMargin, edgeMargin);
    }

    public double getRouteMargin() {
        return routeMargin;
    }

    public double getEdgeMargin() {
        return edgeMargin;
    }

    /**
     * @return a sorted {@link List} of conflict
     */
    @NotNull
    public List<Conflict> evaluateConflicts(boolean biggestConflictFirst) {
        List<Conflict> conflicts = conflictFinder.getConflicts(getEdges(), getNodes());
        if (biggestConflictFirst) {
            return CollectionUtil.reverse(conflicts);
        }
        return conflicts;
    }

    /**
     * @return a {@link Stream} of non-octilinear edges
     */
    @NotNull
    public Stream<Edge> evaluateNonOctilinearEdges() {
        return getEdges().stream()
                .filter(Edge::isNotOctilinear);
    }

}
