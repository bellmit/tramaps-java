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

/**
 * Immutable implementation of {@link NodePoint}.
 */
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

    protected void setX(double x) {
        this.x = x;
    }

    @Override
    public double getY() {
        return y;
    }

    protected void setY(double y) {
        this.y = y;
    }

    @Override
    public Coordinate getCoordinate() {
        return new Coordinate(x, y);
    }

    @Override
    public Point getPoint() {
        return JTSFactoryFinder.getGeometryFactory().createPoint(getCoordinate());
    }

    @Override
    public boolean equals(Object obj) {

        if (obj == null || !(obj instanceof ImmutableNodePoint)) {
            return false;
        }

        ImmutableNodePoint point = (ImmutableNodePoint) obj;
        return point.getY() == getY() && point.getX() == getX();

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
