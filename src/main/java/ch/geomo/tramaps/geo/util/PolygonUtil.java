/*
 * Copyright (c) 2016 Thomas Zuberbuehler. All rights reserved.
 */

package ch.geomo.tramaps.geo.util;

import com.vividsolutions.jts.geom.*;
import com.vividsolutions.jts.geom.util.AffineTransformation;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class PolygonUtil {

    private PolygonUtil() {
    }

    /**
     * Returns a {@link Stream} of {@link LineString}s which are parallel to given {@link LineString}
     * and within given {@link Polygon} while one endpoint is equals to a vertex and the other end
     * point of the line string lies on the exterior of the polygon.
     */
    @NotNull
    public static Stream<LineString> findParallelLineString(@NotNull Polygon inPolygon, @NotNull LineString parallelTo) {

        Point centroid = inPolygon.getCentroid();
        Envelope envelope = inPolygon.getEnvelopeInternal();

        // scale line string order to be long enough to intersect with the polygons exterior
        double factor = Math.max(envelope.getHeight(), envelope.getWidth()) * 2;
        AffineTransformation scaleTransformation = new AffineTransformation();
        scaleTransformation.scale(factor, factor);
        LineString scaledLineString = GeomUtil.createLineString(parallelTo.getStartPoint(), GeomUtil.createPoint(scaleTransformation.transform(parallelTo.getEndPoint())));

        return Arrays.stream(inPolygon.getCoordinates())
                .sequential()
                // create a parallel line for each vertex
                .map(vertex -> {
                    AffineTransformation translateTransformation = new AffineTransformation();
                    translateTransformation.translate(vertex.x - scaledLineString.getCentroid().getX(), vertex.y - scaledLineString.getCentroid().getY());
                    return translateTransformation.transform(scaledLineString);
                })
                .filter(Geometry::isValid)
                // get line string within polygon
                .filter(inPolygon::intersects)
                .map(inPolygon::intersection)
                .filter(geom -> geom instanceof LineString && !geom.isEmpty())
                .map(geom -> (LineString) geom);

    }

    /**
     * Finds the longest parallel {@link LineString} parallel to the given {@link LineString} and within
     * the given {@link Polygon}.
     */
    @NotNull
    public static Optional<LineString> findLongestParallelLineString(@NotNull Polygon inPolygon, @NotNull LineString parallelTo) {
        Set<LineString> lines = PolygonUtil.findParallelLineString(inPolygon, parallelTo)
                .collect(Collectors.toSet());

        return lines.stream()
                .max((l1, l2) -> Double.compare(l1.getLength(), l2.getLength()));
    }

}
