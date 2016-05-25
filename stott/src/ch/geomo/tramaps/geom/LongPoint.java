/*
 * Copyright (c) 2016 by Thomas Zuberbühler
 */

package ch.geomo.tramaps.geom;

import ch.geomo.tramaps.util.CollectionUtil;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class LongPoint implements Point<Long> {

    private static final Map<String, LongPoint> cache = new HashMap<>();

    private long x;
    private long y;

    public LongPoint(long x, long y) {
        this.x = x;
        this.y = y;
    }

    @NotNull
    @Override
    public Long getX() {
        return x;
    }

    @NotNull
    @Override
    public Long getY() {
        return y;
    }

    @Override
    public double calculateDistanceTo(@NotNull Point<Long> point) {
        return Math.sqrt(Math.pow(getX() - point.getX(), 2) + Math.pow(getY() - point.getY(), 2));
    }

    /**
     * Calculates the distance between current point and a x/y coordinate.
     *
     * @see Point#calculateDistanceTo(Point)
     */
    public double calculateDistanceTo(long x, long y) {
        return Math.sqrt(Math.pow(getX() - x, 2) + Math.pow(getY() - y, 2));
    }

    /**
     * Calculates the distance between current point and a x/y coordinate.
     *
     * @see Point#calculateDistanceTo(Point)
     */
    public double calculateDistanceTo(double x, double y) {
        return Math.sqrt(Math.pow(getX() - x, 2) + Math.pow(getY() - y, 2));
    }

    @Override
    public double calculateAngleBetween(@NotNull Point<Long> p1, @NotNull Point<Long> p2) {
        double angle1 = Math.atan2(p1.getY() - getY(), p1.getX() - getX());
        double angle2 = Math.atan2(p2.getY() - getY(), p2.getX() - getX());
        return angle1 - angle2;
    }

    @Override
    public boolean equals(Object obj) {

        if (obj == null || !(obj instanceof LongPoint)) {
            return false;
        }

        LongPoint point = (LongPoint)obj;
        return point.getY().equals(getY()) && point.getX().equals(getY());

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
