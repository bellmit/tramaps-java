/*
 * Copyright (c) 2016 Thomas Zuberbuehler. All rights reserved.
 */

package ch.geomo.tramaps.graph;

import javafx.scene.paint.Color;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class Route {

    private double lineWidth;
    private Color lineColor;
    private String name;

    public Route(@NotNull String name, double lineWidth, @NotNull Color lineColor) {
        this.name = name;
        this.lineWidth = lineWidth;
        this.lineColor = lineColor;
    }

    @NotNull
    public String getName() {
        return name;
    }

    @NotNull
    public Color getLineColor() {
        return lineColor;
    }

    public double getLineWidth() {
        return lineWidth;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Route && Objects.equals(name, ((Route) obj).getName())
                && Objects.equals(lineColor, ((Route) obj).getLineColor()) && lineWidth == ((Route) obj).getLineWidth();
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, lineColor, lineWidth);
    }

    @Override
    public String toString() {
        return "Route: {" + name + "}";
    }

}
