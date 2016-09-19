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
import ch.geomo.tramaps.map.MetroMap;
import ch.geomo.util.collection.GCollectors;
import ch.geomo.util.collection.list.EnhancedList;
import ch.geomo.util.collection.pair.Pair;
import ch.geomo.util.collection.set.EnhancedSet;
import ch.geomo.util.collection.set.GSet;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;
import org.jetbrains.annotations.NotNull;

import java.util.Comparator;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class ConflictFinder {

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

    private final Graph graph;
    private final double routeMargin;
    private final double edgeMargin;
    private final double nodeMargin;

    public ConflictFinder(@NotNull Graph graph, double routeMargin, double edgeMargin, double nodeMargin) {
        this.graph = graph;
        this.routeMargin = routeMargin;
        this.edgeMargin = edgeMargin;
        this.nodeMargin = nodeMargin;
    }

    private Stream<ElementBuffer> createEdgeBuffers() {
        return graph.getEdges().stream()
                .map(edge -> new EdgeBuffer(edge, routeMargin, edgeMargin));
    }

    private Stream<ElementBuffer> createNodeBuffers() {
        return graph.getNodes().stream()
                .map(node -> new NodeBuffer(node, nodeMargin));
    }

    private static boolean intersects(@NotNull Pair<ElementBuffer> bufferPair) {

        ElementBuffer a = bufferPair.getFirst();
        ElementBuffer b = bufferPair.getSecond();

        Polygon pa = a.getBuffer();
        Polygon pb = b.getBuffer();

//        if (bufferPair.stream().allMatch(buffer -> buffer instanceof NodeBuffer)) {
//            if (a.getElement().isAdjacent(b.getElement())) {
//                pa = (Polygon)pa.buffer(50);
//                pb = (Polygon)pb.buffer(50);
//            }
//        }

        return pa.relate(pb, "T********");

    }

    @NotNull
    private EnhancedList<Conflict> getBufferConflicts() {
        return getConflictElements().stream()
                // check interior intersection
                .filter(ConflictFinder::intersects)
                // create conflict
                .map(BufferConflict::new)
                // filter conflicts which do not cross with other (not-conflict related) edges
                .filter(conflict -> conflict.hasElementNeighborhood(graph.getEdges()))
                // filter unsolved conflicts
                .filter(BufferConflict::isNotSolved)
                // remove duplicates
                .distinct()
                .collect(GCollectors.toList());
    }

    @NotNull
    public EnhancedList<Conflict> getOctilinearConflicts(double correctionFactor, boolean majorMisalignmentOnly) {
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

    @NotNull
    private EnhancedSet<Pair<ElementBuffer>> getConflictElements() {
        EnhancedSet<ElementBuffer> buffers = GSet.createSet(createEdgeBuffers(), createNodeBuffers());
        return buffers.toPairSet(ConflictFinder.CONFLICT_PAIR_PREDICATE);
    }

    @NotNull
    public EnhancedList<Conflict> getConflicts(double correctionFactor, boolean majorMisalignmentOnly) {
        return getBufferConflicts()
                .union(getOctilinearConflicts(correctionFactor, majorMisalignmentOnly))
                .sortElements(CONFLICT_COMPARATOR);
    }

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
