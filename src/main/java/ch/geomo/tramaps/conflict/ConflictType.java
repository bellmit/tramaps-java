/*
 * Copyright (c) 2016 Thomas Zuberbuehler. All rights reserved.
 */

package ch.geomo.tramaps.conflict;

public enum ConflictType {

    ADJACENT_NODE_NODE_DIAGONAL(80, true, true),
    ADJACENT_NODE_NODE(50, true, true),
    NODE_NODE(50, true, true),
    NODE_EDGE(50, true, false),
    EDGE_EDGE(50, false, false);

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

}
