/*
 * Copyright (c) 2016 by Thomas Zuberbühler
 */

package ch.geomo.tramaps;

import ch.geomo.tramaps.geotools.TramapsGraphGenerator;
import ch.geomo.tramaps.grid.GridEdge;
import ch.geomo.tramaps.grid.GridGraph;
import ch.geomo.tramaps.grid.GridNode;
import org.geotools.data.DataUtilities;
import org.geotools.data.FileDataStore;
import org.geotools.data.FileDataStoreFinder;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.feature.DefaultFeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.geotools.feature.SchemaException;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.graph.build.feature.FeatureGraphGenerator;
import org.geotools.map.FeatureLayer;
import org.geotools.map.Layer;
import org.geotools.map.MapContent;
import org.geotools.styling.SLD;
import org.geotools.styling.Style;
import org.geotools.swing.JMapFrame;
import org.opengis.feature.Feature;
import org.opengis.feature.simple.SimpleFeature;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class MetroMapMain {

    public MetroMapMain() throws SchemaException {
    }

    public static void main(String[] args) throws IOException, SchemaException {

//        File home = new File("/Users/thozub/Repositories/tramaps/stott/data/shp");
//        File segments = JFileDataStoreChooser.showOpenFile("shp", home, null);

//        File segments = new File("/Users/thozub/Repositories/tramaps-java/stott/data/shp/ZH.shp");

        File segments = new File("/Users/thozub/Repositories/tramaps-java/stott/data/shp/city2.shp");
        File stations = new File("/Users/thozub/Repositories/tramaps-java/stott/data/shp/stations2.shp");

        FileDataStore store = FileDataStoreFinder.getDataStore(segments);
        SimpleFeatureSource featureSource = store.getFeatureSource();

        FileDataStore stationStore = FileDataStoreFinder.getDataStore(stations);
        SimpleFeatureSource stationFeatureSource = stationStore.getFeatureSource();

        SimpleFeatureCollection featureCollection = featureSource.getFeatures();
        TramapsGraphGenerator generator = new TramapsGraphGenerator(50, stationFeatureSource.getFeatures());
        FeatureGraphGenerator featureGenerator = new FeatureGraphGenerator(generator);

        try (FeatureIterator iter = featureCollection.features()) {
            while (iter.hasNext()) {
                Feature feature = iter.next();
                featureGenerator.add(feature);
            }
        }
        GridGraph graph = (GridGraph) featureGenerator.getGraph();

        new MetroMapBuilder()
                .setGraph(graph)
                .setSpacing(500)
                .setMaxIteration(12)
                .setInitialSearchDistance(16)
                .build();

        System.out.println(graph);

        List<SimpleFeature> lineFeatures = graph.getEdges().parallelStream()
                .map(GridEdge::getSimpleFeature)
                .collect(Collectors.toList());

        DefaultFeatureCollection graphLineFeatures = new DefaultFeatureCollection();
        graphLineFeatures.addAll(lineFeatures);

        SimpleFeatureSource graphLineFeatureSource = DataUtilities.source(graphLineFeatures);

        MapContent map = new MapContent();

        Style style = SLD.createSimpleStyle(graphLineFeatureSource.getSchema());
        Layer layer = new FeatureLayer(graphLineFeatureSource, style);
        map.layers().add(layer);

//        List<SimpleFeature> pointFeatures = graph.getEdges().parallelStream()
//                .flatMap(GridEdge::getNodeStream)
//                .map(GridNode::getSimpleFeature)
//                .distinct()
//                .collect(Collectors.toList());
//
//        DefaultFeatureCollection graphPointFeatures = new DefaultFeatureCollection();
//        graphPointFeatures.addAll(pointFeatures);
//
//        SimpleFeatureSource graphPointFeatureSource = DataUtilities.source(graphPointFeatures);
//
//        Style style2 = SLD.createSimpleStyle(graphPointFeatureSource.getSchema());
//        Layer layer2 = new FeatureLayer(graphPointFeatureSource, style2);
//        map.layers().add(layer2);

        JMapFrame mapFrame = new JMapFrame(map);
        mapFrame.setSize(1200, 900);
        mapFrame.setVisible(true);

    }

}
