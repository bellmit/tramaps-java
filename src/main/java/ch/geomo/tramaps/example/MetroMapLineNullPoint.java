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

public class MetroMapLineNullPoint extends MetroMap {

    public MetroMapLineNullPoint() {

        super(1, 15, 25);

        Route line1 = new Route("U1", 20, Color.BLUE);
        Route line2 = new Route("U2", 20, Color.RED);
        Route line3 = new Route("U3", 20, Color.GREEN);
        Route line4 = new Route("U4", 20, Color.YELLOW);

        Function<Node, NodeSignature> signatureFunction = SquareStationSignature::new;

        Node a = createNode(-10, -10, "A", signatureFunction);
        Node b = createNode(10, 10, "B", signatureFunction);
        //Node c = createNode(10, 60, "C", signatureFunction);

        createEdge(a, b, line1, line2, line3, line4);
        //createEdge(a, c, line1, line2);

    }
}
