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
import ch.geomo.util.collection.list.EnhancedList;
import org.jetbrains.annotations.NotNull;

public class MetroMap extends Graph {

    private final ConflictFinder conflictFinder;

    private final double routeMargin;
    private final double edgeMargin;
    private final double nodeMargin;

    private int bendCount = 0;
    private int crossingCount = 0;
    private int junctionCount = 0;

    public MetroMap(double routeMargin, double edgeMargin, double nodeMargin) {
        super();
        this.routeMargin = routeMargin;
        this.edgeMargin = edgeMargin;
        this.nodeMargin = nodeMargin;
        conflictFinder = new ConflictFinder(this, routeMargin, edgeMargin, nodeMargin);
    }

    public double getRouteMargin() {
        return routeMargin;
    }

    @SuppressWarnings("unused")
    public double getEdgeMargin() {
        return edgeMargin;
    }

    @SuppressWarnings("unused")
    public double getNodeMargin() {
        return nodeMargin;
    }

    /**
     * @return a sorted {@link EnhancedList} of conflicts
     */
    @NotNull
    public EnhancedList<Conflict> evaluateConflicts(boolean biggestConflictFirst) {
        return conflictFinder.getConflicts(0.25, true)
                .reverseIf(() -> biggestConflictFirst);
    }

    /**
     * @return a sorted {@link EnhancedList} of conflicts
     */
    @NotNull
    public EnhancedList<Conflict> evaluateConflicts(boolean biggestConflictFirst, double correctionFactor, boolean majorMisalignmentOnly) {
        return conflictFinder.getConflicts(correctionFactor, majorMisalignmentOnly)
                .reverseIf(() -> biggestConflictFirst);
    }

    public long countNonOctilinearEdges() {
        return getEdges().stream()
                .filter(Edge::isNotOctilinear)
                .count();
    }

    @NotNull
    public Node createCrossingNode(double x, double y) {
        Node node = new Node("C" + (++crossingCount), x, y, BendNodeSignature::new);
        addNodes(node);
        return node;
    }

    @NotNull
    public Node createJunctionNode(double x, double y) {
        Node node = new Node("J" + (++junctionCount), x, y, BendNodeSignature::new);
        addNodes(node);
        return node;
    }

    @NotNull
    public Node createBendNode(double x, double y) {
        Node node = new Node("B" + (++bendCount), x, y, BendNodeSignature::new);
        addNodes(node);
        return node;
    }

}
