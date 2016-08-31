package ch.geomo.tramaps.graph;

import ch.geomo.tramaps.map.SquareStationSignature;
import ch.geomo.tramaps.map.NodeSignature;
import ch.geomo.util.point.NodePoint;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Point;
import org.geotools.geometry.jts.JTSFactoryFinder;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Observable;
import java.util.Set;
import java.util.function.Function;

public class Node extends Observable implements GraphElement, NodePoint {

    private Point point;

    private final NodeSignature signature;
    private final Set<Edge> adjacentEdges;

    /**
     * Creates a new instance of {@link Node} using a {@link SquareStationSignature}
     * instance.
     *
     * @see Node#Node(Point, Function)
     */
    public Node(@NotNull Point point) {
        this(point, SquareStationSignature::new);
    }

    /**
     * Creates a new instance of {@link Node} using a custom {@link NodeSignature}
     * instance.
     */
    public Node(@NotNull Point point, @NotNull Function<Node, NodeSignature> stationSignatureFactory) {
        this.point = point;
        adjacentEdges = new HashSet<>();
        signature = stationSignatureFactory.apply(this);
    }

    /**
     * @return all adjacent edges
     */
    @NotNull
    public Set<Edge> getAdjacentEdges() {
        return adjacentEdges;
    }

    /**
     * Adds a new adjacent edge but ignores given edge if neither node A nor
     * node B is equals to this instance.
     */
    public void addAdjacentEdge(@NotNull Edge edge) {
        if (this.equals(edge.getNodeA()) || this.equals(edge.getNodeB())) {
            adjacentEdges.add(edge);
        }
    }

    /**
     * @return the nodes point
     */
    @NotNull
    public Point getPoint() {
        return point;
    }

    /**
     * @see #getPoint()
     */
    @NotNull
    @Override
    public Geometry getGeometry() {
        return getPoint();
    }

    @Override
    @Contract("null->false")
    public boolean isAdjacent(@Nullable Edge edge) {
        return edge != null && getAdjacentEdges().contains(edge);
    }

    @Override
    @Contract("null->false")
    public boolean isAdjacent(@Nullable Node node) {
        return getAdjacentEdges().stream()
                .anyMatch(edge -> edge.isAdjacent(node));
    }

    @NotNull
    public NodeSignature getSignature() {
        return signature;
    }

    @Override
    public double getX() {
        return getCoordinate().x;
    }

    @Override
    public double getY() {
        return getCoordinate().y;
    }

    @NotNull
    public Coordinate getCoordinate() {
        return point.getCoordinate();
    }

    public void setPoint(@NotNull Point point) {
        this.point = point;
        notifyObservers();
    }

    public void setCoordinate(@NotNull Coordinate coordinate) {
        this.point = JTSFactoryFinder.getGeometryFactory().createPoint(coordinate);
        setChanged();
        notifyObservers();
    }

    public void setCoordinate(double x, double y) {
        setCoordinate(new Coordinate(x, y));
    }

    public void setX(double x) {
        setCoordinate(new Coordinate(x, this.getCoordinate().y));
    }

    public void setY(double y) {
        setCoordinate(new Coordinate(this.getCoordinate().x, y));
    }

    @Override
    public String toString() {
        return point.toString();
    }

    @Override
    public boolean isEdge() {
        return false;
    }

    @Override
    public boolean isNode() {
        return true;
    }

}
