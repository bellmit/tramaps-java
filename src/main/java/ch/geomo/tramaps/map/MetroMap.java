/*
 * Copyright (c) 2016 Thomas Zuberbuehler. All rights reserved.
 */

package ch.geomo.tramaps.map;

import ch.geomo.tramaps.conflict.Conflict;
import ch.geomo.tramaps.conflict.ConflictFinder;
import ch.geomo.tramaps.graph.Edge;
import ch.geomo.tramaps.graph.Graph;
import ch.geomo.tramaps.graph.Node;
import ch.geomo.tramaps.map.signature.BendNodeSignature;
import ch.geomo.util.collection.EnhancedList;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.stream.Stream;

public class MetroMap extends Graph {

    private int bendCount = 0;
    private int crossingCount = 0;
    private int junctionCount = 0;

    private double routeMargin;
    private double edgeMargin;

    private ConflictFinder conflictFinder;

    public MetroMap(double routeMargin, double edgeMargin) {
        super();
        this.routeMargin = routeMargin;
        this.edgeMargin = edgeMargin;
        conflictFinder = new ConflictFinder(this, routeMargin, edgeMargin);
    }

    public double getRouteMargin() {
        return routeMargin;
    }

    public double getEdgeMargin() {
        return edgeMargin;
    }

    /**
     * @return a sorted {@link List} set conflict
     */
    @NotNull
    public EnhancedList<Conflict> evaluateConflicts(boolean biggestConflictFirst) {
        return conflictFinder.getConflicts()
                .reverseIf(() -> biggestConflictFirst);
    }

    public long countNonOctilinearEdges() {
        return getEdges().stream()
                .filter(Edge::isNotOctilinear)
                .count();
    }

    public Node createCrossingNode(double x, double y) {
        Node node = new Node(x, y, "C" + (++crossingCount), BendNodeSignature::new);
        addNodes(node);
        return node;
    }

    public Node createJunctionNode(double x, double y) {
        Node node = new Node(x, y, "J" + (++junctionCount), BendNodeSignature::new);
        addNodes(node);
        return node;
    }

    public Node createBendNode(double x, double y) {
        Node node = new Node(x, y, "B" + (++bendCount), BendNodeSignature::new);
        addNodes(node);
        return node;
    }

}
