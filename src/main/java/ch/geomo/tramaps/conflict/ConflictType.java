/*
 * Copyright (c) 2016 Thomas Zuberbuehler. All rights reserved.
 */

package ch.geomo.tramaps.conflict;

public enum ConflictType {

    OCTILINEAR(10, true, true),
    ADJACENT_NODE_NODE_DIAGONAL(5, true, true),
    ADJACENT_NODE_NODE(5, true, true),
    NODE_NODE(5, true, true),
    NODE_EDGE(5, true, false),
    EDGE_EDGE(5, false, false);

    private final boolean isNode1;
    private final boolean isNode2;
    private final int conflictRank;

    ConflictType(int conflictRank, boolean isNode1, boolean isNode2) {
        this.conflictRank = conflictRank;
        this.isNode1 = isNode1;
        this.isNode2 = isNode2;
    }

    public int getConflictRank() {
        return conflictRank;
    }

    public boolean isAdjacentNodeConflict() {
        return this == ADJACENT_NODE_NODE || this == ADJACENT_NODE_NODE_DIAGONAL;
    }

    public boolean isNodeNodeConflict() {
        return isNode1 && isNode2;
    }

    public boolean hasNode() {
        return isNode1 || isNode2;
    }

    public boolean hasEdge() {
        return !isNode1 || !isNode2;
    }

    public boolean isEdgeEdgeConflict() {
        return !isNode1 && !isNode2;
    }
}
