package ch.geomo.tramaps.graph;

import ch.geomo.tramaps.map.StationSignature;
import ch.geomo.util.point.NodePoint;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Point;
import org.geotools.geometry.jts.JTSFactoryFinder;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.HashSet;
import java.util.Observable;
import java.util.Set;

public class Node extends Observable implements GraphElement, NodePoint {

    private Point point;

    private final StationSignature signature;
    private final Set<Edge> adjacentEdges;

    public Node(Point point) {
        this.point = point;
        this.adjacentEdges = new HashSet<>();
        this.signature = new StationSignature(this);
    }

    @NotNull
    public Set<Edge> getAdjacentEdges() {
        return this.adjacentEdges;
    }

    public void addAdjacentEdge(Edge edge) {
        if (this.equals(edge.getNodeA()) || this.equals(edge.getNodeB())) {
            this.adjacentEdges.add(edge);
        }
    }

    @NotNull
    public Point getPoint() {
        return this.point;
    }

    @NotNull
    @Override
    public Geometry getGeometry() {
        return this.point;
    }

    @Override
    public boolean isAdjacent(Edge edge) {
        return this.getAdjacentEdges().contains(edge);
    }

    @Override
    public boolean isAdjacent(Node node) {
        return this.getAdjacentEdges().stream()
                .anyMatch(edge -> edge.isAdjacent(node));
    }

    public StationSignature getSignature() {
        return signature;
    }

    @Override
    public double getX() {
        return this.getCoordinate().x;
    }

    @Override
    public double getY() {
        return this.getCoordinate().y;
    }

    @NotNull
    public Coordinate getCoordinate() {
        return this.point.getCoordinate();
    }

    public void setPoint(@NotNull Point point) {
        this.point = point;
        this.notifyObservers();
    }

    public void setCoordinate(@NotNull Coordinate coordinate) {
        this.point = JTSFactoryFinder.getGeometryFactory().createPoint(coordinate);
        this.setChanged();
        this.notifyObservers();
    }

    public void setCoordinate(double x, double y) {
        this.setCoordinate(new Coordinate(x, y));
    }

    public void setX(double x) {
        this.setCoordinate(new Coordinate(x, this.getCoordinate().y));
    }

    public void setY(double y) {
        this.setCoordinate(new Coordinate(this.getCoordinate().x, y));
    }

    @Override
    public String toString() {
        return point.toString();
    }
}
