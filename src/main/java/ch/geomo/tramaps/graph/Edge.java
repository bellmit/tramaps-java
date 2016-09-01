package ch.geomo.tramaps.graph;

import ch.geomo.tramaps.geo.util.GeomUtil;
import ch.geomo.tramaps.graph.util.AnyDirection;
import ch.geomo.tramaps.graph.util.Direction;
import ch.geomo.tramaps.graph.util.OctilinearDirection;
import ch.geomo.util.pair.Pair;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.Point;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class Edge extends Observable implements Observer, GraphElement {

    private Node nodeA;
    private Node nodeB;
    private Pair<Node> nodePair;

    private LineString lineString;
    private List<Point> vertices;

    private Set<Route> routes;

    private Direction direction;

    public Edge(@NotNull Node nodeA, @NotNull Node nodeB) {
        this.nodeA = nodeA;
        this.nodeB = nodeB;
        nodeA.addAdjacentEdge(this);
        nodeB.addAdjacentEdge(this);
        routes = new HashSet<>();
        vertices = new ArrayList<>();
        nodePair = Pair.of(nodeA, nodeB);
        updateLineString();
    }

    public double getEdgeWidth(double routeMargin) {
        double width = getRoutes().stream()
                .mapToDouble(Route::getLineWidth)
                .sum();
        return width + routeMargin * (getRoutes().size() - 2);
    }

    @NotNull
    public Node getNodeA() {
        return nodeA;
    }

    @NotNull
    public Node getNodeB() {
        return nodeB;
    }

    @NotNull
    public List<Point> getVertices() {
        return vertices;
    }

    private void updateLineString() {
        lineString = GeomUtil.createLineString(this.getNodeA(), this.getNodeB());
        direction = AnyDirection.fromAngle(Math.ceil(GeomUtil.getAngleToXAxisAsDegree(lineString)));
        setChanged();
        notifyObservers();
    }

    public void addRoutes(@NotNull Collection<Route> routes) {
        this.routes.addAll(routes);
        setChanged();
        notifyObservers();
    }

    public void addRoutes(@NotNull Route... routes) {
        addRoutes(Arrays.asList(routes));
    }

    /**
     * @return an unmodifiable {@link Set} with all routes
     */
    @NotNull
    public Set<Route> getRoutes() {
        // unmodifiable in order to avoid side effects
        return Collections.unmodifiableSet(routes);
    }

    /**
     * @throws NoSuchElementException if given node is not an end node of this edge
     */
    @NotNull
    public Node getOtherNode(@NotNull Node node) {
        return nodePair.getOtherValue(node);
    }

    @Override
    @Contract("null->false")
    public boolean isAdjacent(@Nullable Edge edge) {
        return edge != null && (getNodeA().getAdjacentEdges().contains(edge) || getNodeB().getAdjacentEdges().contains(edge));
    }

    @Override
    @Contract("null->false")
    public boolean isAdjacent(@Nullable Node node) {
        return nodeA.equals(node) || nodeB.equals(node);
    }

    @NotNull
    public LineString getLineString() {
        return lineString;
    }

    @NotNull
    @Override
    public Geometry getGeometry() {
        return lineString;
    }

    @Override
    public void update(Observable o, Object arg) {
        updateLineString();
    }

    @NotNull
    @Override
    public String toString() {
        return lineString.toString();
    }

    public boolean isNonOctilinear() {
        return !OctilinearDirection.isOctilinear(direction);
    }

    /**
     * @return true if vertical to x-axis
     */
    public boolean isVertical() {
        return direction.isVertical();
    }

    /**
     * @return true if horizontal to x-axis
     */
    public boolean isHorizontal() {
        return direction.isHorizontal();
    }

    /**
     * @return true if neither vertical nor horizontal to x-axis but octliniear
     */
    @SuppressWarnings("unused")
    public boolean isDiagonal() {
        return direction.isDiagonal();
    }

    @NotNull
    @SuppressWarnings("unused")
    public Direction getDirection() {
        return direction;
    }

    @SuppressWarnings("unused")
    public double getAngle() {
        return direction.getAngle();
    }

    @Override
    @Contract("->true")
    public boolean isEdge() {
        return true;
    }

    @Override
    @Contract("->false")
    public boolean isNode() {
        return false;
    }

    @Contract("null->true")
    public boolean isNotEquals(@Nullable Edge edge) {
        return !this.equals(edge);
    }

    @Override
    public boolean equals(Object obj) {
        // TODO
        return super.equals(obj);
    }

    @Override
    public int hashCode() {
        // TODO
        return super.hashCode();
    }

    public void destroy(@Nullable Graph graph) {
        getNodeA().removeAdjacentEdge(this, graph);
        getNodeB().removeAdjacentEdge(this, graph);
        deleteObservers();
    }

}
