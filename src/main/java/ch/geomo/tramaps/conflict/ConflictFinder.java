/*
 * Copyright (c) 2016 Thomas Zuberbuehler. All rights reserved.
 */

package ch.geomo.tramaps.conflict;

import ch.geomo.tramaps.conflict.buffer.EdgeBuffer;
import ch.geomo.tramaps.conflict.buffer.ElementBuffer;
import ch.geomo.tramaps.conflict.buffer.ElementBufferPair;
import ch.geomo.tramaps.conflict.buffer.NodeBuffer;
import ch.geomo.tramaps.graph.Graph;
import ch.geomo.util.collection.EnhancedList;
import ch.geomo.util.collection.EnhancedSet;
import ch.geomo.util.collection.GList;
import ch.geomo.util.collection.GSet;
import ch.geomo.util.pair.Pair;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ConflictFinder {

    /**
     * Returns true if both elements are not equal and not adjacent or at least one element is a node.
     */
    private final static Predicate<Pair<ElementBuffer>> CONFLICT_PAIR_PREDICATE = (Pair<ElementBuffer> pair) -> {

        ElementBufferPair bufferPair = new ElementBufferPair(pair);

        if (bufferPair.hasEqualElements()) {
            return false;
        }
        else if (!bufferPair.hasAdjacentElements()) {
            return true;
        }
        return bufferPair.isNodePair();

    };

    private final Graph graph;
    private final double routeMargin;
    private final double edgeMargin;

    public ConflictFinder(@NotNull Graph graph, double routeMargin, double edgeMargin) {
        this.graph = graph;
        this.routeMargin = routeMargin;
        this.edgeMargin = edgeMargin;
    }

    private Stream<ElementBuffer> createEdgeBuffers() {
        return graph.getEdges().stream()
                .map(edge -> new EdgeBuffer(edge, routeMargin, edgeMargin));
    }

    private Stream<ElementBuffer> createNodeBuffers() {
        return graph.getNodes().stream()
                .map(node -> new NodeBuffer(node, edgeMargin));
    }

    @NotNull
    public EnhancedList<Conflict> getConflicts() {

        EnhancedSet<ElementBuffer> buffers = GSet.set(createEdgeBuffers(), createNodeBuffers());

        List<Conflict> conflicts = buffers.toPairStream(ConflictFinder.CONFLICT_PAIR_PREDICATE)
                // check interior intersection
                .filter(bufferPair -> bufferPair.getFirst().getBuffer().relate(bufferPair.getSecond().getBuffer(), "T********"))
                // create conflict
                .map(Conflict::new)
                // filter conflicts which do not cross with other (not-conflict related) edges
                .filter(conflict -> conflict.hasElementNeighborhood(graph.getEdges()))
                // filter unsolved conflicts
                .filter(Conflict::isNotSolved)
                // remove duplicates
                .distinct()
                // sort conflicts (smallest conflict first)
                .sorted()
                .collect(Collectors.toList());

        return GList.list(conflicts);

    }

}
