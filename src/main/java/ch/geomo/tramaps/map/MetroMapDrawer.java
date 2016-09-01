/*
 * Copyright (c) 2016 Thomas Zuberbuehler. All rights reserved.
 */

package ch.geomo.tramaps.map;

import ch.geomo.tramaps.conflict.Conflict;
import ch.geomo.tramaps.conflict.ConflictFinder;
import ch.geomo.tramaps.graph.Edge;
import com.vividsolutions.jts.geom.Envelope;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.shape.StrokeLineCap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

public class MetroMapDrawer {

    private MetroMap map;
    private double routeMargin;
    private double edgeMargin;

    public MetroMapDrawer(double routeMargin, double edgeMargin) {
        this.routeMargin = routeMargin;
        this.edgeMargin = edgeMargin;
    }

    public void setMetroMap(@Nullable MetroMap map) {
        this.map = map;
    }

    private void drawEdge(@NotNull Edge edge, @NotNull GraphicsContext context) {
        context.strokeLine(edge.getNodeA().getX(), edge.getNodeA().getY(), edge.getNodeB().getX(), edge.getNodeB().getY());
    }

    public void draw(@NotNull GraphicsContext context, @NotNull Envelope bbox) {

        if (map == null) {
            return;
        }

        // start drawing at the top left
        context.translate(-bbox.getMinX() + 50, -bbox.getMinY() + 50);

        int max = (int) Math.ceil(Math.max(bbox.getMaxX(), bbox.getMaxY())) + 1000;
        for (int i = -max, j = 0; i < max; i = i + 25, j = j + 25) {
            context.setStroke(j % 100 == 0 ? Color.GRAY : Color.LIGHTGRAY);
            context.strokeLine(i, 0, i, max * 2);
            context.strokeLine(0, i, max * 2, i);
        }

        map.getEdges().forEach(edge -> {
            double width = edge.calculateEdgeWidth(routeMargin);
            context.setLineWidth(width);
            context.setStroke(Color.rgb(139, 187, 206, 0.5d));
            context.setLineCap(StrokeLineCap.BUTT);
            drawEdge(edge, context);
        });
        map.getNodes().forEach(node -> {
            Envelope station = node.getNodeSignature().getGeometry().getEnvelopeInternal();
            context.setFill(Color.BLACK);
            context.fillRoundRect(station.getMinX() - 5, station.getMinY() - 5, station.getWidth() + 10, station.getHeight() + 10, 25, 25);
            context.setFill(Color.WHITE);
            context.fillRoundRect(station.getMinX(), station.getMinY(), station.getWidth(), station.getHeight(), 25, 25);
        });
        map.getEdges().forEach(edge -> {
            context.setLineWidth(1);
            context.setStroke(Color.BLACK);
            drawEdge(edge, context);
        });
        context.translate(-5, -5);
        map.getNodes().forEach(node -> {
            context.setFill(Color.rgb(0, 145, 255));
            context.fillOval(node.getX(), node.getY(), 10, 10);
        });

        context.translate(5, 5);
        Set<Conflict> conflicts = new ConflictFinder(routeMargin, edgeMargin).getConflicts(map.getEdges(), map.getNodes());
        conflicts.forEach(conflict -> {
            context.setFill(Color.rgb(240, 88, 88, 0.4));
            Envelope bbox2 = conflict.getConflictPolygon().getEnvelopeInternal();
            context.fillRect(bbox2.getMinX(), bbox2.getMinY(), bbox2.getWidth(), bbox2.getHeight());
        });

    }

}
