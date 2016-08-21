package ch.geomo.tramaps.test;

import ch.geomo.tramaps.geo.util.GeomUtil;
import ch.geomo.tramaps.graph.Edge;
import ch.geomo.tramaps.graph.Node;
import ch.geomo.tramaps.graph.Route;
import ch.geomo.tramaps.map.MetroMap;
import com.vividsolutions.jts.geom.Coordinate;
import javafx.scene.paint.Color;

import java.util.Arrays;
import java.util.Collections;

public class ExampleMetroMap extends MetroMap {

    public ExampleMetroMap() {

        Node a = new Node(GeomUtil.createPoint(new Coordinate(150, 200)));
        Node b = new Node(GeomUtil.createPoint(new Coordinate(150, 100)));
        Node c = new Node(GeomUtil.createPoint(new Coordinate(200, 100)));
        Node d = new Node(GeomUtil.createPoint(new Coordinate(200, 150)));
        Node e = new Node(GeomUtil.createPoint(new Coordinate(200, 250)));
        Node f = new Node(GeomUtil.createPoint(new Coordinate(150, 300)));
        Node g = new Node(GeomUtil.createPoint(new Coordinate(100, 300)));
        Node h = new Node(GeomUtil.createPoint(new Coordinate(100, 200)));
        Node i = new Node(GeomUtil.createPoint(new Coordinate(100, 150)));

        Node j = new Node(GeomUtil.createPoint(new Coordinate(150, 250)));
        Node k = new Node(GeomUtil.createPoint(new Coordinate(160, 250)));

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

        ab.setRoutes(Arrays.asList(line1, line2, line3, line4, line5));
        bc.setRoutes(Arrays.asList(line1, line2, line4, line5));
        cd.setRoutes(Arrays.asList(line1, line2, line4, line5));
        de.setRoutes(Arrays.asList(line1, line2, line4, line5));
        ef.setRoutes(Arrays.asList(line1, line2, line4, line5, line6));
        fg.setRoutes(Arrays.asList(line1, line6));
        gh.setRoutes(Arrays.asList(line1, line6));
        ha.setRoutes(Arrays.asList(line1, line2, line3, line6, line7));
        hi.setRoutes(Arrays.asList(line1, line3, line6));
        ib.setRoutes(Collections.singletonList(line6));
        ia.setRoutes(Arrays.asList(line1, line4, line5));
        aj.setRoutes(Collections.singletonList(line5));
        jk.setRoutes(Collections.singletonList(line5));

        this.getNodes().addAll(Arrays.asList(a, b, c, d, e, f, g, h, i, j, k));
        this.getEdges().addAll(Arrays.asList(ab, bc, cd, de, ef, fg, gh, ha, hi, ib, ia, aj, jk));

    }

}
