package ch.geomo.tramaps.map;

import ch.geomo.tramaps.conflicts.Conflict;
import ch.geomo.tramaps.conflicts.ConflictFinder;
import ch.geomo.tramaps.graph.Edge;
import ch.geomo.tramaps.graph.Graph;
import ch.geomo.tramaps.graph.Node;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.DoubleStream;
import java.util.stream.Stream;

public class MetroMap extends Graph {

    public MetroMap() {
        super();
    }

    public MetroMap(@NotNull Set<Edge> edges, @NotNull Set<Node> nodes) {
        super(edges, nodes);
    }

    /**
     * Evaluate conflicts and returns them sorted.
     */
    @NotNull
    public Stream<Conflict> evaluateConflicts(double routeMargin, double edgeMargin, boolean biggestConflictFirst) {
        return new ConflictFinder(routeMargin, edgeMargin)
                .getConflicts(getEdges(), getNodes()).stream()
                .sorted((c1, c2) -> {
                    Conflict conflict1 = biggestConflictFirst ? c2 : c1;
                    Conflict conflict2 = biggestConflictFirst ? c1 : c2;
                    return conflict1.compareTo(conflict2);
                });
    }

    @NotNull
    public Stream<Edge> evaluateNonOctilinearEdges() {
        return getEdges().stream()
                .filter(Edge::isNonOctilinear);
    }

    @Override
    public String toString() {
        return new StringBuilder()
                .append("Edges: ")
                .append(getEdges())
                .append("\n")
                .append("Nodes: ")
                .append(getNodes())
                .toString();
    }
}
