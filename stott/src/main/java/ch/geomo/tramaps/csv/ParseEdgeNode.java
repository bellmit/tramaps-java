/*
 * Copyright (c) 2016 by Thomas Zuberbühler
 */

package ch.geomo.tramaps.csv;

import ch.geomo.tramaps.grid.GridNode;
import org.supercsv.cellprocessor.CellProcessorAdaptor;
import org.supercsv.util.CsvContext;

import java.util.Map;
import java.util.Optional;

public class ParseEdgeNode extends CellProcessorAdaptor {

    private Map<String, GridNode> nodes;

    public ParseEdgeNode(Map<String, GridNode> nodes) {
        this.nodes = nodes;
    }

    @Override
    public GridNode execute(Object o, CsvContext csvContext) {
        return Optional.ofNullable(o).map(node -> nodes.get(node.toString())).orElse(null);
    }

}
