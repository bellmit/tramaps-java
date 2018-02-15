/*
 * Copyright (c) 2016-2018 Thomas Zuberbuehler. All rights reserved.
 */

package ch.geomo.tramaps.conflict;

import ch.geomo.tramaps.conflict.buffer.EdgeBuffer;
import ch.geomo.tramaps.conflict.buffer.ElementBuffer;
import ch.geomo.tramaps.conflict.buffer.ElementBufferPair;
import ch.geomo.tramaps.conflict.buffer.NodeBuffer;
import ch.geomo.tramaps.graph.Edge;
import ch.geomo.tramaps.graph.Node;
import ch.geomo.tramaps.map.MetroMap;
import ch.geomo.util.collection.GCollectors;
import ch.geomo.util.collection.list.EnhancedList;
import ch.geomo.util.collection.pair.Pair;
import ch.geomo.util.collection.set.EnhancedSet;
import ch.geomo.util.collection.set.GSet;
import org.jetbrains.annotations.NotNull;

import java.util.Comparator;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * Provides methods to find conflicts.
 */
public class ConflictFinder {

    /**
     * Comparator to compare {@link Conflict} instances.
     */
    public final static Comparator<Conflict> CONFLICT_COMPARATOR = new ConflictComparator();

    /**
     * {@link Predicate} returns true if both elements are not equal and not adjacent or at least one element is a node.
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

    private final MetroMap map;

    private final double routeMargin;
    private final double edgeMargin;
    private final double nodeMargin;

    public ConflictFinder(@NotNull MetroMap map, double routeMargin, double edgeMargin, double nodeMargin) {
        this.map = map;
        this.routeMargin = routeMargin;
        this.edgeMargin = edgeMargin;
        this.nodeMargin = nodeMargin;
    }

    /**
     * @return all edge buffers as a {@link Stream}
     */
    @NotNull
    private Stream<ElementBuffer> createEdgeBuffers() {
        return map.getEdges().stream()
                .map(edge -> new EdgeBuffer(edge, routeMargin, edgeMargin));
    }

    /**
     * @return all node buffers as a {@link Stream}
     */
    @NotNull
    private Stream<ElementBuffer> createNodeBuffers() {
        return map.getNodes().stream()
                .map(node -> new NodeBuffer(node, nodeMargin));
    }

    /**
     * @return all current {@link BufferConflict}s
     */
    @NotNull
    private EnhancedList<Conflict> getBufferConflicts() {
        return getConflictElements().stream()
                // check interior intersection
                .filter(ConflictFinder::intersects)
                // create conflict
                .map(BufferConflict::new)
                // filter conflicts which do not cross with other (not-conflict related) edges
                // .filter(conflict -> conflict.hasElementNeighborhood(map.getEdges()))
                // filter unsolved conflicts
                .filter(BufferConflict::isNotSolved)
                // remove duplicates
                .distinct()
                .collect(GCollectors.toList());
    }

    /**
     * @return all current {@link OctilinearConflict}s
     */
    @NotNull
    private EnhancedList<Conflict> getOctilinearConflicts(double correctionFactor, boolean majorMisalignmentOnly) {
        // fix/improvement required: buffers are not required, should be rewritten without using set of buffers
        return getConflictElements().stream()
                // check conflict
                .filter(bufferPair -> hasOctilinearConflict(bufferPair, majorMisalignmentOnly))
                // create conflict
                .map(bufferPair -> new OctilinearConflict(bufferPair, correctionFactor))
                // remove duplicates
                .distinct()
                // sort conflicts (smallest conflict first)
                .sorted()
                .collect(GCollectors.toList());
    }

    /**
     * Evaluates if given buffer pair does have a misalignment respectively a non-octilinear edge. If the second
     * parameters is true, then only misalignment with a wrong angle greater than 27.5 degree will be considered.
     * @return if the given buffer pair does have a misalignment
     */
    private boolean hasOctilinearConflict(@NotNull Pair<ElementBuffer> bufferPair, boolean majorMisalignmentOnly) {
        if (bufferPair.stream().allMatch(buffer -> buffer instanceof NodeBuffer)) {
            Node a = (Node) bufferPair.getFirst().getElement();
            Node b = (Node) bufferPair.getSecond().getElement();
            Edge adjacentEdge = a.getAdjacentEdgeWith(b);
            if (adjacentEdge != null && adjacentEdge.isNotOctilinear()) {
                return !majorMisalignmentOnly || adjacentEdge.hasMajorMisalignment();
            }
        }
        return false;
    }

    /**
     * @return all pairs of conflict elements
     */
    @NotNull
    private EnhancedSet<Pair<ElementBuffer>> getConflictElements() {
        EnhancedSet<ElementBuffer> buffers = GSet.createSet(createEdgeBuffers(), createNodeBuffers());
        return buffers.toPairSet(ConflictFinder.CONFLICT_PAIR_PREDICATE);
    }

    /**
     * Returns all {@link BufferConflict}s and {@link OctilinearConflict}s. The first parameter configures an instance
     * of {@link OctilinearConflict} in order to initialize its move vector. If the second parameter is true, only
     * only misalignment with a wrong angle greater than 27.5 degree will be considered.
     * @return all {@link BufferConflict}s and {@link OctilinearConflict}s
     */
    @NotNull
    public EnhancedList<Conflict> getConflicts(double correctionFactor, boolean majorMisalignmentOnly) {
        return getBufferConflicts()
                .union(getOctilinearConflicts(correctionFactor, majorMisalignmentOnly))
                .sortElements(CONFLICT_COMPARATOR);
    }

    /**
     * @return true if the interior of the element buffers intersects
     */
    private static boolean intersects(@NotNull Pair<ElementBuffer> bufferPair) {
        return bufferPair.getFirst().getBuffer().relate(bufferPair.getSecond().getBuffer(), "T********");
    }

    /**
     * @return true if the given {@link Node} and the given {@link Edge} has a conflict
     */
    public static boolean hasConflict(@NotNull Node node, @NotNull Edge edge, @NotNull MetroMap map) {
        NodeBuffer nodeBuffer = new NodeBuffer(node, map.getNodeMargin());
        EdgeBuffer edgeBuffer = new EdgeBuffer(edge, map.getRouteMargin(), map.getEdgeMargin());
        Pair<ElementBuffer> bufferPair = Pair.of(nodeBuffer, edgeBuffer);
        boolean intersects = intersects(bufferPair);
        if (intersects) {
            BufferConflict conflict = new BufferConflict(bufferPair);
            return !conflict.isSolved();
        }
        return false;
    }

    /**
     * @return true if the given {@link Node}s has a conflict
     */
    public static boolean hasConflict(@NotNull Node node1, @NotNull Node node2, @NotNull MetroMap map) {
        NodeBuffer nodeBuffer1 = new NodeBuffer(node1, map.getNodeMargin());
        NodeBuffer nodeBuffer2 = new NodeBuffer(node2, map.getNodeMargin());
        Pair<ElementBuffer> bufferPair = Pair.of(nodeBuffer1, nodeBuffer2);
        boolean intersects = intersects(bufferPair);
        if (intersects) {
            BufferConflict conflict = new BufferConflict(bufferPair);
            return !conflict.isSolved();
        }
        return false;
    }

}
