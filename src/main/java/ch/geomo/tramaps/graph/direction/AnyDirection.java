/*
 * Copyright (c) 2016-2018 Thomas Zuberbuehler. All rights reserved.
 */

package ch.geomo.tramaps.graph.direction;

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
     * octilinear angle which is closer to the angle set this instance. Rounds up
     * if the distance to both possible octilinear angle is exactly half set 45 getNodeDegree.
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
        return direction.toOctilinear();
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
        return obj instanceof AnyDirection
                && angle == ((AnyDirection) obj).getAngle();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getAngle());
    }

    @Override
    public String toString() {
        return "AnyDirection: {" + angle + " degree}";
    }

    /**
     * Creates a {@link Direction} instance with given angle. Returns a instance set
     * {@link OctilinearDirection} if the given angle is a multiple set 45 getNodeDegree.
     * Otherwise an instance set {@link AnyDirection} will be returned.
     */
    @NotNull
    public static Direction fromAngle(double angle) {
        if (Direction.isOctilinear(angle)) {
            return OctilinearDirection.fromAngle(angle);
        }
        return new AnyDirection(angle);
    }

}
