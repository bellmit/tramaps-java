package ch.geomo.tramaps.graph.util;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

public enum OctilinearDirection implements Direction {

    NORTH(0d),
    NORTH_EAST(45d),
    EAST(90d),
    SOUTH_EAST(135d),
    SOUTH(180d),
    SOUTH_WEST(225d),
    WEST(270d),
    NORTH_WEST(315d);

    private double angle;

    OctilinearDirection(double angle) {
        this.angle = angle;
    }

    public double getAngle() {
        return angle;
    }

    @Contract(pure = true)
    public boolean isHorizontal() {
        return this == EAST || this == WEST;
    }

    @Contract(pure = true)
    public boolean isVertical() {
        return this == NORTH || this == SOUTH;
    }

    @NotNull
    @Override
    @Contract(pure = true)
    public OctilinearDirection toOctilinearDirection() {
        return this; // :-)
    }

    @Contract(value = "null -> false", pure = true)
    public static boolean isOctilinear(Direction direction) {
        return direction instanceof OctilinearDirection;
    }

    public static boolean isOctilinear(double angle) {
        return isOctilinear(fromAngle(angle));
    }

    @Nullable
    public static OctilinearDirection fromAngle(double angle) {
        if (angle == 360) {
            return NORTH;
        }
        return Arrays.stream(values())
                .filter(direction -> direction.angle == angle)
                .findFirst()
                .orElse(null);
    }

}
