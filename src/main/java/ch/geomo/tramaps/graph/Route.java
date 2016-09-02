/*
 * Copyright (c) 2016 Thomas Zuberbuehler. All rights reserved.
 */

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
        return "Route: {name= " + name + ", lineColor= " + lineColor + ", lineWidth= " + lineWidth + "}";
    }

}
