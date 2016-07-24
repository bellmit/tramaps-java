/*
 * Copyright (c) 2016 by Thomas Zuberbühler
 */

package ch.geomo.tramaps.util.point;

/**
 * Mutable implementation of {@link NodePoint}.
 */
public class MutableNodePoint extends ImmutableNodePoint {

    public MutableNodePoint(double x, double y) {
        super(x, y);
    }

    public MutableNodePoint(NodePoint point) {
        super(point.getX(), point.getY());
    }

    @Override
    public void setX(double x) {
        super.setX(x);
    }

    @Override
    public void setY(double y) {
        super.setY(y);
    }

    public void moveX(int moveDistance) {
        setX(getX() + moveDistance);
    }

    public void moveY(int moveDistance) {
        setY(getY() + moveDistance);
    }

}
