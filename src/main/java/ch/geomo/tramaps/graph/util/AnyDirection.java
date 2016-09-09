/*
 * Copyright (c) 2016 Thomas Zuberbuehler. All rights reserved.
 */

package ch.geomo.tramaps.graph.util;

import ch.geomo.util.Contracts;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class AnyDirection implements Direction {

    private final double angle;
    private Direction oppositeDirection;

    protected AnyDirection(double angle) {
        this.angle = angle % 360;
    }

    @Override
    public double getAngle() {
        return angle;
    }

    public boolean isVertical() {
        return angle % 180 == 0;
    }

    public boolean isHorizontal() {
        return angle % 90 == 0 && !isVertical();
    }

    /**
     * Returns the closest octilinear direction for this direction. Rounds to the
     * octilinear angle which is closer to the angle of this instance. Rounds up
     * if the distance to both possible octilinear angle is exactly half of 45 getNodeDegree.
     *
     * @return the closest octilinear direction
     */
    @NotNull
    @Override
    public OctilinearDirection toOctilinear() {
        Direction direction;
        double diff = angle % 45;
        if (diff < 45 / 2) {
            direction = fromAngle(angle - diff);
        }
        else {
            direction = fromAngle(angle + (45 - diff));
        }
        Contracts.test(direction, OctilinearDirection::isOctilinear, () -> "Direction " + direction + " is not octilinear!");
        return direction.toOctilinear();
    }

    @NotNull
    @Override
    public OctilinearDirection toOrthogonal() {
        Direction direction;
        double diff = angle % 45;
        if (diff < 45) {
            direction = fromAngle(angle - diff);
        }
        else {
            direction = fromAngle(angle + (90 - diff));
        }
        Contracts.test(direction, OctilinearDirection::isOctilinear, () -> "Direction " + direction + " is not octilinear!");
        return direction.toOctilinear();
    }

    /**
     * Creates a {@link Direction} instance with given angle. Returns a instance of
     * {@link OctilinearDirection} if the given angle is a multiple of 45 getNodeDegree.
     * Otherwise an instance of {@link AnyDirection} will be returned.
     */
    @NotNull
    public static Direction fromAngle(double angle) {
        if (Direction.isOctilinear(angle)) {
            return OctilinearDirection.fromAngle(angle);
        }
        return new AnyDirection(angle);
    }

    /**
     * @return the opposite direction
     */
    @NotNull
    @Override
    public Direction opposite() {
        if (oppositeDirection == null) {
            // calculate once when accessing first time
            oppositeDirection = fromAngle(Math.abs(360 - angle));
        }
        return oppositeDirection;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof AnyDirection && angle == ((AnyDirection) obj).getAngle();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getAngle());
    }

    @Override
    public String toString() {
        return angle + " degree";
    }

}
