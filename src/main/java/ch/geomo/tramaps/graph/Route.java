package ch.geomo.tramaps.graph;

import javafx.scene.paint.Color;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class Route {

    private double lineWidth;
    private Color lineColor;
    private String name;

    public Route(double lineWidth, @NotNull Color lineColor) {
        this.lineWidth = lineWidth;
        this.lineColor = lineColor;
    }

    public Route(double lineWidth, @NotNull Color lineColor, @Nullable String name) {
        this.lineWidth = lineWidth;
        this.lineColor = lineColor;
        this.name = name;
    }

    /**
     * @return the route's line color
     */
    @NotNull
    public Color getLineColor() {
        return lineColor;
    }

    /**
     * @return the route's line width
     */
    public double getLineWidth() {
        return lineWidth;
    }

    /**
     * @return the route's name
     */
    @Nullable
    public String getName() {
        return name;
    }

    @NotNull
    @Override
    public String toString() {
        String routeName = Objects.toString(this.name, "No Name");
        return "Route: {" + routeName + ", " + lineColor + ", " + lineWidth + "}";
    }

}
