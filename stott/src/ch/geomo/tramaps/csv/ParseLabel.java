/*
 * Copyright (c) 2016 by Thomas Zuberbühler
 */

package ch.geomo.tramaps.csv;

import ch.geomo.tramaps.graph.NodeLabel;
import org.supercsv.cellprocessor.CellProcessorAdaptor;
import org.supercsv.util.CsvContext;

import java.util.Optional;

public class ParseLabel extends CellProcessorAdaptor {

    @Override
    public NodeLabel execute(Object o, CsvContext csvContext) {
        return Optional.ofNullable(o).map(label -> new NodeLabel(label.toString())).orElse(null);
    }

}
