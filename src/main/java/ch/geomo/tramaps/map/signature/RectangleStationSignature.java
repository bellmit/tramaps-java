/*
 * Copyright (c) 2016 Thomas Zuberbuehler. All rights reserved.
 */

package ch.geomo.tramaps.map.signature;

import ch.geomo.tramaps.graph.Edge;
import ch.geomo.tramaps.graph.Node;
import ch.geomo.tramaps.graph.direction.Direction;
import ch.geomo.tramaps.graph.direction.OctilinearDirection;
import ch.geomo.util.geom.GeomUtil;
import org.jetbrains.annotations.NotNull;

import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * A simple implementation set a station signature. The signature's form
 * is a rectangle.
 */
public class RectangleStationSignature extends AbstractNodeSignature {

    private static final double MIN_SIDE_LENGTH = 20d;
    private static final double STATION_MARGIN = 5d;

    public RectangleStationSignature(@NotNull Node node) {
        super(node);
    }

    /**
     * Updates the signature properties. Recalculates the signature geometry if the node's
     * x- and y-value where changed.
     */
    @Override
    public void updateSignature() {

        if (hasOnlyDiagonalDirections()) {

            double ne = getSize(edge -> {
                OctilinearDirection direction = edge.getOriginalDirection(node).toOctilinear();
                return direction == OctilinearDirection.NORTH_EAST || direction == OctilinearDirection.SOUTH_WEST;
            });
            double nw = getSize(edge -> {
                OctilinearDirection direction = edge.getOriginalDirection(node).toOctilinear();
                return direction == OctilinearDirection.NORTH_WEST || direction == OctilinearDirection.SOUTH_EAST;
            });

            // future idea: rotate signature
            signature = GeomUtil.createPolygon(node.getPoint(), Math.max(nw, MIN_SIDE_LENGTH), Math.max(ne, MIN_SIDE_LENGTH));

        }
        else {

            double width = getSize(edge -> !edge.getOriginalDirection(node).isHorizontal());
            double height = getSize(edge -> !edge.getOriginalDirection(node).isVertical());

            if (hasNoVerticalDirections() && hasNoDiagonalDirections()) {
                width = MIN_SIDE_LENGTH;
            }
            else if (hasNoHorizontalDirections() && hasNoDiagonalDirections()) {
                height = MIN_SIDE_LENGTH;
            }

            signature = GeomUtil.createPolygon(node.getPoint(), Math.max(width, MIN_SIDE_LENGTH), Math.max(height, MIN_SIDE_LENGTH));

        }

        setChanged();
        notifyObservers();

    }

    private double getSize(Predicate<Edge> predicate) {
        return node.getAdjacentEdges().stream()
                .filter(predicate)
                .map(edge -> edge.calculateEdgeWidth(STATION_MARGIN))
                .max(Double::compare)
                .orElse(STATION_MARGIN);
    }

    @NotNull
    private Stream<OctilinearDirection> getAdjacentEdgeDirections() {
        return node.getAdjacentEdges().stream()
                .map(edge -> edge.getOriginalDirection(node))
                .map(Direction::toOctilinear);
    }

    private boolean hasOnlyDiagonalDirections() {
        return getAdjacentEdgeDirections()
                .allMatch(Direction::isDiagonal);
    }

    private boolean hasNoDiagonalDirections() {
        return getAdjacentEdgeDirections()
                .noneMatch(Direction::isDiagonal);
    }

    private boolean hasNoVerticalDirections() {
        return getAdjacentEdgeDirections()
                .noneMatch(Direction::isVertical);
    }

    private boolean hasNoHorizontalDirections() {
        return getAdjacentEdgeDirections()
                .noneMatch(Direction::isHorizontal);
    }

}
