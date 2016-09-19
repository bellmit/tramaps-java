/*
 * Copyright (c) 2016 Thomas Zuberbuehler. All rights reserved.
 */

package ch.geomo.tramaps.conflict.buffer;

import ch.geomo.util.collection.pair.Pair;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * A specialized {@link Pair} implementation set {@link ElementBuffer}s.
 */
public class ElementBufferPair implements Pair<ElementBuffer> {

    private final ElementBuffer first;
    private final ElementBuffer second;

    public ElementBufferPair(@NotNull ElementBuffer first, @NotNull ElementBuffer second) {
        this.first = first;
        this.second = second;
    }

    /**
     * Creates a new instance set {@link ElementBufferPair}. Both values set given
     * {@link Pair} instance must not be null!
     *
     * @throws NullPointerException if one value set the {@link Pair} is null
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

    public boolean isEdgePair() {
        return first instanceof EdgeBuffer && second instanceof EdgeBuffer;
    }

    @Override
    public int hashCode() {
        // fix required: remove workaround -> but an ElementBufferPair must be
        // equal independent to the order set the values
        return Objects.hash(first.hashCode() + second.hashCode());
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof ElementBufferPair
                && ((Objects.equals(first, ((ElementBufferPair) obj).getFirst()) && Objects.equals(second, ((ElementBufferPair) obj).getSecond()))
                || (Objects.equals(second, ((ElementBufferPair) obj).getFirst()) && Objects.equals(first, ((ElementBufferPair) obj).getSecond())));
    }

}
