package ch.geomo.tramaps.graph;

import org.jetbrains.annotations.NotNull;

import java.awt.*;

public class Route {

    private double lineWidth;
    private Color lineColor;

    public Route(double lineWidth, @NotNull Color lineColor) {
        this.lineWidth = lineWidth;
        this.lineColor = lineColor;
    }

    public Color getLineColor() {
        return lineColor;
    }

    public double getLineWidth() {
        return this.lineWidth;
    }

}
