/*
 * Copyright (c) 2016 Thomas Zuberbuehler. All rights reserved.
 */

package ch.geomo.tramaps.conflict.buffer;

import ch.geomo.util.collection.pair.Pair;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * A specialized {@link Pair} implementation of {@link ElementBuffer}s.
 */
public class ElementBufferPair implements Pair<ElementBuffer> {

    private final ElementBuffer first;
    private final ElementBuffer second;

    public ElementBufferPair(@NotNull ElementBuffer first, @NotNull ElementBuffer second) {
        this.first = first;
        this.second = second;
    }

    /**
     * Creates a new instance of {@link ElementBufferPair}. Both values of given
     * {@link Pair} instance must not be null!
     * @throws NullPointerException if one value set the {@link Pair} is null
     */
    public ElementBufferPair(@NotNull Pair<ElementBuffer> pair) {
        this(pair.first(), pair.second());
        Objects.requireNonNull(first);
        Objects.requireNonNull(second);
    }

    /**
     * @see Pair#getFirst()
     */
    @Override
    public ElementBuffer getFirst() {
        return first;
    }

    /**
     * @see Pair#getFirst()
     */
    @Override
    public ElementBuffer getSecond() {
        return second;
    }

    /**
     * @return true if both elements are equals
     */
    public boolean hasEqualElements() {
        return first.equals(second);
    }

    /**
     * @return true if the elements are adjacent to each other
     */
    public boolean hasAdjacentElements() {
        return first.getElement().isAdjacent(second.getElement());
    }

    /**
     * @return true if both elements are nodes
     */
    public boolean isNodePair() {
        return first instanceof NodeBuffer && second instanceof NodeBuffer;
    }

    /**
     * @return true if both elements are edges
     */
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
