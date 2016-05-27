/*
 * Copyright (c) 2016 by Thomas Zuberbühler
 */

package ch.geomo.tramaps.geotools;

import org.geotools.graph.build.line.LineStringGraphGenerator;

public class TramapsGraphGenerator extends LineStringGraphGenerator {

    private TramapsGraphBuilder builder;

    public TramapsGraphGenerator() {
        super();
        setGraphBuilder(new TramapsGraphBuilder());
    }

    public TramapsGraphGenerator(double tolerance) {
        super(tolerance);
        setGraphBuilder(new TramapsGraphBuilder());
    }

}
