/*
 * Copyright (c) 2016 by Thomas Zuberbühler
 */

package ch.geomo.tramaps.grid;

import ch.geomo.tramaps.util.GeomUtil;
import ch.geomo.tramaps.util.point.NodePoint;
import ch.geomo.tramaps.graph.GridEdgeOrderComparator;
import ch.geomo.tramaps.graph.NodeLabel;
import ch.geomo.tramaps.util.CollectionUtil;
import ch.geomo.tramaps.util.tuple.Tuple;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.Point;
import org.apache.commons.lang3.tuple.Pair;
import org.geotools.geometry.jts.JTSFactoryFinder;
import org.geotools.graph.structure.basic.BasicNode;
import org.geotools.referencing.CRS;
import org.geotools.referencing.GeodeticCalculator;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.opengis.referencing.FactoryException;

import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class GridNode extends BasicNode implements NodePoint {

    @SuppressWarnings("unused")
    private static final Logger logger = Logger.getLogger(GridNode.class.getSimpleName());

    private int type;
    private NodeLabel label;

    /**
     * Metro Map Builder iteration number of the last update/change.
     */
    private long version = 0;

    private List<Point> history;

    private Set<Tuple<GridEdge>> edgePairs;
    private Map<Tuple<GridEdge>, Double> edgeAngles;

    public GridNode() {
        history = new ArrayList<>();
    }

    public long getVersion() {
        return version;
    }

    @NotNull
    public List<Point> getHistory() {
        return history;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public Point getPoint() {
        return (Point) getObject();
    }

    public double getX() {
        return getPoint().getX();
    }

    public double getY() {
        return getPoint().getY();
    }

    public boolean hasEasyAngle() {
        return getEdgeAngles().values().stream()
                .findFirst()
                .map(angle -> !(Math.toDegrees(angle) < 160 && Math.toDegrees(angle) > 200))
                .orElse(true);
    }

    public boolean isMoveable() {
        return hasDegree(2) && (hasEasyAngle() || getRelatedStream()
                .filter(node -> !node.equals(this))
                .anyMatch(GridNode::hasEasyAngle));
    }

    public void moveTo(Coordinate coordinate, boolean overwriteMoveable) {
        if (overwriteMoveable || isMoveable()) {
            setObject(JTSFactoryFinder.getGeometryFactory().createPoint(coordinate));
            getEdges().forEach(GridEdge::updateLineString);
            version++;
        }
    }

    public void moveTo(double x, double y, boolean overwriteMoveable) {
        moveTo(new Coordinate(x, y), overwriteMoveable);
    }

    public void moveTo(double x, double y) {
        moveTo(new Coordinate(x, y), false);
    }

    public void moveTo(NodePoint point) {
        moveTo(point.getCoordinate(), false);
    }

    public void moveTo(NodePoint point, boolean overwriteMoveable) {
        moveTo(point.getCoordinate(), overwriteMoveable);
    }

    public void simplifyEdges() {
        if (!hasDegree(2)) {
            getRelatedStream()
                    .filter(node -> !hasDegree(2))
                    .forEach(node -> {
                        List<GridNode> nodes = new ArrayList<>();
                        GridNode current = node;
                        nodes.add(this);
                        nodes.add(current);
                        while (current != null) {
                            if (current.hasDegree(2)) {
                                current = current.getRelatedStream()
                                        .filter(n -> !nodes.contains(n))
                                        .findFirst()
                                        .orElse(null);
                                if (current != null) {
                                    nodes.add(current);
                                }
                            }
                            else {
                                nodes.add(current);
                                current = null;
                            }
                        }
                        GridNode.simplify(nodes);
                    });
        }
    }

    public static void simplify(List<GridNode> nodes) {

        if (nodes.size() <= 2) {
            return;
        }

        List<List<GridNode>> segments = new ArrayList<>();

        GridNode f = nodes.get(0);
        GridNode l = nodes.get(nodes.size() - 1);

        double distanceX = Math.abs(f.getX() - l.getX());
        double distanceY = Math.abs(f.getY() - l.getY());

        if (!f.hasDegree(1) && !l.hasDegree(1) && distanceX > 750 && distanceY > 750) {

            List<GridNode> firstPart = new ArrayList<>();
            List<GridNode> secondPart = new ArrayList<>();
            List<GridNode> thirdPart = new ArrayList<>();

            if (distanceX > distanceY) {
                double range = distanceX / 3;
                nodes.forEach(node -> {
                    double diff = Math.abs(node.getX() - f.getX());
                    int segmentIndex = (int) (diff / range);
                    if (segmentIndex == 0) {
//                        node.setX(f.getX());
                        firstPart.add(node);
                    }
                    else if (segmentIndex == 1) {
                        secondPart.add(node);
                    }
                    else {
//                        node.setX(l.getX());
                        thirdPart.add(node);
                    }
                });
            }
            else {
                double range = distanceY / 3;
                nodes.forEach(node -> {
                    double diff = Math.abs(node.getY() - f.getY());
                    int segmentIndex = (int) (diff / range);
                    if (segmentIndex == 0) {
//                        node.setY(f.getY());
                        firstPart.add(node);
                    }
                    else if (segmentIndex == 1) {
                        secondPart.add(node);
                    }
                    else {
//                        node.setY(l.getY());
                        thirdPart.add(node);
                    }
                });
            }

            segments.add(firstPart);
            segments.add(secondPart);
            segments.add(thirdPart);

        }
        else {
            segments.add(nodes);
        }

        for (List<GridNode> segment : segments) {

            if (segment.isEmpty()) {
                continue;
            }

            GridNode firstNode = segment.get(0);
            GridNode lastNode = segment.get(segment.size() - 1);

            LineString lineString = JTSFactoryFinder.getGeometryFactory().createLineString(new Coordinate[]{
                    firstNode.getCoordinate(),
                    lastNode.getCoordinate()
            });

            GeodeticCalculator calculator;
            try {
                calculator = new GeodeticCalculator(CRS.decode("EPSG:4326"));
            }
            catch (FactoryException e) {
                System.out.println(e);
                return;
            }

            LinkedList<Coordinate> coordinates = new LinkedList<>();
            coordinates.add(firstNode.getCoordinate());

            double segmentLength = lineString.getLength() / segment.size() - 2;

            Coordinate to = lastNode.getCoordinate();

            for (int i = 0; i < segment.size() - 2; i++) {

                Coordinate from = coordinates.getLast();

//                calculator.setStartingGeographicPoint(from.x, from.y);
//                calculator.setDestinationGeographicPoint(to.x, to.y);
//
//                double length = calculator.getOrthodromicDistance();

                // EPSG:21781
                double length = to.distance(from);

                double ratio = segmentLength / length;
                double dx = to.x - from.x;
                double dy = to.y - from.y;

                coordinates.add(new Coordinate(from.x + (dx * ratio), from.y + (dy * ratio)));

            }

            for (int i = 0; i < segment.size() - 2; i++) {
                segment.get(i).moveTo(coordinates.get(i), false);
            }

        }

    }

    /**
     * Checks the edge ordering of current node did not change. (Node Movement Rule: Preservation of Edge Ordering)
     */
    public boolean hasChangedEdgeOrderingWhenMovingTo(@NotNull NodePoint point) {

        for (GridEdge edge : getEdges()) {

            GridNode node = edge.getOtherNode(this);

            List<LineString> lines = node.getEdges().stream()
                    .map(GridEdge::getLineString)
                    .collect(Collectors.toList());

            LineString test = GeomUtil.createLineString(point.getCoordinate(), node.getCoordinate());
            lines.add(test);

            int edgeIndex = lines.indexOf(edge.getLineString());
            int testIndex = lines.indexOf(test);

            if (edgeIndex - testIndex > 1) {
                return true;
            }

        }

        return false;

    }

    @Override
    public Coordinate getCoordinate() {
        return getPoint().getCoordinate();
    }

    @SuppressWarnings("unused")
    public void setLabelName(@Nullable String name) {
        if (this.label == null) {
            this.label = new NodeLabel(name);
        }
        else {
            this.label.setName(name);
        }
    }

    @SuppressWarnings("unused")
    public void setLabel(@Nullable NodeLabel label) {
        this.label = label;
    }

    @Nullable
    public NodeLabel getLabel() {
        return label;
    }

    public void add(@NotNull GridEdge e) {
        super.add(e);
        clearCaches();
    }

    @SuppressWarnings("unused")
    public void remove(@NotNull GridEdge e) {
        super.remove(e);
        clearCaches();
    }

    @NotNull
    @Override
    @SuppressWarnings("unchecked")
    public List<GridEdge> getEdges() {
        return super.getEdges();
    }

    @NotNull
    @SuppressWarnings("unused")
    public Optional<GridEdge> getEdge(GridNode other) {
        return Optional.ofNullable(super.getEdge(other)).map(edge -> (GridEdge) edge);
    }

    public boolean hasDegree(int degree) {
        return getDegree() == degree;
    }

    @NotNull
    @SuppressWarnings({"unchecked", "unused"})
    public List<GridEdge> getEdges(GridNode other) {
        return super.getEdges(other);
    }

    @NotNull
    @Override
    @SuppressWarnings("unchecked")
    public Iterator<GridNode> getRelated() {
        return super.getRelated();
    }

    @NotNull
    public Stream<GridNode> getRelatedStream() {
        Iterable<GridNode> iterable = this::getRelated;
        return StreamSupport.stream(iterable.spliterator(), false);
    }

    /**
     * Clears all caches.
     */
    public void clearCaches() {
        this.edgePairs = null;
        this.edgeAngles = null;
    }

    @NotNull
    public Set<Tuple<GridEdge>> getAdjacentEdgePairs() {
        if (edgePairs == null) {
            // edges are already sorted
            edgePairs = GridNode.createAdjacentEdgePairs(getEdges(), this, true);
        }
        return edgePairs;
    }

    @NotNull
    public Map<Tuple<GridEdge>, Double> getEdgeAngles() {
        if (edgeAngles == null) {
            edgeAngles = getAdjacentEdgePairs().stream()
                    .map(pair -> {
                        double angle = calculateAngleBetween(pair.getFirst().getOtherNode(this), pair.getSecond().getOtherNode(this));
                        return Pair.of(pair, angle);
                    })
                    .collect(Collectors.toMap(Pair::getLeft, Pair::getRight));
        }
        return edgeAngles;
    }

    @NotNull
    public static Set<Tuple<GridEdge>> createAdjacentEdgePairs(@NotNull List<GridEdge> edges, @NotNull GridNode originPoint, boolean sort) {
        List<GridEdge> sortedEdges = new ArrayList<>(edges);
        if (sort) {
            sortedEdges.sort(new GridEdgeOrderComparator(originPoint));
        }
        return Tuple.from(CollectionUtil.makePairs(sortedEdges, true, true));
    }

    @NotNull
    @SuppressWarnings("unused")
    public Set<GridNode> getAdjacentNodes() {
        return getRelatedStream().collect(Collectors.toSet());
    }

    @Override
    public String toString() {
        return Optional.ofNullable(getLabel())
                .map(NodeLabel::getName)
                .orElse(getPoint().toString());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getX(), getY(), getLabel());
    }

    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof GridNode)) {
            return false;
        }
        GridNode node = (GridNode) obj;
        return Objects.equals(getPoint().getX(), node.getPoint().getX())
                && Objects.equals(getPoint().getY(), node.getPoint().getY())
                && Objects.equals(getLabel(), getLabel());
    }

    @SuppressWarnings("unused")
    public void setX(double x, boolean overwriteMoveable) {
        moveTo(new Coordinate(x, getPoint().getY()), overwriteMoveable);
    }

    @SuppressWarnings("unused")
    public void setY(double y, boolean overwriteMoveable) {
        moveTo(new Coordinate(getPoint().getX(), y), overwriteMoveable);
    }

    @SuppressWarnings("unused")
    public void setX(double x) {
        moveTo(x, getPoint().getY());
    }

    @SuppressWarnings("unused")
    public void setY(double y) {
        moveTo(getPoint().getX(), y);
    }

}
