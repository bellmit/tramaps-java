/*
 * Copyright (c) 2016 Thomas Zuberbuehler. All rights reserved.
 */

package ch.geomo.tramaps.conflict;

import java.util.Comparator;

public class ConflictComparator implements Comparator<Conflict> {

    @Override
    public int compare(Conflict o1, Conflict o2) {

        double length1a = o1.getBestDisplaceLength();
        double length2a = o2.getBestDisplaceLength();

        if (length1a != length2a) {
            return Double.compare(length1a, length2a);
        }

        double length1b = o1.getDisplaceVector().length();
        double length2b = o2.getDisplaceVector().length();

        if (length1b != length2b) {
            return Double.compare(length1b, length2b);
        }

        int rank1 = o1.getConflictType().getConflictRank();
        int rank2 = o2.getConflictType().getConflictRank();

        if (rank1 != rank2) {
            return Integer.compare(rank1, rank2);
        }

        double x1 = o1.getDisplaceVector().getX();
        double x2 = o2.getDisplaceVector().getX();

        if (x1 != x2) {
            return Double.compare(x1, x2);
        }

        double y1 = o1.getDisplaceVector().getY();
        double y2 = o2.getDisplaceVector().getY();

        if (y1 != y2) {
            return Double.compare(y1, y2);
        }

        // Loggers.warning(this, "Conflicts are equals. Output might not be reproduceable.");
        return 0;

    }

}
