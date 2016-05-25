/*
 * Copyright (c) 2016 by Thomas Zuberbühler
 */

package ch.geomo.tramaps.graph;

import ch.geomo.tramaps.geom.Point;
import ch.geomo.tramaps.util.NumberUtil;

/**
 * Quadrant of a Cartesian coordinate system.
 */
public enum Quadrant {

    FIRST(1),
    SECOND(2),
    THIRD(3),
    FOURTH(4);

    private int number;

    Quadrant(int number) {
        this.number = number;
    }

    /**
     * Gets the number of the quadrant.
     */
    public int getNumber() {
        return number;
    }

    /**
     * Returns true if the given quadrant's number is smaller then the current quadrant's number.
     */
    public boolean isBefore(Quadrant quadrant) {
        return getNumber() < quadrant.getNumber();
    }

    public static <N extends Number & Comparable<N>>Quadrant getQuadrant(Point<N> originPoint, Point<N> point) {

        if (NumberUtil.isGreaterThan(point.getX(), originPoint.getX())) {
            if (NumberUtil.isGreaterThan(point.getY(), originPoint.getY())) {
                return Quadrant.FIRST;
            }
            return Quadrant.FOURTH;
        }

        if (NumberUtil.isGreaterThan(point.getY(), originPoint.getY())) {
            return Quadrant.SECOND;
        }
        return Quadrant.THIRD;

    }

}
