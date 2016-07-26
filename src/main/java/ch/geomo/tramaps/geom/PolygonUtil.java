package ch.geomo.tramaps.geom;

import com.vividsolutions.jts.geom.*;
import com.vividsolutions.jts.geom.util.AffineTransformation;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Optional;
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
        Geometry scaledLineString = scaleTransformation.transform(parallelTo);

        return Arrays.stream(inPolygon.getCoordinates())
                .parallel()
                // create a parallel line for each vertex
                .map(vertex -> {
                    AffineTransformation translateTransformation = new AffineTransformation();
                    translateTransformation.translate(vertex.x - centroid.getX(), vertex.y - centroid.getY());
                    return translateTransformation.transform(scaledLineString);
                })
                // get line string within polygon
                .map(inPolygon::intersection)
                .map(geom -> (LineString) geom);

    }

    /**
     * Finds the longest parallel {@link LineString} parallel to the given {@link LineString} and within
     * the given {@link Polygon}.
     */
    @NotNull
    public static Optional<LineString> findLongestParallelLineString(@NotNull Polygon inPolygon, @NotNull LineString parallelTo) {
        return PolygonUtil.findParallelLineString(inPolygon, parallelTo)
                .max((l1, l2) -> Double.compare(l1.getLength(), l2.getLength()));
    }

}
