package ch.geomo.tramaps;

import ch.geomo.tramaps.conflicts.Conflict;
import ch.geomo.tramaps.conflicts.buffer.ElementBuffer;
import ch.geomo.tramaps.geo.Axis;
import ch.geomo.tramaps.geo.util.GeomUtil;
import ch.geomo.tramaps.map.MetroMap;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryCollection;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.util.AffineTransformation;
import com.vividsolutions.jts.math.Vector2D;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DisplacementHandler {

    public double evaluateScaleFactor(Set<Conflict> conflicts, double mapWidth, double mapHeight) {

        double maxMoveX = 0d;
        double maxMoveY = 0d;

        for (Conflict conflict : conflicts) {
            Axis axis = conflict.getBestMoveVectorAxis();
            Vector2D v = conflict.getBestMoveVectorAlongAnAxis();
            if (axis == Axis.X) {
                maxMoveX = Math.max(maxMoveX, v.length());
            }
            else {
                maxMoveY = Math.max(maxMoveY, v.length());
            }
        }

        double scaleFactorAlongX = (mapWidth + maxMoveX) / mapWidth;
        double scaleFactorAlongY = (mapHeight + maxMoveY) / mapHeight;

        return Math.ceil(Math.max(scaleFactorAlongX, scaleFactorAlongY) * 1000) / 1000;

    }

    private void scale(MetroMap map, double scaleFactor) {

        AffineTransformation scaleTransformation = new AffineTransformation();
        scaleTransformation.scale(scaleFactor, scaleFactor);

        map.getNodes().forEach(node -> {
            Geometry geom = scaleTransformation.transform(node.getGeometry());
            node.setCoordinate(geom.getCoordinate());
        });

    }

    public void makeSpaceByScaling(MetroMap map, double routeMargin, double edgeMargin) {
        this.makeSpaceByScaling(map, routeMargin, edgeMargin, 0);
    }

    public void makeSpaceByScaling(MetroMap map, double routeMargin, double edgeMargin, int count) {

        count++;

        System.out.println("makeSpaceByScaling");

        Set<Conflict> conflicts = map.evaluateConflicts(routeMargin, edgeMargin, false)
                .collect(Collectors.toSet());

        if (!conflicts.isEmpty()) {
            Stream<Geometry> buffers = conflicts.stream()
                    .flatMap(conflict -> Stream.of(conflict.getBufferA(), conflict.getBufferB()))
                    .map(ElementBuffer::getBuffer);
            GeometryCollection coll = GeomUtil.createCollection(buffers);
            Envelope bbox = coll.getEnvelopeInternal();
            double scaleFactor = this.evaluateScaleFactor(conflicts, bbox.getWidth(), bbox.getHeight());
            System.out.println("scale factor: " + scaleFactor);
            this.scale(map, scaleFactor);
        }

        // repeat if space issue is not yet solved
        if (conflicts.stream().anyMatch(conflict -> !conflict.isSolved()) && count < 25) {
            this.makeSpaceByScaling(map, routeMargin, edgeMargin, count);
        }

    }

    public void makeSpaceByScalingSubGraphs(MetroMap map, double routeMargin, double edgeMargin) {
        // TODO
    }

    public void makeSpaceByDisplacement(MetroMap map, double routeMargin, double edgeMargin) {
        this.makeSpaceByDisplacement(map, routeMargin, edgeMargin, 0);
    }

    public void makeSpaceByDisplacement(MetroMap map, double routeMargin, double edgeMargin, int count) {

        count++;

        List<Conflict> conflicts = map.evaluateConflicts(routeMargin, edgeMargin, true)
                .peek(conflict -> System.out.println(conflict.getBestMoveVectorAlongAnAxis().length() + " / " + conflict.getMoveVector().length() + " / " + Arrays.asList(conflict.getBufferA().getElement(), conflict.getBufferB().getElement())))
                .collect(Collectors.toList());

        System.out.println("Iteration: " + count);
        System.out.println("Conflicts found: " + conflicts.size());

        if (!conflicts.isEmpty()) {

            Conflict conflict = conflicts.get(0);

            Point centroid = conflict.getConflictPolygon().getCentroid();

            if (conflict.getBestMoveVectorAxis() == Axis.X) {
                map.getNodes().stream()
                        .filter(node -> node.getPoint().getX() > centroid.getX())
//                        .filter(node -> node.getAdjacentEdges().size() > 1 || node.getAdjacentEdges().stream()
//                                .findFirst()
//                                .map(edge -> edge.getOtherNode(node).getX() > centroid.getX())
//                                .orElse(true))
                        .forEach(node -> node.setX(node.getX() + conflict.getBestMoveLengthAlongAnAxis()));
            }
            else {
                map.getNodes().stream()
                        .filter(node -> node.getPoint().getY() > centroid.getY())
//                        .filter(node -> node.getAdjacentEdges().size() > 1 || node.getAdjacentEdges().stream()
//                                .findFirst()
//                                .map(edge -> edge.getOtherNode(node).getY() > centroid.getY())
//                                .orElse(true))
                        .forEach(node -> node.setY(node.getY() + conflict.getBestMoveLengthAlongAnAxis()));
            }

            this.correctMap(map);

            if (count < 100) {
                makeSpaceByDisplacement(map, routeMargin, edgeMargin, count);
            }

        }

    }

    private void correctMap(MetroMap map) {
        System.out.println("Non-Octilinear Edges:" + map.evaluateNonOctilinearEdges().count());
    }

}
