/*
 * Copyright (c) 2016 Thomas Zuberbuehler. All rights reserved.
 */

package ch.geomo.tramaps.geom.util;

import com.vividsolutions.jts.geom.*;
import com.vividsolutions.jts.geom.util.AffineTransformation;
import com.vividsolutions.jts.geom.util.LineStringExtracter;
import com.vividsolutions.jts.operation.polygonize.Polygonizer;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static ch.geomo.tramaps.geom.util.GeomUtil.getGeomUtil;

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

        Envelope envelope = inPolygon.getEnvelopeInternal();

        // scale line string order to be long enough to intersect with the polygons exterior
        double factor = Math.max(envelope.getHeight(), envelope.getWidth()) * 2;
        AffineTransformation scaleTransformation = new AffineTransformation();
        scaleTransformation.scale(factor, factor);
        LineString scaledLineString = getGeomUtil().createLineString(parallelTo.getStartPoint(), getGeomUtil().createPoint(scaleTransformation.transform(parallelTo.getEndPoint())));

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
                .map(geom -> (LineString)geom);

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

    /**
     * Creates polygons from given {@link LineString} or {@link GeometryCollection} of {@link LineString}.
     * <p>
     * Note: Returns an empty {@link GeometryCollection} if {@link Geometry} is not a {@link LineString}
     * or a {@link GeometryCollection} of {@link LineString}.
     *
     * @return a {@link GeometryCollection} of polygons
     * @see <a href="http://suite.opengeo.org/ee/docs/4.5/processing/wpsjava/index.html">OpenGeo Suite Enterprise Docs</a>
     * @see <a href="http://gis.stackexchange.com/a/190002/21355">JTS: split arbitrary polygon by a line (stackoverflow.com)</a>
     */
    @NotNull
    public static GeometryCollection polygonize(@NotNull Geometry geometry) {
        @SuppressWarnings("unchecked")
        List<LineString> lines = LineStringExtracter.getLines(geometry);
        if (lines.isEmpty()) {
            return getGeomUtil().createCollection();
        }
        Polygonizer polygonizer = new Polygonizer();
        polygonizer.add(lines);
        @SuppressWarnings("unchecked")
        Collection<Polygon> polygonCollection = polygonizer.getPolygons();
        return getGeomUtil().createCollection(polygonCollection);
    }

    /**
     * Splits given polygon with given {@link LineString} or collection of {@link LineString}. Returns
     * an empty {@link GeometryCollection}.
     *
     * @return a {@link GeometryCollection} of polygons
     * @see <a href="http://suite.opengeo.org/ee/docs/4.5/processing/wpsjava/index.html">OpenGeo Suite Enterprise Docs</a>
     * @see <a href="http://gis.stackexchange.com/a/190002/21355">JTS: split arbitrary polygon by a line (stackoverflow.com)</a>
     */
    @NotNull
    public static GeometryCollection splitPolygon(@NotNull Geometry polygon, @NotNull Geometry lineString) {
        GeometryCollection lines;
        if (lineString instanceof GeometryCollection) {
            Stream<Geometry> boundaryStream = Stream.of(polygon.getBoundary());
            Stream<Geometry> lineStringStream = getGeomUtil().toStream((GeometryCollection) lineString);
            lines = getGeomUtil().createCollection(Stream.concat(boundaryStream, lineStringStream));
        }
        else {
            lines = (GeometryCollection) polygon.getBoundary().union(lineString);
        }
        Stream<Polygon> stream = getGeomUtil().toStream(polygonize(lines))
                .map(geom -> (Polygon) geom)
                // polygons which are inside the polygon
                .filter(p -> polygon.contains(p.getInteriorPoint()));
        return getGeomUtil().createCollection(stream);
    }

}
