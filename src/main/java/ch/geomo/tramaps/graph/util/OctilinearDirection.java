/*
 * Copyright (c) 2016 Thomas Zuberbuehler. All rights reserved.
 */

package ch.geomo.tramaps.graph.util;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.stream.Stream;

public enum OctilinearDirection implements Direction {

    NORTH(0d, Alignment.VERTICAL, true, false, false, false),
    NORTH_EAST(45d, Alignment.DIAGONAL_45, true, true, false, false),
    EAST(90d, Alignment.HORIZONTAL, false, true, false, false),
    SOUTH_EAST(135d, Alignment.DIAGONAL_135, false, true, true, false),
    SOUTH(180d, Alignment.VERTICAL, false, false, true, false),
    SOUTH_WEST(225d, Alignment.DIAGONAL_45, false, false, true, true),
    WEST(270d, Alignment.HORIZONTAL, false, false, false, true),
    NORTH_WEST(315d, Alignment.DIAGONAL_135, true, false, false, true);

    private final double angle;
    private final Alignment alignment;
    private final boolean northwards;
    private final boolean eastwards;
    private final boolean southwards;
    private final boolean westwards;

    OctilinearDirection(double angle, Alignment alignment, boolean north, boolean east, boolean south, boolean west) {
        this.angle = angle;
        this.alignment = alignment;
        northwards = north;
        eastwards = east;
        southwards = south;
        westwards = west;
    }

    public double getAngle() {
        return angle;
    }

    public Alignment getAlignment() {
        return alignment;
    }

    public boolean isEastwards() {
        return eastwards;
    }

    public boolean isNorthwards() {
        return northwards;
    }

    public boolean isSouthwards() {
        return southwards;
    }

    public boolean isWestwards() {
        return westwards;
    }

    @Contract(pure = true)
    public boolean isHorizontal() {
        return this == EAST || this == WEST;
    }

    @Contract(pure = true)
    public boolean isVertical() {
        return this == NORTH || this == SOUTH;
    }

    @Contract(pure = true)
    public boolean isDiagonal45() {
        return getAlignment() == Alignment.DIAGONAL_45;
    }

    @Contract(pure = true)
    public boolean isDiagonal135() {
        return getAlignment() == Alignment.DIAGONAL_135;
    }

    /**
     * Returns the closest octilinear direction for this direction. Since
     * this implementation of {@link Direction} is always octilinear, the
     * current instance will always be returned.
     *
     * @return current instance
     */
    @NotNull
    @Override
    @Contract(value = "->!null", pure = true)
    public OctilinearDirection toOctilinear() {
        return this; // just satisfying interface :-)
    }

    @Override
    public @NotNull OctilinearDirection toOrthogonal() {
        if (angle % 90 == 0) {
             return this;
        }
        return fromAngle(angle + 45);
    }

    /**
     * @return true if given {@link Direction} is an instance of {@link OctilinearDirection}
     */
    @Contract(value = "null -> false", pure = true)
    public static boolean isOctilinear(@Nullable Direction direction) {
        return direction instanceof OctilinearDirection;
    }

    /**
     * Finds the octilinear direction for given angle. If angle is not a multiple
     * of 45 getNodeDegree, an octilinear direction will be evaluated using
     * {@link AnyDirection#toOctilinear()}.
     *
     * @return the octilinear direction for given angle
     */
    @NotNull
    public static OctilinearDirection fromAngle(double angle) {
        return Arrays.stream(values())
                .filter(direction -> direction.angle == (angle+360) % 360)
                .findFirst()
                .orElseThrow(IllegalStateException::new);
    }

    /**
     * @return the opposite direction of this direction
     */
    @NotNull
    public OctilinearDirection opposite() {
        switch (this) {
            case NORTH_EAST:
                return SOUTH_WEST;
            case EAST:
                return WEST;
            case SOUTH_EAST:
                return NORTH_WEST;
            case SOUTH:
                return NORTH;
            case SOUTH_WEST:
                return NORTH_EAST;
            case WEST:
                return EAST;
            case NORTH_WEST:
                return SOUTH_EAST;
            default:
                return SOUTH;
        }
    }

    public OctilinearDirection rotate(double angle) {
        return fromAngle(getAngle() + angle);
    }

    @NotNull
    public OctilinearDirection rotate(@NotNull OctilinearDirection nullDirection) {
        return fromAngle(getAngle() + nullDirection.getAngle());
    }

    /**
     * @return true if one of the given {@link OctilinearDirection} is equals to this instance
     */
    public boolean matchWithOneOf(@NotNull OctilinearDirection... directions) {
        return Stream.of(directions).anyMatch(direction -> this == direction);
    }

}
