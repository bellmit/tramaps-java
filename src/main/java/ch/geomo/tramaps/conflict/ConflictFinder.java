/*
 * Copyright (c) 2016 Thomas Zuberbuehler. All rights reserved.
 */

package ch.geomo.tramaps.conflict;

import ch.geomo.tramaps.conflict.buffer.EdgeBuffer;
import ch.geomo.tramaps.conflict.buffer.ElementBuffer;
import ch.geomo.tramaps.conflict.buffer.ElementBufferPair;
import ch.geomo.tramaps.conflict.buffer.NodeBuffer;
import ch.geomo.tramaps.graph.Edge;
import ch.geomo.tramaps.graph.Graph;
import ch.geomo.tramaps.graph.Node;
import ch.geomo.util.collection.GCollection;
import ch.geomo.util.collection.GCollectors;
import ch.geomo.util.collection.list.EnhancedList;
import ch.geomo.util.collection.set.EnhancedSet;
import ch.geomo.util.collection.list.GList;
import ch.geomo.util.collection.set.GSet;
import ch.geomo.util.collection.pair.Pair;
import org.jetbrains.annotations.NotNull;
import sun.misc.GC;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ConflictFinder {

    public final static Comparator<Conflict> CONFLICT_COMPARATOR = new ConflictComparator();

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

        EnhancedSet<ElementBuffer> buffers = GSet.createSet(createEdgeBuffers(), createNodeBuffers());
        EnhancedSet<Pair<ElementBuffer>> bufferPairs = buffers.toPairSet(ConflictFinder.CONFLICT_PAIR_PREDICATE);

        EnhancedList<Conflict> bufferConflicts = bufferPairs.stream()
                // check interior intersection
                .filter(bufferPair -> bufferPair.getFirst().getBuffer().relate(bufferPair.getSecond().getBuffer(), "T********"))
                // create conflict
                .map(BufferConflict::new)
                // filter conflicts which do not cross with other (not-conflict related) edges
                .filter(conflict -> conflict.hasElementNeighborhood(graph.getEdges()))
                // filter unsolved conflicts
                .filter(BufferConflict::isNotSolved)
                // remove duplicates
                .distinct()
                .collect(GCollectors.toList());

//        EnhancedList<Conflict> octilinearConflicts = bufferPairs.stream()
//                // check conflict
//                .filter(this::hasOctilinearConflict)
//                // create conflict
//                .map(OctilinearConflict::new)
//                // remove duplicates
//                .distinct()
//                // sort conflicts (smallest conflict first)
//                .sorted()
//                .collect(GCollectors.toList());

        return bufferConflicts
                .union(new ArrayList<>())
                .sortElements(CONFLICT_COMPARATOR);

    }

    private boolean hasOctilinearConflict(Pair<ElementBuffer> bufferPair) {
        if (bufferPair.stream().allMatch(buffer -> buffer instanceof NodeBuffer)) {
            Node a = (Node)bufferPair.getFirst().getElement();
            Node b = (Node)bufferPair.getSecond().getElement();
            Edge adjacentEdge = a.getAdjacentEdgeWith(b);
            if (adjacentEdge != null && adjacentEdge.isNotOctilinear()) {
                return adjacentEdge.hasMajorMisalignment();
            }
        }
        return false;
    }

}
