/*
 * Copyright (c) 2016 Thomas Zuberbuehler. All rights reserved.
 */

package ch.geomo.tramaps.map;

import ch.geomo.tramaps.graph.Node;
import ch.geomo.tramaps.graph.Route;
import ch.geomo.tramaps.map.signature.BendNodeSignature;
import ch.geomo.tramaps.map.signature.RectangleStationSignature;
import ch.geomo.util.Builder;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;

public class MetroMapEdgeBuilder implements Builder<LinkedList<Node>> {

    private MetroMap map;
    private Route[] routes = new Route[1];

    private LinkedList<Node> nodes;

    private boolean built = false;

    public MetroMapEdgeBuilder(@NotNull MetroMap map) {
        this.map = map;
        nodes = new LinkedList<>();
    }

    @NotNull
    public MetroMapEdgeBuilder routes(@NotNull Route... routes) {
        this.routes = routes;
        return this;
    }

    @NotNull
    public MetroMapEdgeBuilder station(@NotNull Node node) {
        nodes.add(node);
        return this;
    }

    @NotNull
    public MetroMapEdgeBuilder junction(@NotNull Node node) {
        nodes.add(node);
        return this;
    }

    @NotNull
    public MetroMapEdgeBuilder crossing(@NotNull Node node) {
        nodes.add(node);
        return this;
    }

    @NotNull
    public MetroMapEdgeBuilder station(double x, double y, @NotNull String name) {
        nodes.add(map.createNode(x, y, name, RectangleStationSignature::new));
        return this;
    }

    @NotNull
    public MetroMapEdgeBuilder bend(double x, double y) {
        nodes.add(map.createBendNode(x, y));
        return this;
    }

    public LinkedList<Node> create() {
        return build();
    }

    @NotNull
    @Override
    public LinkedList<Node> build() {
        if (built) {
            return nodes;
        }
        built = true;
        for (int i = 0; i < (nodes.size() - 1); i++) {
            map.createEdge(nodes.get(i), nodes.get(i + 1), routes);
        }
        return nodes;
    }

}
