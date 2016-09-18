/*
 * Copyright (c) 2016 Thomas Zuberbuehler. All rights reserved.
 */

package ch.geomo.tramaps.graph.util;

import ch.geomo.tramaps.graph.Edge;
import ch.geomo.tramaps.graph.Node;
import ch.geomo.util.collection.list.EnhancedList;
import ch.geomo.util.collection.set.EnhancedSet;
import org.jetbrains.annotations.NotNull;

public enum GraphUtil {

    /* util class */;

    public static double getAbsDiffDeltaXY(@NotNull Edge edge) {
        return Math.abs(getAbsDeltaX(edge) - getAbsDeltaY(edge));
    }

    public static double getAbsDeltaX(@NotNull Node nodeA, @NotNull Node nodeB) {
        return Math.abs(nodeA.getX() - nodeB.getX());
    }

    public static double getAbsDeltaY(@NotNull Node nodeA, @NotNull Node nodeB) {
        return Math.abs(nodeA.getY() - nodeB.getY());
    }

    public static double getAbsDeltaX(@NotNull Edge edge) {
        return getAbsDeltaX(edge.getNodeA(), edge.getNodeB());
    }

    public static double getAbsDeltaY(@NotNull Edge edge) {
        return getAbsDeltaY(edge.getNodeA(), edge.getNodeB());
    }

    public static boolean isComponentWhenRemovingBridge(@NotNull Edge edge, @NotNull Node node, @NotNull EnhancedList<Node> nodes) {

        EnhancedSet<Edge> adjacentEdges = node.getAdjacentEdges(edge);

        if (nodes.contains(node)) {
            return false;
        }
        nodes.add(node);

        boolean isComponent = true;
        for (Edge adjacentEdge : adjacentEdges) {
            Node otherNode = adjacentEdge.getOtherNode(node);
            isComponent = isComponent && isComponentWhenRemovingBridge(adjacentEdge, otherNode, nodes);
        }
        return isComponent;

    }

}
