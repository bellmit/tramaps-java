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
        return new ConflictFinder(routeMargin, edgeMargin).getConflicts(getEdges(), getNodes()).stream()
                .sorted((c1, c2) -> {
                    double l1 = c1.getBestMoveVectorAlongAnAxis().length();
                    double l2 = c2.getBestMoveVectorAlongAnAxis().length();
                    if (l1 == l2) {
                        l1 = l1 + c1.getMoveVector().length();
                        l2 = l2 + c2.getMoveVector().length();
                    }
                    if (l1 == l2) {
                        // same distance but different directions
                        double x1 = c1.getMoveVector().getX();
                        double x2 = c2.getMoveVector().getX();
                        if (x1 == x2) {
                            return Double.compare(c1.getMoveVector().getY(), c2.getMoveVector().getY());
                        }
                        return Double.compare(x1, x2);
                    }
                    if (!biggestConflictFirst) {
                        return Double.compare(l1, l2);
                    }
                    return Double.compare(l2, l1);
                });
    }

    @NotNull
    public List<Conflict> evaluateConflicts(double routeMargin, double edgeMargin) {
        return new ConflictFinder(routeMargin, edgeMargin).getConflictList(getEdges(), getNodes());
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
