package ch.geomo.tramaps.geom;

import com.vividsolutions.jts.geom.*;
import com.vividsolutions.jts.geom.util.AffineTransformation;
import org.jetbrains.annotations.NotNull;
import org.opengis.referencing.operation.TransformException;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class PolygonUtil {

    private PolygonUtil() {
    }

    @NotNull
    public static Stream<LineString> findParallelLineStringStream(@NotNull Polygon inPolygon, @NotNull LineString parallelTo) throws TransformException {

        Point centroid = inPolygon.getCentroid();
        Envelope envelope = inPolygon.getEnvelopeInternal();

        // scale line string order to be long enough to intersect with the polygons exterior
        double factor = Math.max(envelope.getHeight(), envelope.getWidth())*2;
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

    public static Set<LineString> findParallelLineStringSet(@NotNull Polygon inPolygon, @NotNull LineString parallelTo) throws TransformException {
        return findParallelLineStringStream(inPolygon, parallelTo).collect(Collectors.toSet());
    }

}
