/*
 * Copyright (c) 2016 Thomas Zuberbuehler. All rights reserved.
 */

package ch.geomo.tramaps.conflict;

import ch.geomo.tramaps.conflict.buffer.EdgeBuffer;
import ch.geomo.tramaps.conflict.buffer.ElementBuffer;
import ch.geomo.tramaps.conflict.buffer.ElementBufferPair;
import ch.geomo.tramaps.conflict.buffer.NodeBuffer;
import ch.geomo.tramaps.geom.util.PolygonUtil;
import ch.geomo.tramaps.graph.Edge;
import ch.geomo.tramaps.graph.Node;
import ch.geomo.tramaps.map.MetroMap;
import ch.geomo.util.CollectionUtil;
import ch.geomo.util.Loggers;
import ch.geomo.util.pair.Pair;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.GeometryCollection;
import org.geotools.geometry.jts.JTS;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static ch.geomo.tramaps.geom.util.GeomUtil.getGeomUtil;

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

    private final double routeMargin;
    private final double edgeMargin;

    public ConflictFinder(double routeMargin, double edgeMargin) {
        this.routeMargin = routeMargin;
        this.edgeMargin = edgeMargin;
    }

    @NotNull
    public List<Conflict> getConflicts(MetroMap map) {

        Set<Edge> edges = map.getEdges();
        Set<Node> nodes = map.getNodes();

        // Envelope bbox = map.getBoundingBox();
        // GeometryCollection lineStrings = getGeomUtil().createCollection(edges.stream().map(Edge::getLineString));
        // GeometryCollection polygons = PolygonUtil.splitPolygon(JTS.toGeometry(bbox), lineStrings);

        Set<ElementBuffer> edgeBuffers = edges.stream()
                .map(edge -> new EdgeBuffer(edge, routeMargin, edgeMargin))
                .collect(Collectors.toSet());

        Set<ElementBuffer> nodeBuffers = nodes.stream()
                .map(node -> new NodeBuffer(node, edgeMargin))
                .collect(Collectors.toSet());

        Set<ElementBuffer> buffers = new HashSet<>(edgeBuffers);
        buffers.addAll(nodeBuffers);

        Set<Pair<ElementBuffer>> pairs = CollectionUtil.makePairs(buffers, ConflictFinder.CONFLICT_PAIR_PREDICATE);

        return pairs.stream()
                .filter(tuple -> tuple.getFirst().getBuffer().relate(tuple.getSecond().getBuffer(), "T********"))
                .map(tuple -> new Conflict(tuple.getFirst(), tuple.getSecond()))
                .filter(conflict ->  conflict.hasElementNeighborhood(edges))
                .filter(conflict -> !conflict.isSolved())
                .distinct()
                .sorted()
                .collect(Collectors.toList());

        // filter conflicts which does not contains another conflict
        // return result.stream()
        //        .filter(conflict -> result.stream()
        //                .filter(conflict2 -> !conflict.equals(conflict2))
        //                .noneMatch(conflict2 -> conflict.getConflictPolygon().relate(conflict2.getConflictPolygon(), "T*****FF*")))
        //        .collect(Collectors.toList());

    }

}
