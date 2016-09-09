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

    public SimpleGraphDrawer(MetroMap map, double margin) {
        this.map = map;
        this.margin = margin;
    }

    private void drawEdge(@NotNull Edge edge, @NotNull GraphicsContext context) {
        context.strokeLine(edge.getNodeA().getX(), edge.getNodeA().getY()*-1, edge.getNodeB().getX(), edge.getNodeB().getY()*-1);
    }

    public void draw(@NotNull GraphicsContext context, @NotNull Envelope bbox) {

        if (map == null) {
            return;
        }

        double x = bbox.getMinX();
        double y = bbox.getMaxY();

        // start drawing at the top left
        context.translate(-x + margin, y + margin);

        int max = (int) Math.ceil(Math.max(bbox.getMaxX(), bbox.getMaxY())) + 1000;
        for (int i = -max, j = 0; i < max; i = i + 25, j = j + 25) {
            context.setStroke(j % 100 == 0 ? Color.GRAY : Color.LIGHTGRAY);
            context.strokeLine(i, -max, i, max * 2);
            context.strokeLine(-max, i, max * 2, i);
        }

        context.translate(-5, -5);
        map.getNodes().forEach(node -> {
            context.setFill(Color.rgb(0, 145, 255));
            context.fillOval(node.getX(), node.getY()*-1, 10, 10);
        });
        context.translate(5, 5);

        map.getEdges().forEach(edge -> {
            context.setLineWidth(1);
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
