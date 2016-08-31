package ch.geomo.tramaps.graph.util;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public interface Direction {

    /**
     * @return the angle of the direction
     */
    double getAngle();

    /**
     * @return true if horizontal to x-axis
     */
    boolean isHorizontal();

    /**
     * @return true if vertical to y-axis
     */
    boolean isVertical();

    /**
     * @return true if neither horizontal nor vertical
     */
    @Contract(pure = true)
    default boolean isDiagonal() {
        return !isHorizontal() && !isVertical();
    }

    @NotNull
    OctilinearDirection toOctilinearDirection();

}
