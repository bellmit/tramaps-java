/*
 * Copyright (c) 2016-2018 Thomas Zuberbuehler. All rights reserved.
 */

package ch.geomo.util.geom.point;

import ch.geomo.util.geom.GeomUtil;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Point;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public abstract class AbstractNodePoint implements NodePoint {

    protected double x;
    protected double y;

    public AbstractNodePoint(double x, double y) {
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

    @NotNull
    @Override
    public Coordinate toCoordinate() {
        return GeomUtil.createCoordinate(x, y);
    }

    @NotNull
    @Override
    public Point toPoint() {
        return GeomUtil.createPoint(x, y);
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof NodePoint
                && getY() == ((NodePoint) obj).getY()
                && getX() == ((NodePoint) obj).getY();
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
