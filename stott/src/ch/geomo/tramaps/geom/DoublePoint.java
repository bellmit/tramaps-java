/*
 * Copyright (c) 2016 by Thomas Zuberbühler
 */

package ch.geomo.tramaps.geom;

import org.jetbrains.annotations.NotNull;

public class DoublePoint implements Point<Double> {

    private double x;
    private double y;

    public DoublePoint(double x, double y) {
        this.x = x;
        this.y = y;
    }

    @NotNull
    @Override
    public Double getX() {
        return x;
    }

    @NotNull
    @Override
    public Double getY() {
        return y;
    }

    @Override
    public double calculateDistanceTo(@NotNull Point<Double> point) {
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
    public double calculateAngleBetween(@NotNull Point<Double> p1, @NotNull Point<Double> p2) {
        double angle1 = Math.atan2(p1.getY() - getY(), p1.getX() - getX());
        double angle2 = Math.atan2(p2.getY() - getY(), p2.getX() - getX());
        return angle1 - angle2;
    }

}
