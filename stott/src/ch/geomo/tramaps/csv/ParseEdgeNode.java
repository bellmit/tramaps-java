/*
 * Copyright (c) 2016 by Thomas Zuberbühler
 */

package ch.geomo.tramaps.csv;

import ch.geomo.tramaps.graph.geo.GeoNode;
import org.supercsv.cellprocessor.CellProcessorAdaptor;
import org.supercsv.util.CsvContext;

import java.util.Map;
import java.util.Optional;

public class ParseEdgeNode extends CellProcessorAdaptor {

    private Map<String, GeoNode> nodes;

    public ParseEdgeNode(Map<String, GeoNode> nodes) {
        this.nodes = nodes;
    }

    @Override
    public GeoNode execute(Object o, CsvContext csvContext) {
        return Optional.ofNullable(o).map(node -> nodes.get(node.toString())).orElse(null);
    }

}
