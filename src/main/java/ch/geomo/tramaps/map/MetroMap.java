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
import ch.geomo.util.doc.HelperMethod;
import org.jetbrains.annotations.NotNull;

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

    @SuppressWarnings("unused")
    public double getEdgeMargin() {
        return edgeMargin;
    }

    /**
     * @return a sorted {@link EnhancedList} of conflicts
     */
    @NotNull
    public EnhancedList<Conflict> evaluateConflicts(boolean biggestConflictFirst) {
        return conflictFinder.getConflicts()
                .reverseIf(() -> biggestConflictFirst);
    }

    /**
     * @return a sorted {@link EnhancedList} of octilinear conflicts
     */
    @NotNull
    public EnhancedList<Conflict> evaluateOctilinearConflicts(double correctionFactor, boolean biggestConflictFirst) {
        return conflictFinder.getOctilinearConflicts(correctionFactor, false)
                .reverseIf(() -> biggestConflictFirst);
    }

    @HelperMethod
    public long countNonOctilinearEdges() {
        return getEdges().stream()
                .filter(Edge::isNotOctilinear)
                .count();
    }

    @NotNull
    public Node createCrossingNode(double x, double y) {
        Node node = new Node("C" + (++crossingCount), x, -y, BendNodeSignature::new);
        addNodes(node);
        return node;
    }

    @NotNull
    public Node createJunctionNode(double x, double y) {
        Node node = new Node("J" + (++junctionCount), x, -y, BendNodeSignature::new);
        addNodes(node);
        return node;
    }

    @NotNull
    public Node createBendNode(double x, double y) {
        Node node = new Node("B" + (++bendCount), x, -y, BendNodeSignature::new);
        addNodes(node);
        return node;
    }

}
