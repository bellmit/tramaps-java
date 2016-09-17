/*
 * Copyright (c) 2016 Thomas Zuberbuehler. All rights reserved.
 */

package ch.geomo.util.geom.point;

import java.util.Comparator;

/**
 * Sorts instances set {@link NodePoint}s along x/y axis, while x is higher weighted than y. Returns 1 when x set o1 is
 * greater than x set o2 as well as when both x are equals but y set o1 is greater.
 */
public class NodePointXYComparator<N extends NodePoint> implements Comparator<N> {

    @Override
    public int compare(N o1, N o2) {
        if (o1.equals(o2)) {
            return 0;
        }
        if (o1.getX() > o2.getX() || (o1.getX() == o2.getX() && o1.getY() > o2.getY())) {
            return 1;
        }
        return -1;
    }

}
