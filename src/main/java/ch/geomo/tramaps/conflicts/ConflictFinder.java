package ch.geomo.tramaps.conflicts;

import ch.geomo.tramaps.conflicts.buffer.EdgeBuffer;
import ch.geomo.tramaps.conflicts.buffer.ElementBuffer;
import ch.geomo.tramaps.conflicts.buffer.NodeBuffer;
import ch.geomo.tramaps.graph.Edge;
import ch.geomo.tramaps.graph.Node;
import ch.geomo.tramaps.util.CollectionUtil;
import ch.geomo.util.tuple.Tuple;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class ConflictFinder {

    /**
     * Returns true if both elements are not equal and not adjacent or at least one element is a node.
     */
    private final static Predicate<Tuple<ElementBuffer>> CONFLICT_TUPLE_PREDICATE = (Tuple<ElementBuffer> tuple) -> {
        if (tuple.getFirst() == tuple.getSecond()) {
            return false;
        }
        boolean adjacent = tuple.getFirst().getElement().isAdjacent(tuple.getSecond().getElement());
        return !adjacent || tuple.stream()
                .noneMatch(buffer -> buffer.getElement() instanceof Node);
    };

    private final double routeMargin;
    private final double edgeMargin;

    public ConflictFinder(double routeMargin, double edgeMargin) {
        this.routeMargin = routeMargin;
        this.edgeMargin = edgeMargin;
    }

    @NotNull
    public Set<Conflict> getConflicts(@NotNull Set<Edge> edges, @NotNull Set<Node> nodes) {

        Set<ElementBuffer> edgeBuffers = edges.parallelStream()
                .map(edge -> new EdgeBuffer(edge, this.routeMargin, this.edgeMargin))
                .collect(Collectors.toSet());

        Set<ElementBuffer> nodeBuffers = nodes.parallelStream()
                .map(node -> new NodeBuffer(node, this.edgeMargin))
                .collect(Collectors.toSet());

        Set<ElementBuffer> buffers = new HashSet<>(edgeBuffers);
        buffers.addAll(nodeBuffers);

        Set<Tuple<ElementBuffer>> tuples = CollectionUtil.makePairs(buffers, ConflictFinder.CONFLICT_TUPLE_PREDICATE);

        return tuples.parallelStream()
                .filter(tuple -> tuple.getFirst().getBuffer().relate(tuple.getSecond().getBuffer(), "T********"))
                .map(tuple -> new Conflict(tuple.getFirst(), tuple.getSecond()))
                .collect(Collectors.toSet());

    }

}
