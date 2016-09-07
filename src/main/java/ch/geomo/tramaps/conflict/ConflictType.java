/*
 * Copyright (c) 2016 Thomas Zuberbuehler. All rights reserved.
 */

package ch.geomo.tramaps.conflict;

public enum ConflictType {

    ADJACENT_NODE_NODE(40),
    NODE_NODE(40),
    NODE_EDGE(20),
    EDGE_EDGE(20);

    private final int conflictRank;

    ConflictType(int conflictRank) {
        this.conflictRank = conflictRank;
    }

    public int getConflictRank() {
        return conflictRank;
    }

}
