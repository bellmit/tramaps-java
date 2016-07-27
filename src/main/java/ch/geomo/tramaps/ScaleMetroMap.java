package ch.geomo.tramaps;

import ch.geomo.tramaps.geom.Axis;
import ch.geomo.tramaps.geom.GeomUtil;
import ch.geomo.tramaps.graph.Edge;
import ch.geomo.tramaps.graph.Node;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.util.AffineTransformation;
import com.vividsolutions.jts.math.Vector2D;

import java.util.Set;

public class ScaleMetroMap {

    public double evaluateScaleFactor(Set<Conflict> conflicts, double mapWidth, double mapHeight) {

        double maxMoveX = 0d;
        double maxMoveY = 0d;

        for(Conflict conflict : conflicts) {
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

        return Math.max(scaleFactorAlongX, scaleFactorAlongY);

    }

    private void scale(MetroMap map, double scaleFactor) {

        AffineTransformation scaleTransformation = new AffineTransformation();
        scaleTransformation.scale(scaleFactor, scaleFactor);

        map.getNodes().parallelStream()
                .forEach(node -> {
                    Geometry geom = scaleTransformation.transform(node.getGeometry());
                    node.setCoordinate(geom.getCoordinate());
                });

    }

    public void makeSpaceByScaling(MetroMap map, double routeMargin, double edgeMargin) {

        Set<Edge> edges = map.getEdges();
        Set<Node> nodes = map.getNodes();

        Envelope bbox = map.getBoundingBox();

        Set<Conflict> conflicts = new ConflictFinder(routeMargin, edgeMargin).getConflicts(edges, nodes);

        if (!conflicts.isEmpty()) {
            double scaleFactor = this.evaluateScaleFactor(conflicts, bbox.getWidth(), bbox.getHeight());
            this.scale(map, scaleFactor);
        }
    }

}
