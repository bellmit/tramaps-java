/*
 * Copyright (c) 2016 Thomas Zuberbuehler. All rights reserved.
 */

package ch.geomo.util.point;

/**
 * Mutable implementation of {@link NodePoint}.
 */
public class MutableNodePoint extends AbstractNodePoint {

    public MutableNodePoint(double x, double y) {
        super(x, y);
    }

    public MutableNodePoint(NodePoint point) {
        super(point.getX(), point.getY());
    }

    public void setX(double x) {
        super.x = x;
    }

    public void setY(double y) {
        super.y = y;
    }

    public void moveX(int moveDistance) {
        setX(getX() + moveDistance);
    }

    public void moveY(int moveDistance) {
        setY(getY() + moveDistance);
    }

}
