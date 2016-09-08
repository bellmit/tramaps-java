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
import com.vividsolutions.jts.geom.Coordinate;
import javafx.scene.paint.Color;

import java.util.function.Function;

import static ch.geomo.tramaps.geom.util.GeomUtil.getGeomUtil;

public class ExampleMetroMap extends MetroMap {

    public ExampleMetroMap(double routeMargin, double edgeMargin) {

        super(routeMargin, edgeMargin);

        Function<Node, NodeSignature> signatureFunction = RectangleStationSignature::new;

        Node a = new Node(150, 200, "A", signatureFunction);
        Node b = new Node(150, 100, "B", signatureFunction);
        Node c = new Node(200, 100, "C", signatureFunction);
        Node d = new Node(200, 150, "D", signatureFunction);
        Node e = new Node(200, 250, "E", signatureFunction);
        Node f = new Node(150, 300, "F", signatureFunction);
        Node g = new Node(100, 300, "G", signatureFunction);
        Node h = new Node(100, 200, "H", signatureFunction);
        Node i = new Node(100, 150, "I", signatureFunction);
        Node j = new Node(150, 250, "J", signatureFunction);
        Node k = new Node(170, 250, "K", signatureFunction);
        Node l = new Node(300, 200, "L", signatureFunction);
        Node n = new Node(100, 250, "N", signatureFunction);
        Node o = new Node(170, 150, "O", signatureFunction);
        Node p = new Node(300, 250, "P", signatureFunction);
        Node q = new Node(400, 100, "Q", signatureFunction);
        Node r = new Node(100, 350, "R", signatureFunction);

        Edge ab = new Edge(a, b);
        Edge bc = new Edge(c, b);
        Edge cd = new Edge(c, d);
        Edge de = new Edge(d, e);
        Edge ef = new Edge(e, f);
        Edge fg = new Edge(f, g);
        Edge gn = new Edge(g, n);
        Edge nh = new Edge(n, h);
        Edge ha = new Edge(h, a);
        Edge hi = new Edge(h, i);
        Edge ib = new Edge(i, b);
        Edge ia = new Edge(i, a);
        Edge aj = new Edge(a, j);
        Edge jk = new Edge(j, k);
        Edge lc = new Edge(l, c);
        Edge ek = new Edge(e, k);
        Edge ok = new Edge(o, k);
        Edge od = new Edge(o, d);
        Edge pe = new Edge(p, e);
        //Edge gr = new Edge(g, r);

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
        ib.addRoutes(line1, line2, line3, line4, line5, line6, line7);
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
//        lq.addRoutes(line2, line3);
//        qc.addRoutes(line2);
//        pl.addRoutes(line2);

        addNodes(a, b, c, d, e, f, g, h, i, j, k, n, l, o, p);
//        addNodes(a, b, c, d, e, f, g, h, i, j, k, l);

    }

}
