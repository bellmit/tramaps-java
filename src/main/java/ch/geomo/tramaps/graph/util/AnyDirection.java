/*
 * Copyright (c) 2016 Thomas Zuberbuehler. All rights reserved.
 */

package ch.geomo.tramaps.graph.util;

import org.jetbrains.annotations.NotNull;

public class AnyDirection implements Direction {

    private double angle;
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
     * if the distance to both possible octilinear angle is exactly half of 45 degree.
     *
     * @return the closest octilinear direction
     */
    @NotNull
    @Override
    public OctilinearDirection toOctilinear() {
        Direction direction;
        if ((angle % 45) < 45 / 2) {
            direction = fromAngle(Math.floor(angle / 100) * 100);
        }
        else {
            direction = fromAngle(Math.ceil(angle / 100) * 100);
        }
        if (!(direction instanceof OctilinearDirection)) {
            // should never reach this point
            System.out.println(direction);
            throw new IllegalStateException("Something went wrong while evaluating the best octilinear direction.");
        }
        return (OctilinearDirection) direction;
    }

    /**
     * Creates a {@link Direction} instance with given angle. Returns a instance of
     * {@link OctilinearDirection} if the given angle is a multiple of 45 degree.
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
    public String toString() {
        return "Direction: {" + angle + " degree}";
    }

}
