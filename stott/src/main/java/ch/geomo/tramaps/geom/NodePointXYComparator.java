/*
 * Copyright (c) 2016 by Thomas Zuberbühler
 */

package ch.geomo.tramaps.geom;

import java.util.Comparator;

/**
 * Sorts instances of {@link NodePoint}s along x/y axis, while x is higher weighted than y. Returns 1 when x of o1 is
 * greater than x of o2 as well as when both x are equals but y of o1 is greater.
 */
public class NodePointXYComparator implements Comparator<NodePoint> {

    @Override
    public int compare(NodePoint o1, NodePoint o2) {
        if (o1.equals(o2)) {
            return 0;
        }
        if (o1.getX() > o2.getX() || (o1.getX() == o2.getX() && o1.getY() > o2.getY())) {
            return 1;
        }
        return -1;
    }

}
