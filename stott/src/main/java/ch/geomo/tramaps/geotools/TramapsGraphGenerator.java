/*
 * Copyright (c) 2016 by Thomas Zuberbühler
 */

package ch.geomo.tramaps.geotools;

import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.graph.build.line.LineStringGraphGenerator;

public class TramapsGraphGenerator extends LineStringGraphGenerator {

    @SuppressWarnings("unused")
    public TramapsGraphGenerator(SimpleFeatureCollection stations) {
        super();
        setGraphBuilder(new TramapsGraphBuilder(stations));
    }

    public TramapsGraphGenerator(double tolerance, SimpleFeatureCollection stations) {
        super(tolerance);
        setGraphBuilder(new TramapsGraphBuilder(stations));
    }

}
