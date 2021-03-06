/*
 * Copyright (c) 2016-2018 Thomas Zuberbuehler. All rights reserved.
 */

package ch.geomo.tramaps.conflict;

/**
 * Represents the type of a {@link Conflict}.
 */
public enum ConflictType {

    /**
     * Represents an {@link OctilinearConflict} which occurs if a diagonal edge
     * is not octilinear.
     */
    OCTILINEAR(50),
    /**
     * Represents a {@link BufferConflict} with two adjacent nodes which shares
     * an identical edge.
     */
    ADJACENT_NODE_NODE_DIAGONAL(50),
    /**
     * Represents a {@link BufferConflict} with two adjacent nodes which <b>do not</b>
     * share an identical edge.
     */
    ADJACENT_NODE_NODE(25),
    /**
     * Represents a {@link BufferConflict} with two <b>non-adjacent</b> nodes.
     */
    NODE_NODE(20),
    /**
     * Represents a {@link BufferConflict} between a node and a non-adjacent edge.
     */
    NODE_EDGE(10),
    /**
     * Represents a {@link BufferConflict} between two non-adjacent edges.
     */
    EDGE_EDGE(5);

    private final int conflictRank;

    ConflictType(int conflictRank) {
        this.conflictRank = conflictRank;
    }

    /**
     * @return the rank defining the priority order to solve the conflict
     */
    public int getConflictRank() {
        return conflictRank;
    }

}
