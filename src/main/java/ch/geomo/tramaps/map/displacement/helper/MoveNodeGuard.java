/*
 * Copyright (c) 2016 Thomas Zuberbuehler. All rights reserved.
 */

package ch.geomo.tramaps.map.displacement.helper;

import ch.geomo.tramaps.conflict.Conflict;
import ch.geomo.tramaps.graph.GraphElement;
import ch.geomo.tramaps.graph.Node;
import ch.geomo.tramaps.graph.util.OctilinearDirection;
import ch.geomo.tramaps.map.MetroMap;
import ch.geomo.tramaps.map.displacement.helper.DisplaceNodeHandler.DisplaceNodeResult;
import com.vividsolutions.jts.geom.Coordinate;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static ch.geomo.tramaps.graph.util.OctilinearDirection.NORTH;

/**
 * A transfer object which works as a guard to keep track of already visited nodes.
 */
public class MoveNodeGuard {

    private final List<Node> traversedNodes;

    private final Conflict conflict;
    private final MetroMap map;

    private final MoveNodeHandler nodeMoveHandler;
    private final DisplaceNodeResult displaceNodeResult;

    private List<Node> moveableNodes;
    private OctilinearDirection lastMoveDirection;
    private double lastMoveDistance = 0d;

    public MoveNodeGuard(@NotNull MetroMap map, @NotNull Conflict conflict, @NotNull DisplaceNodeResult displaceNodeResult, @NotNull Node firstNode) {
        this.map = map;
        this.conflict = conflict;
        this.displaceNodeResult = displaceNodeResult;
        lastMoveDirection = displaceNodeResult.getDisplaceDirection();
        traversedNodes = new ArrayList<>();
        nodeMoveHandler = new MoveNodeHandler();
        initMoveableNodes(firstNode);
    }

    private void initMoveableNodes(@NotNull Node firstNode) {

        Coordinate pointOnDisplacementLine = conflict.getSamplePointOnDisplaceLine();

        if (displaceNodeResult.getDisplaceDirection() == NORTH) {
            moveableNodes = map.getNodes().stream()
                    .filter(node -> {
                        if (firstNode.getX() < pointOnDisplacementLine.x) {
                            return node.getX() < pointOnDisplacementLine.x;
                        }
                        return node.getX() > pointOnDisplacementLine.x;
                    })
                    .collect(Collectors.toList());
        }
        else {
            moveableNodes = map.getNodes().stream()
                    .filter(node -> {
                        if (firstNode.getY() < pointOnDisplacementLine.y) {
                            return node.getY() < pointOnDisplacementLine.y;
                        }
                        return node.getY() > pointOnDisplacementLine.y;
                    })
                    .collect(Collectors.toList());
        }

    }

    public MoveNodeHandler getNodeMoveHandler() {
        return nodeMoveHandler;
    }

    public DisplaceNodeResult getDisplaceNodeResult() {
        return displaceNodeResult;
    }

    public OctilinearDirection getDisplaceDirection() {
        return displaceNodeResult.getDisplaceDirection();
    }

    public double getDisplaceDistance() {
        return displaceNodeResult.getDisplaceDistance();
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
     * @return the best displace length based on the {@link Conflict}
     */
    public int getMoveDistance() {
        return conflict.getBestDisplaceLength();
    }

    /**
     * @return the {@link Conflict} which is guarded by this instance
     */
    @NotNull
    public Conflict getConflict() {
        return conflict;
    }

    /**
     * @return true if given {@link Node} is already traversed/visted
     */
    public boolean hasAlreadyVisited(@NotNull Node node) {
        return traversedNodes.contains(node);
    }

    /**
     * Marks the given {@link Node} as visited. Adds the node to the
     * cache of traversed nodes.
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
        return conflict.isConflictElementRelated(graphElement);
    }

    /**
     * Clears the cache of the traversed nodes and returns this instance.
     *
     * @return this instance
     */
    @NotNull
    public MoveNodeGuard reuse() {
        traversedNodes.clear();
        return this;
    }

    /**
     * @return the {@link MetroMap} instance
     */
    @NotNull
    public MetroMap getMetroMap() {
        return map;
    }

    public boolean hasBeenDisplaced(@NotNull Node node) {
        return displaceNodeResult.getDisplacedNodes().contains(node);
    }

}
