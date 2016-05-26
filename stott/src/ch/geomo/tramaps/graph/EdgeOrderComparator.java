/*
 * Copyright (c) 2016 by Thomas Zuberbühler
 */

package ch.geomo.tramaps.graph;

import org.jetbrains.annotations.NotNull;

import java.util.Comparator;

/**
 * Compares two edges in order to sort edges counter-clockwise using the quadrants of the Cartesian coordinate
 * system.
 */
public class EdgeOrderComparator<N extends Node<Edge<N>>, E extends Edge<N>> implements Comparator<E> {

    private N originNode;

    public EdgeOrderComparator(@NotNull N originNode) {
        this.originNode = originNode;
    }

    @Override
    public int compare(E e1, E e2) {

        if (!e1.isAdjacent(e2)) {
            return -1;
        }

        N n1 = e1.getOppositeNode(originNode);
        N n2 = e2.getOppositeNode(originNode);

        if (n1.equals(n2)) {
            return 0;
        }

        Quadrant q1 = n1.getQuadrant(originNode);
        Quadrant q2 = n2.getQuadrant(originNode);

        if (q1 == q2) {
            switch (q1) {
                case FIRST:
                case SECOND:
                    return (n1.getY() - n2.getY()) > 0 ? 1 : -1;
                case THIRD:
                case FOURTH:
                    return (n2.getY() - n1.getY()) > 0 ? 1 : -1;
            }
        }

        if (q1.isBefore(q2)) {
            return -1;
        }
        return 1;

    }

}
