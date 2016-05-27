/*
 * Copyright (c) 2016 by Thomas Zuberbühler
 */

package ch.geomo.tramaps;

public class MetroMapExporter {

//    public void toCsv(String filePath, MetroMap map) throws IOException {
//
//        ICsvBeanWriter writer = null;
//        try {
//
//            writer = new CsvBeanWriter(new FileWriter(filePath + "/nodes.csv"), CsvPreference.EXCEL_NORTH_EUROPE_PREFERENCE);
//
//            String[] header = new String[]{"name", "label", "x", "y"};
//            CellProcessor[] processors = new CellProcessor[]{new NotNull(), new NotNull(), new Optional(), new Optional()};
//
//            writer.writeHeader(header);
//            for (GridNode node : map.getNodes()) {
//                writer.write(node, header, processors);
//            }
//
//            writer.flush();
//            writer = new CsvBeanWriter(new FileWriter(filePath + "/segments.csv"), CsvPreference.EXCEL_NORTH_EUROPE_PREFERENCE);
//
//            header = new String[]{"name", "startX", "startY", "endX", "endY"};
//            processors = new CellProcessor[]{new NotNull(), new Optional(), new Optional(), new Optional(), new Optional()};
//
//            writer.writeHeader(header);
//            for (GridEdge edge : map.getEdges()) {
//                writer.write(edge, header, processors);
//            }
//
//        }
//        finally {
//            if (writer != null) {
//                writer.close();
//            }
//        }
//
//}

}
