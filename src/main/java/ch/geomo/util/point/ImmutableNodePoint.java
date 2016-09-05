/*
 * Copyright (c) 2016 Thomas Zuberbuehler. All rights reserved.
 */

package ch.geomo.util.point;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Point;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import static ch.geomo.tramaps.geom.util.GeomUtil.getGeomUtil;

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

    @NotNull
    @Override
    public Coordinate toCoordinate() {
        return new Coordinate(x, y);
    }

    @NotNull
    @Override
    public Point toPoint() {
        return getGeomUtil().createPoint(toCoordinate());
    }

    @Override
    public boolean equals(Object obj) {

        if (obj == null || !(obj instanceof NodePoint)) {
            return false;
        }

        NodePoint point = (NodePoint) obj;
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
