/*
 * Copyright (c) 2016 Thomas Zuberbuehler. All rights reserved.
 */

package ch.geomo.tramaps.conflict.buffer;

import ch.geomo.tramaps.graph.Node;
import ch.geomo.tramaps.map.signature.BendNodeSignature;
import ch.geomo.util.pair.Pair;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * A specialized {@link Pair} implementation of {@link ElementBuffer}s.
 */
public class ElementBufferPair implements Pair<ElementBuffer> {

    private ElementBuffer first;
    private ElementBuffer second;

    public ElementBufferPair(@NotNull ElementBuffer first, @NotNull ElementBuffer second) {
        this.first = first;
        this.second = second;
    }

    /**
     * Creates a new instance of {@link ElementBufferPair}. Both values of given
     * {@link Pair} instance must not be null!
     *
     * @throws NullPointerException if one value of the {@link Pair} is null
     */
    public ElementBufferPair(@NotNull Pair<ElementBuffer> pair) {
        this(pair.first(), pair.second());
        Objects.requireNonNull(first);
        Objects.requireNonNull(second);
    }

    @Override
    public ElementBuffer getFirst() {
        return first;
    }

    @Override
    public ElementBuffer getSecond() {
        return second;
    }

    public boolean hasEqualElements() {
        return first.equals(second);
    }

    public boolean hasAdjacentElements() {
        return first.getElement().isAdjacent(second.getElement());
    }

    public boolean isNodePair() {
        return first instanceof NodeBuffer && second instanceof NodeBuffer;
    }

    public boolean hasBendNode() {
        return stream()
                .filter(elementBuffer -> elementBuffer instanceof NodeBuffer)
                .map(elementBuffer -> (Node) elementBuffer.getElement())
                .anyMatch(node -> node.getNodeSignature() instanceof BendNodeSignature);
    }

    public boolean isEdgePair() {
        return first instanceof EdgeBuffer && second instanceof EdgeBuffer;
    }

}
