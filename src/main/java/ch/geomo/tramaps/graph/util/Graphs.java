/*
 * Copyright (c) 2016 Thomas Zuberbuehler. All rights reserved.
 */

package ch.geomo.tramaps.graph.util;

import ch.geomo.tramaps.graph.Edge;
import ch.geomo.tramaps.graph.Node;
import org.jetbrains.annotations.NotNull;

public enum Graphs {

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

}
