/*
 * Copyright (c) 2016 Thomas Zuberbuehler. All rights reserved.
 */

package ch.geomo.tramaps.conflict;

import java.util.Comparator;

/**
 * Compares two conflicts in order to prioritise them for conflict solving.
 */
public class ConflictComparator implements Comparator<Conflict> {

    @Override
    public int compare(Conflict o1, Conflict o2) {

        int rank1 = o1.getConflictType().getConflictRank();
        int rank2 = o2.getConflictType().getConflictRank();
        if (rank1 != rank2) {
            return Integer.compare(rank1, rank2);
        }

        double length1a = o1.getBestDisplaceDistance();
        double length2a = o2.getBestDisplaceDistance();
        if (length1a != length2a) {
            return Double.compare(length1a, length2a);
        }

        double length1b = o1.getDisplaceVector().length();
        double length2b = o2.getDisplaceVector().length();
        if (length1b != length2b) {
            return Double.compare(length1b, length2b);
        }

        double x1 = o1.getDisplaceDistanceAlongX();
        double x2 = o2.getDisplaceDistanceAlongX();
        if (x1 != x2) {
            return Double.compare(x1, x2);
        }

        double y1 = o1.getDisplaceDistanceAlongY();
        double y2 = o2.getDisplaceDistanceAlongY();
        if (y1 != y2) {
            return Double.compare(y1, y2);
        }

        // fix/analysis required: after displacing nodes more than one OctilinearConflict
        // may occur and must be solved. however, these conflict has an equal distance
        // and displace vector to be solved. therefore they have currently a randomised
        // sorting among themselves
        return 0;

    }

}
