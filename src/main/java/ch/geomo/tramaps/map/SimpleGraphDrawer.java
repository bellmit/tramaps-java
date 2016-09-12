/*
 * Copyright (c) 2016 Thomas Zuberbuehler. All rights reserved.
 */

package ch.geomo.tramaps.map;

import ch.geomo.tramaps.graph.Edge;
import com.vividsolutions.jts.geom.Envelope;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import org.jetbrains.annotations.NotNull;

public class SimpleGraphDrawer {

    private final MetroMap map;
    private final double margin;

    private double lineWidth = 5;
    private double radius = 20;

    public SimpleGraphDrawer(@NotNull MetroMap map, double margin, double factor) {
        this.map = map;
        this.margin = margin;
        if (factor > 1) {
            lineWidth = 1;
            radius = 5;
        }
    }

    private void drawEdge(@NotNull Edge edge, @NotNull GraphicsContext context) {
        context.strokeLine(edge.getNodeA().getX(), edge.getNodeA().getY(), edge.getNodeB().getX(), edge.getNodeB().getY());
    }

    public void draw(@NotNull GraphicsContext context, @NotNull Envelope bbox) {

        context.translate(-bbox.getMinX() + margin, -bbox.getMinY() + margin);

        int max = (int) Math.ceil(Math.max(bbox.getMaxX(), bbox.getMaxY())) + 1000;
        for (int i = -max, j = 0; i < max; i = i + 25, j = j + 25) {
            context.setStroke(j % 100 == 0 ? Color.GRAY : Color.LIGHTGRAY);
            context.strokeLine(i, -max, i, max * 2);
            context.strokeLine(-max, i, max * 2, i);
        }

        context.translate(-radius, -radius);
        map.getNodes().forEach(node -> {
            context.setFill(Color.rgb(0, 145, 255));
            context.fillOval(node.getX(), node.getY(), radius * 2, radius * 2);
        });
        context.translate(radius, radius);

        map.getEdges().forEach(edge -> {
            context.setLineWidth(lineWidth);
            context.setStroke(Color.BLACK);
            drawEdge(edge, context);
        });

//        map.getNodes().forEach(node -> {
//            context.setStroke(Color.BLACK);
//            context.setLineWidth(1);
//            context.strokeText(node.getName() + "(" + Math.round(node.getX()) + "/" + Math.round(node.getY()) + ")", node.getX() - 50, node.getY() + 20);
//        });

    }

}
