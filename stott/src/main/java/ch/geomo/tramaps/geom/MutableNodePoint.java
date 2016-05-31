/*
 * Copyright (c) 2016 by Thomas Zuberbühler
 */

package ch.geomo.tramaps.geom;

/**
 * Mutable implementation of {@link NodePoint}.
 */
public class MutableNodePoint extends ImmutableNodePoint {

    public MutableNodePoint(double x, double y) {
        super(x, y);
    }

    @Override
    public void setX(double x) {
        super.setX(x);
    }

    @Override
    public void setY(double y) {
        super.setY(y);
    }

}
