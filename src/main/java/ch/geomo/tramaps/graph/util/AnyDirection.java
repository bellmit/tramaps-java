package ch.geomo.tramaps.graph.util;

import org.jetbrains.annotations.NotNull;

public class AnyDirection implements Direction {

    private double angle;

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

    @NotNull
    @Override
    public OctilinearDirection toOctilinearDirection() {
        Direction direction;
        if ((angle % 45) < 45/2) {
            direction = fromAngle(Math.floor(angle/100)*100);
        }
        else {
            direction = fromAngle(Math.ceil(angle/100)*100);
        }
        if (!(direction instanceof OctilinearDirection)) {
            // should never reach this point
            throw new IllegalStateException("Something went wrong while evaluating the best octilinear direction.");
        }
        return (OctilinearDirection)direction;
    }

    @NotNull
    public static Direction fromAngle(double angle) {
        Direction direction = OctilinearDirection.fromAngle(angle);
        if (direction != null) {
            return direction;
        }
        return new AnyDirection(angle);
    }

}
