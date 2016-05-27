/*
 * Copyright (c) 2016 by Thomas Zuberbühler
 */

package ch.geomo.tramaps.graph;

import ch.geomo.tramaps.geom.NodePoint;

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

    public static Quadrant getQuadrant(NodePoint originPoint, NodePoint point) {

        if (point.getX() > originPoint.getX()) {
            if (point.getY() > originPoint.getY()) {
                return Quadrant.FIRST;
            }
            return Quadrant.FOURTH;
        }

        if (point.getY() > originPoint.getY()) {
            return Quadrant.SECOND;
        }
        return Quadrant.THIRD;

    }

}
