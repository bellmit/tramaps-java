/*
 * Copyright (c) 2016 Thomas Zuberbuehler. All rights reserved.
 */

package ch.geomo.tramaps.conflict;

import ch.geomo.tramaps.conflict.buffer.EdgeBuffer;
import ch.geomo.tramaps.conflict.buffer.ElementBuffer;
import ch.geomo.tramaps.conflict.buffer.NodeBuffer;
import ch.geomo.tramaps.graph.Edge;
import ch.geomo.tramaps.graph.Node;
import ch.geomo.util.CollectionUtil;
import ch.geomo.util.pair.Pair;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class ConflictFinder {

    /**
     * Returns true if both elements are not equal and not adjacent or at least one element is a node.
     */
    private final static Predicate<Pair<ElementBuffer>> CONFLICT_PAIR_PREDICATE = (Pair<ElementBuffer> pair) -> {
        if (pair.getFirst() == pair.getSecond()) {
            return false;
        }
        boolean adjacent = pair.getFirst().getElement().isAdjacent(pair.getSecond().getElement());
        return !adjacent || pair.stream()
                .anyMatch(buffer -> buffer.getElement() instanceof NodeBuffer);
    };

    private final double routeMargin;
    private final double edgeMargin;

    public ConflictFinder(double routeMargin, double edgeMargin) {
        this.routeMargin = routeMargin;
        this.edgeMargin = edgeMargin;
    }

    public List<Conflict> getConflictList(@NotNull Set<Edge> edges, @NotNull Set<Node> nodes) {
        return new ArrayList<>(getConflicts(edges, nodes));
    }

    @NotNull
    public Set<Conflict> getConflicts(@NotNull Set<Edge> edges, @NotNull Set<Node> nodes) {

        Set<ElementBuffer> edgeBuffers = edges.stream()
                .map(edge -> new EdgeBuffer(edge, this.routeMargin, this.edgeMargin))
                .collect(Collectors.toSet());

        Set<ElementBuffer> nodeBuffers = nodes.stream()
                .map(node -> new NodeBuffer(node, this.edgeMargin))
                .collect(Collectors.toSet());

        Set<ElementBuffer> buffers = new HashSet<>(edgeBuffers);
        buffers.addAll(nodeBuffers);

        Set<Pair<ElementBuffer>> pairs = CollectionUtil.makePairs(buffers, ConflictFinder.CONFLICT_PAIR_PREDICATE);

        return pairs.stream()
                .filter(tuple -> tuple.getFirst().getBuffer().relate(tuple.getSecond().getBuffer(), "T********"))
                .map(tuple -> new Conflict(tuple.getFirst(), tuple.getSecond()))
                .collect(Collectors.toSet());

    }

}
