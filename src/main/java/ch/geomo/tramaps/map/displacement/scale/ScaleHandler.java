/*
 * Copyright (c) 2016 Thomas Zuberbuehler. All rights reserved.
 */

package ch.geomo.tramaps.map.displacement.scale;

import ch.geomo.tramaps.conflict.Conflict;
import ch.geomo.tramaps.graph.direction.OctilinearDirection;
import ch.geomo.tramaps.map.MetroMap;
import ch.geomo.tramaps.map.displacement.LineSpaceHandler;
import ch.geomo.util.collection.list.EnhancedList;
import ch.geomo.util.geom.GeomUtil;
import ch.geomo.util.geom.PolygonUtil;
import ch.geomo.util.logging.Loggers;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryCollection;
import com.vividsolutions.jts.geom.util.AffineTransformation;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

/**
 * This {@link LineSpaceHandler} implementation makes space by scaling the underlying graph.
 */
public class ScaleHandler implements LineSpaceHandler {

    /**
     * Max iteration until algorithm will be terminated when not found a non-conflict solution.
     */
    private static final int MAX_ITERATIONS = 100;

    private final MetroMap map;

    public ScaleHandler(@NotNull MetroMap map) {
        this.map = map;
    }

    /**
     * Makes space for line and station signatures by scaling nodes recursively.
     */
    private double evaluateScaleFactor(@NotNull EnhancedList<Conflict> conflicts, double mapWidth, double mapHeight) {

        double maxMoveX = conflicts.stream()
                .map(conflict -> {
                    GeometryCollection col = GeomUtil.createCollection(Arrays.asList(conflict.getBufferA().getElement().getGeometry(), conflict.getBufferB().getElement().getGeometry()));
                    Envelope bbox = col.getEnvelopeInternal();
                    double alongX = conflict.getDisplaceDistanceAlongX();
                    if (bbox.getWidth() != 0) {
                        return (bbox.getWidth() + alongX) / bbox.getWidth();
                    }
                    return 1d;
                })
                .max(Double::compare)
                .orElse(1d);
        double maxMoveY = conflicts.stream()
                .map(conflict -> {
                    GeometryCollection col = GeomUtil.createCollection(Arrays.asList(conflict.getBufferA().getElement().getGeometry(), conflict.getBufferB().getElement().getGeometry()));
                    Envelope bbox = col.getEnvelopeInternal();
                    double alongY = conflict.getDisplaceDistanceAlongY();
                    if (bbox.getHeight() != 0) {
                        return (bbox.getHeight() + alongY) / bbox.getHeight();
                    }
                    return 1d;
                })
                .max(Double::compare)
                .orElse(1d);

        return Math.max(GeomUtil.makePrecise(Math.max(maxMoveX, maxMoveY)), 1.0001);

    }

    /**
     * Scales the map with given scale factor.
     */
    private void scale(double scaleFactor) {
        map.getNodes().forEach(node -> node.updatePosition(node.getX()*scaleFactor, node.getY()*scaleFactor));
    }

    private void makeSpace(int lastIteration) {

        int currentIteration = lastIteration + 1;

        EnhancedList<Conflict> conflicts = map.evaluateConflicts(true);

        Loggers.separator(this);
        Loggers.info(this, "Iteration: {0}", currentIteration);

        if (!conflicts.isEmpty()) {

            Loggers.warning(this, "Conflicts found: {0}", conflicts.size());

            Envelope bbox = map.getBoundingBox();
            // Loggers.info(this, "Bounding Box Size: " + bbox.getWidth() + "x" + bbox.getHeight());
            double scaleFactor = evaluateScaleFactor(conflicts, bbox.getWidth(), bbox.getHeight());
            Loggers.info(this, "Use scale factor: " + scaleFactor);
            scale(scaleFactor);

            if (currentIteration < MAX_ITERATIONS) {
                makeSpace(currentIteration);
            }
            else {
                Loggers.separator(this);
                Loggers.warning(this, "Max number set iteration reached. Stop algorithm.");
                Loggers.info(this, getBoundingBoxString());
                Loggers.separator(this);
            }

        }
        else {
            Loggers.separator(this);
            Loggers.info(this, "No (more) conflicts found.");
            Loggers.info(this, getBoundingBoxString());
            Loggers.separator(this);
        }

    }

    /**
     * @return the bounding box size as a {@link String}
     */
    @NotNull
    private String getBoundingBoxString() {
        Envelope mapBoundingBox = map.getBoundingBox();
        return "Size: " + (int) Math.ceil(mapBoundingBox.getWidth()) + "x" + (int) Math.ceil(mapBoundingBox.getHeight());
    }

    /**
     * Starts algorithm and makes space for line and station signatures by scaling the node positions.
     */
    @Override
    public void makeSpace() {
        makeSpace(0);
    }

}
