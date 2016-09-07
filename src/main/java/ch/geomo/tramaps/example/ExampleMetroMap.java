/*
 * Copyright (c) 2016 Thomas Zuberbuehler. All rights reserved.
 */

package ch.geomo.tramaps.example;

import ch.geomo.tramaps.graph.Edge;
import ch.geomo.tramaps.graph.Node;
import ch.geomo.tramaps.graph.Route;
import ch.geomo.tramaps.map.MetroMap;
import ch.geomo.tramaps.map.signature.NodeSignature;
import ch.geomo.tramaps.map.signature.RectangleStationSignature;
import ch.geomo.tramaps.map.signature.SquareStationSignature;
import com.vividsolutions.jts.geom.Coordinate;
import javafx.scene.paint.Color;

import java.util.function.Function;

import static ch.geomo.tramaps.geom.util.GeomUtil.getGeomUtil;

public class ExampleMetroMap extends MetroMap {

    public ExampleMetroMap(double routeMargin, double edgeMargin) {

        super(routeMargin, edgeMargin);

        Function<Node, NodeSignature> signatureFunction = RectangleStationSignature::new;

        Node a = new Node(getGeomUtil().createPoint(new Coordinate(150, 200)), signatureFunction);
        Node b = new Node(getGeomUtil().createPoint(new Coordinate(150, 100)), signatureFunction);
        Node c = new Node(getGeomUtil().createPoint(new Coordinate(200, 100)), signatureFunction);
        Node d = new Node(getGeomUtil().createPoint(new Coordinate(200, 150)), signatureFunction);
        Node e = new Node(getGeomUtil().createPoint(new Coordinate(200, 250)), signatureFunction);
        Node f = new Node(getGeomUtil().createPoint(new Coordinate(150, 300)), signatureFunction);
        Node g = new Node(getGeomUtil().createPoint(new Coordinate(100, 300)), signatureFunction);
        Node h = new Node(getGeomUtil().createPoint(new Coordinate(100, 200)), signatureFunction);
        Node i = new Node(getGeomUtil().createPoint(new Coordinate(100, 150)), signatureFunction);
        Node j = new Node(getGeomUtil().createPoint(new Coordinate(150, 250)), signatureFunction);
        Node k = new Node(getGeomUtil().createPoint(new Coordinate(170, 250)), signatureFunction);
        Node l = new Node(getGeomUtil().createPoint(new Coordinate(300, 200)), signatureFunction);
//        Node m = new Node(getGeomUtil().createPoint(new Coordinate(302, 302)), signatureFunction);

        Node n = new Node(getGeomUtil().createPoint(new Coordinate(100, 250)), signatureFunction);
        Node o = new Node(getGeomUtil().createPoint(new Coordinate(170, 150)), signatureFunction);
        Node p = new Node(getGeomUtil().createPoint(new Coordinate(300, 250)), signatureFunction);

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
        l.setName("L");
//        m.setName("M");
        n.setName("N");
        o.setName("O");
        p.setName("P");

        Edge ab = new Edge(a, b);
        Edge bc = new Edge(c, b);
        Edge cd = new Edge(c, d);
        Edge de = new Edge(d, e);
        Edge ef = new Edge(e, f);
        Edge fg = new Edge(f, g);
//        Edge gh = new Edge(g, h);
        Edge gn = new Edge(g, n);
        Edge nh = new Edge(n, h);
        Edge ha = new Edge(h, a);
        Edge hi = new Edge(h, i);
        Edge ib = new Edge(i, b);
        Edge ia = new Edge(i, a);
        Edge aj = new Edge(a, j);
        Edge jk = new Edge(j, k);
        Edge lc = new Edge(l, c);
//        Edge lm = new Edge(l, m);
//        Edge le = new Edge(l, e);
//        Edge jf = new Edge(h, f);

//        Edge bd = new Edge(b, d);
        //Edge ad = new Edge(a, d);
        //Edge kd = new Edge(k, d);
        //Edge kb = new Edge(k, b);
        Edge ek = new Edge(e, k);
        Edge ok = new Edge(o, k);
        Edge od = new Edge(o, d);
        Edge pe = new Edge(p, e);
//        Edge pl = new Edge(p, l);

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
//        gh.addRoutes(line1, line6);
        gn.addRoutes(line1, line2, line4);
        nh.addRoutes(line1, line2);
        ha.addRoutes(line1, line2, line3, line6, line7);
        hi.addRoutes(line1, line3, line6);
        ib.addRoutes(line6);
        ia.addRoutes(line1, line4, line5);
        aj.addRoutes(line5);
        jk.addRoutes(line5, line2);
//        jf.addRoutes(line5);
        lc.addRoutes(line1, line3);
//        le.addRoutes(line1, line3);
        //kb.addRoutes(line1);
        ek.addRoutes(line2);
        ok.addRoutes(line2, line5, line4);
        od.addRoutes(line1, line2, line4, line5, line6);
        pe.addRoutes(line1, line2, line4, line5);
//        pl.addRoutes(line2);

        addNodes(a, b, c, d, e, f, g, h, i, j, k, n, l, o, p);
//        addNodes(a, b, c, d, e, f, g, h, i, j, k, l);

    }

}
