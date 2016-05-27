/*
 * Copyright (c) 2016 by Thomas Zuberbühler
 */

package ch.geomo.tramaps;

public class MetroMapDataLoader {

//    public Map<String, GeoNode> readNodes() throws IOException {
//
//        Map<String, GeoNode> nodes = new HashMap<>();
//
//        ICsvBeanReader reader = null;
//        try {
//
//            reader = new CsvBeanReader(new FileReader("/Users/thozub/Repositories/tramaps/stott/data/nodes2.csv"), CsvPreference.EXCEL_NORTH_EUROPE_PREFERENCE);
//            reader.getHeader(true);
//
//            final String[] header = new String[]{null, "name", "label", "x", "y", null, null, null, null, null};
//            final CellProcessor[] processors = new CellProcessor[]{null, new NotNull(), new ParseLabel(), new NotNull(new ParseDouble()), new NotNull(new ParseDouble()), null, null, null, null, null};
//
//            GeoNode node;
//            while ((node = reader.read(GeoNode.class, header, processors)) != null) {
//                nodes.put(node.getName(), node);
//            }
//
//        }
//        finally {
//            if (reader != null) {
//                reader.close();
//            }
//        }
//
//        return nodes;
//
//    }
//
//    public List<GeoEdge> readEdges(Map<String, GeoNode> nodes) throws IOException {
//
//        List<GeoEdge> edges = new ArrayList<>();
//
//        ICsvBeanReader reader = null;
//        try {
//
//            reader = new CsvBeanReader(new FileReader("/Users/thozub/Repositories/tramaps/stott/data/segments.csv"), CsvPreference.EXCEL_NORTH_EUROPE_PREFERENCE);
//            reader.getHeader(true);
//
//            ParseEdgeNode edgeNodeParser = new ParseEdgeNode(nodes);
//
//            final String[] header = new String[]{null, null, null, "name", "firstNode", "secondNode"};
//            final CellProcessor[] processors = new CellProcessor[]{null, null, null, new NotNull(), edgeNodeParser, edgeNodeParser};
//
//            GeoEdge edge;
//            while ((edge = reader.read(GeoEdge.class, header, processors)) != null) {
//
//                if (edge.getNodes() == null) {
//                    continue;
//                }
//
//                GeoNode first = edge.getFirstNode();
//                GeoNode second = edge.getSecondNode();
//
//                if (first == null || second == null) {
//                    continue;
//                }
//
//                edges.add(edge);
//
//                // add bi-directional dependency
//                first.addEdge(edge);
//                second.addEdge(edge);
//
//            }
//
//        }
//        finally {
//            if (reader != null) {
//                reader.close();
//            }
//        }
//
//        return edges;
//
//    }

}
