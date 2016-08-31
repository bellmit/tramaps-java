package ch.geomo.tramaps.example;

import ch.geomo.tramaps.geo.util.GeomUtil;
import ch.geomo.tramaps.graph.Edge;
import ch.geomo.tramaps.graph.Node;
import ch.geomo.tramaps.graph.Route;
import ch.geomo.tramaps.map.MetroMap;
import ch.geomo.tramaps.map.signature.RectangleStationSignature;
import com.vividsolutions.jts.geom.Coordinate;
import javafx.scene.paint.Color;

import java.util.Arrays;
import java.util.Collections;

public class ExampleMetroMap extends MetroMap {

    public ExampleMetroMap() {

        Node a = new Node(GeomUtil.createPoint(new Coordinate(150, 200)), RectangleStationSignature::new);
        Node b = new Node(GeomUtil.createPoint(new Coordinate(150, 100)), RectangleStationSignature::new);
        Node c = new Node(GeomUtil.createPoint(new Coordinate(200, 100)), RectangleStationSignature::new);
        Node d = new Node(GeomUtil.createPoint(new Coordinate(200, 150)), RectangleStationSignature::new);
        Node e = new Node(GeomUtil.createPoint(new Coordinate(200, 250)), RectangleStationSignature::new);
        Node f = new Node(GeomUtil.createPoint(new Coordinate(150, 300)), RectangleStationSignature::new);
        Node g = new Node(GeomUtil.createPoint(new Coordinate(100, 300)), RectangleStationSignature::new);
        Node h = new Node(GeomUtil.createPoint(new Coordinate(100, 200)), RectangleStationSignature::new);
        Node i = new Node(GeomUtil.createPoint(new Coordinate(100, 150)), RectangleStationSignature::new);

        Node j = new Node(GeomUtil.createPoint(new Coordinate(150, 250)), RectangleStationSignature::new);
        Node k = new Node(GeomUtil.createPoint(new Coordinate(160, 250)), RectangleStationSignature::new);

        Edge ab = new Edge(a, b);
        Edge bc = new Edge(b, c);
        Edge cd = new Edge(c, d);
        Edge de = new Edge(d, e);
        Edge ef = new Edge(e, f);
        Edge fg = new Edge(f, g);
        Edge gh = new Edge(g, h);
        Edge ha = new Edge(h, a);
        Edge hi = new Edge(h, i);
        Edge ib = new Edge(i, b);
        Edge ia = new Edge(i, a);
        Edge aj = new Edge(a, j);
        Edge jk = new Edge(j, k);

        Route line1 = new Route(20, Color.BLUE);
        Route line2 = new Route(20, Color.RED);
        Route line3 = new Route(20, Color.GREEN);
        Route line4 = new Route(20, Color.YELLOW);
        Route line5 = new Route(20, Color.ORANGE);
        Route line6 = new Route(20, Color.MAGENTA);
        Route line7 = new Route(20, Color.BLACK);

        ab.addRoutes(Arrays.asList(line1, line2, line3, line4, line5));
        bc.addRoutes(Arrays.asList(line1, line2, line4, line5));
        cd.addRoutes(Arrays.asList(line1, line2, line4, line5));
        de.addRoutes(Arrays.asList(line1, line2, line4, line5));
        ef.addRoutes(Arrays.asList(line1, line2, line4, line5, line6));
        fg.addRoutes(Arrays.asList(line1, line6));
        gh.addRoutes(Arrays.asList(line1, line6));
        ha.addRoutes(Arrays.asList(line1, line2, line3, line6, line7));
        hi.addRoutes(Arrays.asList(line1, line3, line6));
        ib.addRoutes(Collections.singletonList(line6));
        ia.addRoutes(Arrays.asList(line1, line4, line5));
        aj.addRoutes(Collections.singletonList(line5));
        jk.addRoutes(Collections.singletonList(line5));

        getNodes().addAll(Arrays.asList(a, b, c, d, e, f, g, h, i, j, k));
        getEdges().addAll(Arrays.asList(ab, bc, cd, de, ef, fg, gh, ha, hi, ib, ia, aj, jk));

    }

}
