/*
 * Copyright (c) 2016 by Thomas Zuberbühler
 */

package ch.geomo.tramaps;

import ch.geomo.tramaps.criteria.NodeCriteriaCalculator;
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
                .setGridSpacing(50)
                .setGraph(new GeoGraph(new ArrayList<>(nodes.values()), edges))
                .build();

//        System.out.println(map.getEdges());

        map.getNodes().stream()
//                .forEach(n -> System.out.println(n.getEdges()));
                .filter(n -> n.getEdges().size() > 3)
                .findFirst()
                .ifPresent(n -> System.out.println(n.getEdges()));

        NodeCriteriaCalculator calculator = new NodeCriteriaCalculator(2, 50);
        System.out.println(calculator.calculate(map.getNodes(), map.getEdges()));

//        MetroMapExporter exporter = new MetroMapExporter();
//        exporter.toCsv("/Users/thozub/Repositories/tramaps/stott/out", map);

    }

}
