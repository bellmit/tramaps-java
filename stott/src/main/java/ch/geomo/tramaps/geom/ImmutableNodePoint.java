/*
 * Copyright (c) 2016 by Thomas Zuberbühler
 */

package ch.geomo.tramaps.geom;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;
import org.geotools.geometry.jts.JTSFactoryFinder;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class ImmutableNodePoint implements NodePoint {

    private double x;
    private double y;

    public ImmutableNodePoint(double x, double y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public double getX() {
        return x;
    }

    @Override
    public double getY() {
        return y;
    }

    @Override
    public double calculateDistanceTo(double x, double y) {
        return Math.sqrt(Math.pow(getX() - x, 2) + Math.pow(getY() - y, 2));
    }

    @Override
    public Coordinate getCoordinate() {
        return new Coordinate(x, y);
    }

    @Override
    public Point getPoint() {
        return JTSFactoryFinder.getGeometryFactory().createPoint(getCoordinate());
    }

    /**
     * Calculates the distance between current point and a x/y coordinate.
     *
     * @see NodePoint#calculateDistanceTo(NodePoint)
     */
    public double calculateDistanceTo(long x, long y) {
        return Math.sqrt(Math.pow(getX() - x, 2) + Math.pow(getY() - y, 2));
    }

    @Override
    public double calculateAngleBetween(@NotNull NodePoint p1, @NotNull NodePoint p2) {
        double angle1 = Math.atan2(p1.getY() - getY(), p1.getX() - getX());
        double angle2 = Math.atan2(p2.getY() - getY(), p2.getX() - getX());
        return angle1 - angle2;
    }

    @Override
    public boolean equals(Object obj) {

        if (obj == null || !(obj instanceof ImmutableNodePoint)) {
            return false;
        }

        ImmutableNodePoint point = (ImmutableNodePoint) obj;
        return point.getY() == getY() && point.getX() == getY();

    }

    @Override
    public int hashCode() {
        return Objects.hash(getX(), getY());
    }

    @Override
    public String toString() {
        return getX() + "/" + getY();
    }

}
