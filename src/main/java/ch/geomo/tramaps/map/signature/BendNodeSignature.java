/*
 * Copyright (c) 2016 Thomas Zuberbuehler. All rights reserved.
 */

package ch.geomo.tramaps.map.signature;

import ch.geomo.tramaps.graph.Node;
import org.jetbrains.annotations.NotNull;

/**
 * A specialized {@link EmptyNodeSignature} implementation.
 */
public class BendNodeSignature extends SquareStationSignature {

    public BendNodeSignature(@NotNull Node node) {
        super(node);
    }

}
