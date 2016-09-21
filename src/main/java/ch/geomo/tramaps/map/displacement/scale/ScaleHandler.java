/*
 * Copyright (c) 2016 Thomas Zuberbuehler. All rights reserved.
 */

package ch.geomo.tramaps.map.displacement.scale;

import ch.geomo.tramaps.conflict.Conflict;
import ch.geomo.tramaps.conflict.buffer.ElementBuffer;
import ch.geomo.util.geom.Axis;
import ch.geomo.tramaps.map.MetroMap;
import ch.geomo.tramaps.map.displacement.LineSpaceHandler;
import ch.geomo.util.collection.list.EnhancedList;
import ch.geomo.util.geom.GeomUtil;
import ch.geomo.util.logging.Loggers;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryCollection;
import com.vividsolutions.jts.geom.util.AffineTransformation;
import org.jetbrains.annotations.NotNull;

import java.util.stream.Stream;

/**
 * This {@link LineSpaceHandler} implementation makes space by scaling the underlying
 * graph set a metro map.
 */
public class ScaleHandler implements LineSpaceHandler {

    private static final int MAX_ITERATIONS = 100;

    private final MetroMap map;

    public ScaleHandler(@NotNull MetroMap map) {
        this.map = map;
    }

    private double evaluateScaleFactor(@NotNull EnhancedList<Conflict> conflicts, double mapWidth, double mapHeight) {

        double maxMoveX = conflicts.stream()
                .map(Conflict::getDisplaceDistanceAlongX)
                // .map(d -> 1 + 1 / mapWidth * d)
                .map(d -> (mapWidth + d) / mapWidth)
                .max(Double::compare)
                .orElse(1d);
        double maxMoveY = conflicts.stream()
                .map(Conflict::getDisplaceDistanceAlongY)
                // .map(d -> 1 + 1 / mapHeight * d)
                .map(d -> (mapHeight + d) / mapHeight)
                .max(Double::compare)
                .orElse(1d);

        return Math.max(GeomUtil.makePrecise(Math.max(maxMoveX, maxMoveY)), 1.00001);

    }

    private void scale(@NotNull MetroMap map, double scaleFactor) {

        AffineTransformation scaleTransformation = new AffineTransformation();
        scaleTransformation.scale(scaleFactor, scaleFactor);

        map.getNodes().forEach(node -> {
            Geometry geom = scaleTransformation.transform(node.getGeometry());
            node.updatePosition(geom.getCoordinate());
        });

    }

    private void makeSpace(int lastIteration) {

        int currentIteration = lastIteration + 1;

        EnhancedList<Conflict> conflicts = map.evaluateConflicts(false);

        Loggers.separator(this);
        Loggers.info(this, "Iteration: " + currentIteration);

        if (!conflicts.isEmpty()) {

            Loggers.warning(this, "Conflicts found: " + conflicts.size());

            Stream<Geometry> buffers = conflicts.stream()
                    .flatMap(conflict -> Stream.of(conflict.getBufferA(), conflict.getBufferB()))
                    .map(ElementBuffer::getBuffer);
            GeometryCollection coll = GeomUtil.createCollection(buffers);
            Envelope bbox = coll.getEnvelopeInternal();
            double scaleFactor = evaluateScaleFactor(conflicts, bbox.getWidth(), bbox.getHeight());
            Loggers.info(this, "Use scale factor: " + scaleFactor);
            scale(map, scaleFactor);

            if (currentIteration < MAX_ITERATIONS) {
                // since conflicts between non-neighbours are
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

    private String getBoundingBoxString() {
        Envelope mapBoundingBox = map.getBoundingBox();
        return "Size: " + (int) Math.ceil(mapBoundingBox.getWidth()) + "x" + (int) Math.ceil(mapBoundingBox.getHeight());
    }

    @Override
    public void makeSpace() {
        makeSpace(0);
    }

}
