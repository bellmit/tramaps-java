package ch.geomo.tramaps;

import ch.geomo.tramaps.conflicts.Conflict;
import ch.geomo.tramaps.conflicts.ConflictFinder;
import ch.geomo.tramaps.geo.Axis;
import ch.geomo.tramaps.geo.util.GeomUtil;
import ch.geomo.tramaps.graph.Edge;
import ch.geomo.tramaps.graph.Node;
import ch.geomo.tramaps.graph.Route;
import ch.geomo.tramaps.map.MetroMap;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.util.AffineTransformation;
import com.vividsolutions.jts.math.Vector2D;
import org.geotools.geometry.jts.JTSFactoryFinder;

import java.awt.*;
import java.util.Arrays;
import java.util.Set;

public class DisplacementHandler {

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

        map.getNodes().stream()
                .forEach(node -> {
                    Geometry geom = scaleTransformation.transform(node.getGeometry());
                    node.setCoordinate(geom.getCoordinate());
                });

    }

    public void makeSpaceByScaling(MetroMap map, double routeMargin, double edgeMargin) {

        Set<Edge> edges = map.getEdges();
        Set<Node> nodes = map.getNodes();

        Set<Conflict> conflicts = new ConflictFinder(routeMargin, edgeMargin).getConflicts(edges, nodes);

        if (!conflicts.isEmpty()) {
            Envelope bbox = map.getBoundingBox();
            double scaleFactor = this.evaluateScaleFactor(conflicts, bbox.getWidth(), bbox.getHeight());
            this.scale(map, scaleFactor);
        }

    }

    public void makeSpaceByScalingSubGraphs(MetroMap map, double routeMargin, double edgeMargin) {
        // TODO
    }

    public void makeSpaceByDisplacement(MetroMap map, double routeMargin, double edgeMargin) {

        Set<Edge> edges = map.getEdges();
        Set<Node> nodes = map.getNodes();

        Set<Conflict> conflicts = new ConflictFinder(routeMargin, edgeMargin).getConflicts(edges, nodes);
        // TODO

    }

    public static void main(String[] args) {

        MetroMap map = new MetroMap();

        Node a = new Node(GeomUtil.createPoint(new Coordinate(50, 100)));
        Node b = new Node(GeomUtil.createPoint(new Coordinate(50, 0)));
        Node c = new Node(GeomUtil.createPoint(new Coordinate(100, 0)));
        Node d = new Node(GeomUtil.createPoint(new Coordinate(100, 50)));
        Node e = new Node(GeomUtil.createPoint(new Coordinate(100, 150)));
        Node f = new Node(GeomUtil.createPoint(new Coordinate(50, 200)));
        Node g = new Node(GeomUtil.createPoint(new Coordinate(0, 200)));

        Edge ab = new Edge(a, b);
        Edge bc = new Edge(b, c);
        Edge cd = new Edge(c, d);
        Edge de = new Edge(d, e);
        Edge ef = new Edge(e, f);
        Edge fg = new Edge(f, g);

        Route line1 = new Route(20, Color.blue);
        Route line2 = new Route(20, Color.red);
        Route line3 = new Route(20, Color.green);
        Route line4 = new Route(20, Color.yellow);
        Route line5 = new Route(20, Color.orange);
        Route line6 = new Route(20, Color.magenta);

        ab.setRoutes(Arrays.asList(line1, line2, line3, line4, line5));
        bc.setRoutes(Arrays.asList(line1, line2, line4, line5));
        cd.setRoutes(Arrays.asList(line1, line2, line4, line5));
        de.setRoutes(Arrays.asList(line1, line2, line4, line5));
        ef.setRoutes(Arrays.asList(line1, line2, line4, line5, line6));
        fg.setRoutes(Arrays.asList(line1, line6));

        map.getNodes().addAll(Arrays.asList(a, b, c, d, e, f, g));
        map.getEdges().addAll(Arrays.asList(ab, bc, cd, de, ef, fg));

        DisplacementHandler handler = new DisplacementHandler();
        System.out.println("Before Scaling:");
        System.out.println(map);
        handler.makeSpaceByScaling(map, 5, 5);
        System.out.println("Scaled Map:");
        System.out.println(map);
        System.out.println("Finish");

    }

}
