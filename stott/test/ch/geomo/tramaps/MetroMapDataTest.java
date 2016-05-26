/*
 * Copyright (c) 2016 by Thomas Zuberbühler
 */

package ch.geomo.tramaps;

import ch.geomo.tramaps.graph.geo.GeoEdge;
import ch.geomo.tramaps.graph.geo.GeoGraph;
import ch.geomo.tramaps.graph.geo.GeoNode;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MetroMapDataTest {

    @Test
    public void test() throws IOException {

        MetroMapDataLoader loader = new MetroMapDataLoader();

        Map<String, GeoNode> nodes = loader.readNodes();
        List<GeoEdge> edges = loader.readEdges(nodes);

        MetroMap map = new MetroMapBuilder()
                .setGraph(new GeoGraph(new ArrayList<>(nodes.values()), edges))
                .setGridSpacing(50)
                .setRadius(4)
                .setMinIteration(1)
                .setMaxIteration(10)
                .setMultiplicator(1.25)
                .build();

//        System.out.println(map.getEdges());

        map.getNodes().stream()
//                .forEach(n -> System.out.println(n.getEdges()));
                .filter(n -> n.getEdges().size() > 3)
                .findFirst()
                .ifPresent(n -> System.out.println(n.getEdges()));

//        NodeCriteriaHandler calculator = new NodeCriteriaHandler(2, 50);
//        System.out.println(calculator._calculate(map.getNodes(), map.getEdges()));

        MetroMapExporter exporter = new MetroMapExporter();
        exporter.toCsv("/Users/thozub/Repositories/tramaps/stott/out", map);

    }

}
