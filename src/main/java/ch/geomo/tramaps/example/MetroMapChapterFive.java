/*
 * Copyright (c) 2016-2018 Thomas Zuberbuehler. All rights reserved.
 */

package ch.geomo.tramaps.example;

import ch.geomo.tramaps.graph.Node;
import ch.geomo.tramaps.graph.Route;
import ch.geomo.tramaps.map.MetroMap;
import ch.geomo.tramaps.map.signature.NodeSignature;
import ch.geomo.tramaps.map.signature.SquareStationSignature;
import javafx.scene.paint.Color;

import java.util.function.Function;

public class MetroMapChapterFive extends MetroMap {

    public MetroMapChapterFive() {

        super(1, 25, 25);

        Route line1 = new Route("U1", 20, Color.BLUE);
        Route line2 = new Route("U2", 20, Color.RED);
        Route line3 = new Route("U3", 20, Color.GREEN);
        Route line4 = new Route("U4", 20, Color.YELLOW);

        Function<Node, NodeSignature> signatureFunction = SquareStationSignature::new;

        Node a = createNode(0, 30, "A", signatureFunction);
        Node b = createNode(80, 30, "B", signatureFunction);
        Node c = createNode(110, 0, "C", signatureFunction);
        Node d = createNode(140, 30, "D", signatureFunction);
        Node e = createNode(110, 60, "E", signatureFunction);
        Node f = createNode(50, 60, "F", signatureFunction);

        createEdge(a, b, line1, line2, line3, line4);
        createEdge(c, b, line1, line2);
        createEdge(c, d, line1);
        createEdge(d, e, line1);
        createEdge(e, f, line1, line3, line4);
        createEdge(b, e, line3, line4);

    }

}
