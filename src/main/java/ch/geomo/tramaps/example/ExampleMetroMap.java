/*
 * Copyright (c) 2016 Thomas Zuberbuehler. All rights reserved.
 */

package ch.geomo.tramaps.example;

import ch.geomo.tramaps.geo.util.GeomUtil;
import ch.geomo.tramaps.graph.Edge;
import ch.geomo.tramaps.graph.Node;
import ch.geomo.tramaps.graph.Route;
import ch.geomo.tramaps.map.MetroMap;
import ch.geomo.tramaps.map.signature.RectangleStationSignature;
import com.vividsolutions.jts.geom.Coordinate;
import javafx.scene.paint.Color;

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

        a.setName("A");
        b.setName("B");
        c.setName("C");
        d.setName("D");
        e.setName("E");
        f.setName("F");
        g.setName("G");
        h.setName("H");
        i.setName("I");
        j.setName("J");
        k.setName("K");

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
        //Edge ia = new Edge(i, a);
        Edge aj = new Edge(a, j);
        Edge jk = new Edge(j, k);

        //Edge bd = new Edge(b, d);
        //Edge ad = new Edge(a, d);
        //Edge kd = new Edge(k, d);
        //Edge kb = new Edge(k, b);

        Route line1 = new Route(20, Color.BLUE);
        Route line2 = new Route(20, Color.RED);
        Route line3 = new Route(20, Color.GREEN);
        Route line4 = new Route(20, Color.YELLOW);
        Route line5 = new Route(20, Color.ORANGE);
        Route line6 = new Route(20, Color.MAGENTA);
        Route line7 = new Route(20, Color.BLACK);

        ab.addRoutes(line1, line2, line3, line4, line5);
        bc.addRoutes(line1, line2, line4, line5);
        cd.addRoutes(line1, line2, line4, line5);
        de.addRoutes(line1, line2, line4, line5);
        ef.addRoutes(line1, line2, line4, line5, line6);
        fg.addRoutes(line1, line6);
        gh.addRoutes(line1, line6);
        ha.addRoutes(line1, line2, line3, line6, line7);
        hi.addRoutes(line1, line3, line6);
        ib.addRoutes(line6);
        //ia.addRoutes(line1, line4, line5);
        aj.addRoutes(line5);
        jk.addRoutes(line5);
        //kb.addRoutes(line1);

        addNodes(a, b, c, d, e, f, g, h, i, j, k);

    }

}
