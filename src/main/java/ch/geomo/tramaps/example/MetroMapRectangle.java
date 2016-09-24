/*
 * Copyright (c) 2016 Thomas Zuberbuehler. All rights reserved.
 */

package ch.geomo.tramaps.example;

import ch.geomo.tramaps.graph.Node;
import ch.geomo.tramaps.graph.Route;
import ch.geomo.tramaps.map.MetroMap;
import ch.geomo.tramaps.map.signature.NodeSignature;
import ch.geomo.tramaps.map.signature.SquareStationSignature;
import javafx.scene.paint.Color;

import java.util.function.Function;

/**
 * Created by thozub on 24.09.16.
 */
public class MetroMapRectangle extends MetroMap {

    public MetroMapRectangle() {

        super(1, 25, 25);

        Route line1 = new Route("U1", 20, Color.BLUE);
        Route line2 = new Route("U2", 20, Color.RED);
        Route line3 = new Route("U3", 20, Color.GREEN);
        Route line4 = new Route("U4", 20, Color.YELLOW);

        Function<Node, NodeSignature> signatureFunction = SquareStationSignature::new;

        Node a = createNode(100, 100, "A", signatureFunction);
        Node b = createNode(300, 100, "B", signatureFunction);
        Node c = createNode(300, 200, "C", signatureFunction);
        Node d = createNode(100, 200, "D", signatureFunction);

        createEdge(a, b, line1, line2, line3, line4);
        createEdge(b, c, line1, line2);
        createEdge(c, d, line1, line2);
        createEdge(d, a, line1, line2);

    }

}
