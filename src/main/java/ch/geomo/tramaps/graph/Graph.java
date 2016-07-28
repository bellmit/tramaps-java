package ch.geomo.tramaps.graph;

import ch.geomo.util.tuple.Tuple;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Graph {

    private Set<Edge> edges;
    private Set<Node> nodes;

    public Graph() {
        this(new HashSet<>(), new HashSet<>());
    }

    public Graph(@NotNull Set<Edge> edges, @NotNull Set<Node> nodes) {
        // TODO
    }

    public Set<Edge> getEdges() {
        return edges;
    }

    public Set<Node> getNodes() {
        return nodes;
    }

    public Set<Node> getBridgeEdges() {
        // TODO
        return null;
    }

    public Map<Edge, Tuple<Graph>> getSubGraphsByLeavingOut(Set<Edge> edges) {
        // TODO
        return null;
    }

}
