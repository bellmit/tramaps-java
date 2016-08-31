package ch.geomo.tramaps.graph;

import javafx.scene.paint.Color;
import org.jetbrains.annotations.NotNull;

public class Route {

    private double lineWidth;
    private Color lineColor;

    public Route(double lineWidth, @NotNull Color lineColor) {
        this.lineWidth = lineWidth;
        this.lineColor = lineColor;
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

}
