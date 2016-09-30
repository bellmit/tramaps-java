/*
 * Copyright (c) 2016 Thomas Zuberbuehler. All rights reserved.
 */

package ch.geomo.tramaps.map;

import ch.geomo.tramaps.conflict.BufferConflict;
import ch.geomo.tramaps.graph.Edge;
import ch.geomo.tramaps.map.signature.BendNodeSignature;
import com.vividsolutions.jts.geom.Envelope;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.text.Font;
import org.jetbrains.annotations.NotNull;

public class SimpleGraphDrawer {

    private final MetroMap map;
    private final double margin;
    private final double scaleFactor;

    private final boolean showNodeName;
    private final boolean showNodePosition;

    public SimpleGraphDrawer(@NotNull MetroMap map, double margin, double scaleFactor, boolean showNodeName, boolean showNodePosition) {
        this.map = map;
        this.margin = margin;
        this.scaleFactor = scaleFactor;
        this.showNodeName = showNodeName;
        this.showNodePosition = showNodePosition;
    }

    private void drawEdge(@NotNull Edge edge, @NotNull GraphicsContext context) {
        context.strokeLine(edge.getNodeA().getX() * scaleFactor, -edge.getNodeA().getY() * scaleFactor, edge.getNodeB().getX() * scaleFactor, -edge.getNodeB().getY() * scaleFactor);
    }

    public void draw(@NotNull GraphicsContext context, @NotNull Envelope bbox) {

        // start drawing at the top left
        context.translate(-bbox.getMinX() * scaleFactor + margin, bbox.getMaxY() * scaleFactor + margin);

//        int max = (int) Math.ceil(Math.max(bbox.getMaxX(), bbox.getMaxY() * -1)) + 1000;
//        for (int i = -max, j = 0; i < max; i = i + 50, j = j + 50) {
//            context.setStroke(Color.LIGHTGRAY);
//            context.setLineWidth(j == 0 || j % 500 == 0 ? 5 * scaleFactor : (j % 250 == 0) ? 3 * scaleFactor : 1 * scaleFactor);
//            context.strokeLine(i * scaleFactor, max * scaleFactor, i * scaleFactor, -max * 2 * scaleFactor);
//            context.strokeLine(-max * scaleFactor, -i * scaleFactor, max * 2 * scaleFactor, -i * scaleFactor);
//        }

        map.getEdges().forEach(edge -> {
            context.setLineWidth(2 * scaleFactor);
            context.setStroke(Color.BLACK);
            drawEdge(edge, context);
        });
        context.translate(-2 * scaleFactor, -2 * scaleFactor);
        map.getNodes().forEach(node -> {
            context.setFill(Color.rgb(0, 145, 255));
            context.fillOval(node.getX() * scaleFactor, -node.getY() * scaleFactor, 4 * scaleFactor, 4 * scaleFactor);
        });

        context.translate(2 * scaleFactor, 2 * scaleFactor);
        if (showNodeName) {
            Font font = Font.font(7);
            map.getNodes().forEach(node -> {
                Envelope station = node.getNodeSignature().getGeometry().getEnvelopeInternal();
                context.setStroke(Color.BLACK);
                context.setFont(font);
                context.setLineWidth(0.5);
                if (showNodePosition) {
                    context.strokeText(node.getName() + "(" + Math.round(node.getX()) + "/" + Math.round(node.getY()) + ")", node.getPoint().getX() * scaleFactor - 10 * scaleFactor, -node.getPoint().getY() * scaleFactor + 5 * scaleFactor);
                }
                else {
                    if (node.getNodeSignature() instanceof BendNodeSignature) {
                        // context.strokeText(node.getName(), node.getPoint().getX() * scaleFactor - 50 * scaleFactor, -node.getPoint().getY() * scaleFactor - 20 * scaleFactor);
                    }
                    else {
                        context.strokeText(node.getName(), station.getMinX() * scaleFactor - 20 * scaleFactor, -station.getMaxY() * scaleFactor - 20 * scaleFactor);
                    }
                }
            });
        }

    }

}
