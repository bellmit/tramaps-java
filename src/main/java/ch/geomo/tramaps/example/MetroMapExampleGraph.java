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

public class MetroMapExampleGraph extends MetroMap {

    public MetroMapExampleGraph(double routeMargin, double edgeMargin) {

        super(routeMargin, edgeMargin);

        Function<Node, NodeSignature> signatureFunction = RectangleStationSignature::new;

        Node a = createNode(150, 200, "A", signatureFunction);
        Node b = createNode(150, 100, "B", signatureFunction);
        Node c = createNode(200, 100, "C", signatureFunction);
        Node d = createNode(200, 150, "D", signatureFunction);
        Node e = createNode(200, 250, "E", signatureFunction);
        Node f = createNode(150, 300, "F", signatureFunction);
        Node g = createNode(100, 300, "G", signatureFunction);
        Node h = createNode(100, 200, "H", signatureFunction);
        Node i = createNode(100, 150, "I", signatureFunction);
        Node j = createNode(150, 250, "J", signatureFunction);
        Node k = createNode(170, 250, "K", signatureFunction);
        Node l = createNode(300, 200, "L", signatureFunction);
        Node n = createNode(100, 250, "N", signatureFunction);
        Node o = createNode(170, 150, "O", signatureFunction);
        Node p = createNode(300, 250, "P", signatureFunction);
        Node q = createNode(400, 100, "Q", signatureFunction);
        Node r = createNode(100, 350, "R", signatureFunction);
        Node s = createNode(150, 50, "S", signatureFunction);

        Route line1 = new Route("U1", 20, Color.BLUE);
        Route line2 = new Route("U2", 20, Color.RED);
        Route line3 = new Route("U3", 20, Color.GREEN);
        Route line4 = new Route("U4", 20, Color.YELLOW);
        Route line5 = new Route("U5", 20, Color.ORANGE);
        Route line6 = new Route("U6", 20, Color.MAGENTA);
        Route line7 = new Route("U7", 20, Color.BLACK);

        createEdge(a, b, line1, line2, line3, line4, line5);
        createEdge(c, b, line1, line2, line4, line5);
        createEdge(c, d, line1, line2, line4, line5);
        createEdge(d, e, line1, line2, line4, line5);
        createEdge(e, f, line1, line2, line4, line5, line6);
        createEdge(f, g, line1, line6);
        createEdge(g, n, line1, line2, line4);
        createEdge(n, h, line1, line2);
        createEdge(h, a, line1, line2, line3, line6, line7);
        createEdge(h, i, line1, line3, line6);
        createEdge(i, b, line1, line2, line3, line4, line5, line6, line7);
        createEdge(i, a, line1, line4, line5);
        createEdge(a, j, line5);
        createEdge(j, k, line5, line2);
        createEdge(l, c, line1, line3);
        createEdge(e, k, line2);
        createEdge(o, k, line2, line5, line4);
        createEdge(o, d, line1, line2, line4, line5, line6);
        createEdge(p, e, line1, line2, line4, line5);
        //createEdge(p, d, line1);
        createEdge(g, r, line1);
        createEdge(f, r, line1);
        createEdge(b, s, line1);
        createEdge(c, s, line1);
        //createEdge(o, b, line1);
        //createEdge(p, q, line1);
        createEdge(l, p, line1);

    }

}
