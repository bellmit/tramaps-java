package ch.geomo.tramaps.graph;

import ch.geomo.tramaps.map.StationSignature;
import ch.geomo.tramaps.util.point.NodePoint;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Point;
import org.geotools.geometry.jts.JTSFactoryFinder;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.Observable;
import java.util.Set;

public class Node extends Observable implements GraphElement, NodePoint {

    private Point point;

    private StationSignature signature;

    public Node(Point point) {
        this.point = point;
        this.signature = new StationSignature(this);
    }

    @NotNull
    public Set<Edge> getAdjacentEdges() {
        return Collections.emptySet();
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

}
