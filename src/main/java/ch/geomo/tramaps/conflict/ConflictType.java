/*
 * Copyright (c) 2016 Thomas Zuberbuehler. All rights reserved.
 */

package ch.geomo.tramaps.conflict;

public enum ConflictType {

    OCTILINEAR(10),
    ADJACENT_NODE_NODE_DIAGONAL(5),
    ADJACENT_NODE_NODE(5),
    NODE_NODE(5),
    NODE_EDGE(5),
    EDGE_EDGE(5);

    private final int conflictRank;

    ConflictType(int conflictRank) {
        this.conflictRank = conflictRank;
    }

    public int getConflictRank() {
        return conflictRank;
    }

}
