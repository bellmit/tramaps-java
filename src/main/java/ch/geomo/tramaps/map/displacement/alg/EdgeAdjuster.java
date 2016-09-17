/*
 * Copyright (c) 2016 Thomas Zuberbuehler. All rights reserved.
 */

package ch.geomo.tramaps.map.displacement.alg;

import ch.geomo.tramaps.graph.Edge;
import ch.geomo.tramaps.graph.Graph;
import ch.geomo.tramaps.graph.Node;
import ch.geomo.tramaps.graph.layout.OctilinearEdge;
import ch.geomo.tramaps.graph.layout.OctilinearEdgeBuilder;
import ch.geomo.util.collection.pair.Pair;
import ch.geomo.util.logging.Loggers;
import org.jetbrains.annotations.NotNull;

/**
 * Adjusts edges.
 */
public class EdgeAdjuster {

    private final Graph graph;
    private final Edge edge;
    private final NodeDisplaceResult displaceResult;

    // TODO to be deleted just for testing
    public EdgeAdjuster(@NotNull Graph graph, @NotNull Edge edge) {
        this.graph = graph;
        this.edge = edge;
        displaceResult = null;
    }

    public EdgeAdjuster(@NotNull Graph graph, @NotNull Edge edge, @NotNull NodeDisplaceResult displaceResult) {
        this.graph = graph;
        this.edge = edge;
        this.displaceResult = displaceResult;
    }

    public void correctEdge() {

        correctEdgeByIntroducingBendNodes();

    }

    /**
     * Introduces a bend node for given {@link Edge}. The given {@link Edge} instance
     * will be destroyed.
     */
    private void correctEdgeByIntroducingBendNodes() {

        // create octilinear edge
        OctilinearEdge octilinearEdge = new OctilinearEdgeBuilder()
                .setOriginalEdge(edge)
                .build();

        Pair<Node> vertices = octilinearEdge.getVertices();

        if (vertices.hasNonNullValues()) {

            // only one vertex
            if (vertices.second() == null) {
                graph.addNodes(vertices.first());
                edge.getNodeA()
                        .createAdjacentEdgeTo(vertices.first(), edge.getRoutes())
                        .createAdjacentEdgeTo(edge.getNodeB(), edge.getRoutes());
            }
            else {
                graph.addNodes(vertices.first(), vertices.second());
                edge.getNodeA()
                        .createAdjacentEdgeTo(vertices.first(), edge.getRoutes())
                        .createAdjacentEdgeTo(vertices.second(), edge.getRoutes())
                        .createAdjacentEdgeTo(edge.getNodeB(), edge.getRoutes());
            }

            Loggers.info(this, "Octilinear edge created: " + edge);

            // remove old edge
            edge.delete();

            // numbers set nodes has changed, edge cache must be flagged for rebuild
            graph.updateGraph();

        }
        else {
            Loggers.warning(this, "No octilinear edge created: " + edge);
        }

    }

}
