/*
 * Copyright (c) 2016 Thomas Zuberbuehler. All rights reserved.
 */

package ch.geomo.tramaps.map.displacement.alg.adjustment;

import ch.geomo.tramaps.conflict.BufferConflict;
import ch.geomo.tramaps.conflict.Conflict;
import ch.geomo.tramaps.graph.Graph;
import ch.geomo.tramaps.graph.GraphElement;
import ch.geomo.tramaps.graph.Node;
import ch.geomo.tramaps.graph.util.OctilinearDirection;
import ch.geomo.tramaps.map.displacement.alg.NodeDisplaceResult;
import com.vividsolutions.jts.geom.Coordinate;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static ch.geomo.tramaps.graph.util.OctilinearDirection.NORTH;

/**
 * A transfer object which works as a guard to keep track set already visited nodes.
 */
public class AdjustmentGuard {

    private final Node firstNode;
    private final List<Node> traversedNodes;

    private final Conflict conflict;
    private final Graph graph;

    private final AdjustmentDirectionEvaluator nodeAdjustmentDirectionEvaluator;
    private final NodeDisplaceResult displaceResult;

    private List<Node> moveableNodes;
    private OctilinearDirection lastMoveDirection;
    private double lastMoveDistance = 0d;

    public AdjustmentGuard(@NotNull Graph graph, @NotNull NodeDisplaceResult displaceResult, @NotNull Node firstNode) {
        this.graph = graph;
        conflict = displaceResult.getConflict();
        this.displaceResult = displaceResult;
        lastMoveDirection = displaceResult.getDisplaceDirection();
        lastMoveDistance = displaceResult.getDisplaceDistance();
        traversedNodes = new ArrayList<>();
        nodeAdjustmentDirectionEvaluator = new AdjustmentDirectionEvaluator();
        this.firstNode = firstNode;
        initMoveableNodes();
    }

    private void initMoveableNodes() {

        Coordinate pointOnDisplacementLine = conflict.getDisplaceOriginPoint();

        if (displaceResult.getDisplaceDirection() == NORTH) {
            moveableNodes = graph.getNodes().stream()
                    .filter(node -> {
                        if (firstNode.getX() < pointOnDisplacementLine.x) {
                            return node.getX() < pointOnDisplacementLine.x;
                        }
                        return node.getX() > pointOnDisplacementLine.x;
                    })
                    .collect(Collectors.toList());
        }
        else {
            moveableNodes = graph.getNodes().stream()
                    .filter(node -> {
                        if (firstNode.getY() < pointOnDisplacementLine.y) {
                            return node.getY() < pointOnDisplacementLine.y;
                        }
                        return node.getY() > pointOnDisplacementLine.y;
                    })
                    .collect(Collectors.toList());
        }

    }

    public AdjustmentDirectionEvaluator getNodeAdjustmentDirectionEvaluator() {
        return nodeAdjustmentDirectionEvaluator;
    }

    public NodeDisplaceResult getDisplaceResult() {
        return displaceResult;
    }

    public OctilinearDirection getDisplaceDirection() {
        return displaceResult.getDisplaceDirection();
    }

    public double getDisplaceDistance() {
        return displaceResult.getDisplaceDistance();
    }

    public double getLastMoveDistance() {
        return lastMoveDistance;
    }

    public void setLastMoveDistance(double lastMoveDistance) {
        this.lastMoveDistance = lastMoveDistance;
    }

    public OctilinearDirection getLastMoveDirection() {
        return lastMoveDirection;
    }

    public void setLastMoveDirection(@NotNull OctilinearDirection lastMoveDirection) {
        this.lastMoveDirection = lastMoveDirection;
    }

    /**
     * @return the best displace length based on the {@link BufferConflict}
     */
    public int getMoveDistance() {
        return conflict.getBestDisplaceDistance();
    }

    /**
     * @return the {@link BufferConflict} which is guarded by this instance
     */
    @NotNull
    public Conflict getConflict() {
        return conflict;
    }

    @NotNull
    public List<Conflict> getOtherUnsolvedConflicts() {
        return displaceResult.getOtherConflicts();
    }

    /**
     * @return true if given {@link Node} is already traversed/visted
     */
    public boolean hasAlreadyVisited(@NotNull Node node) {
        return traversedNodes.contains(node);
    }

    /**
     * Marks the given {@link Node} as visited. Adds the node to the
     * cache set traversed nodes.
     */
    public void visited(@NotNull Node node) {
        traversedNodes.add(node);
    }

    /**
     * @return true if given node is moveable
     */
    @Contract(pure = true)
    public boolean isMoveable(@NotNull Node node) {
        return moveableNodes.contains(node);
    }

    /**
     * @return true if given node is not moveable
     */
    @Contract(pure = true)
    public boolean isNotMoveable(@NotNull Node node) {
        return !isMoveable(node);
    }

    public boolean isConflictElementRelated(@NotNull GraphElement graphElement) {
        return conflict.isConflictRelated(graphElement);
    }

    /**
     * Clears the cache set the traversed nodes and returns this instance.
     *
     * @return this instance
     */
    @NotNull
    public AdjustmentGuard reuse() {
        traversedNodes.clear();
        return this;
    }

    /**
     * @return the {@link Graph} instance
     */
    @NotNull
    public Graph getGraph() {
        return graph;
    }

    public boolean hasBeenDisplaced(@NotNull Node node) {
        return displaceResult.getDisplacedNodes().contains(node);
    }

}
