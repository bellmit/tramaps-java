/*
 * Copyright (c) 2016 by Thomas Zuberbühler
 */

package ch.geomo.tramaps.util.tuple;

import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * A tuple or pair of a certain type. Two tuple are always are equal if both of the first tuple values are equals in
 * any combination to the other tuple's values.
 */
public interface Tuple<T> {

    /**
     * Gets the first value.
     */
    T getFirst();

    /**
     * Gets the second value.
     */
    T getSecond();

    /**
     * Gets the other value. Throws a {@link NoSuchElementException} if given value is not an item of current tuple.
     *
     * @throws java.util.NoSuchElementException if given value is not a value of current {@link Tuple}
     */
    default T getOtherValue(T value) {
        if (Objects.equals(get(0), value)) {
            return get(1);
        }
        if (Objects.equals(get(1), value)) {
            return get(0);
        }
        throw new NoSuchElementException("Given value is not an item of this tuple!");
    }

    default Stream<T> stream() {
        return Stream.of(getFirst(), getSecond());
    }

    default boolean contains(T value) {
        return Objects.equals(value, get(0)) || Objects.equals(value, get(1));
    }

    /**
     * Returns <b>one</b> shared value of both tuples.
     *
     * @throws java.util.NoSuchElementException if no shared value was found
     */
    default T getSharedValue(Tuple<T> otherTuple) {
        return otherTuple.stream()
                .filter(this::contains)
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("Tuples do not share any value."));
    }

    default boolean hasSharedValue(Tuple<T> otherTuple) {
        return otherTuple.stream()
                .anyMatch(this::contains);
    }

    /**
     * Creates a new immutable tuple of given values.
     */
    @NotNull
    static <T> Tuple<T> of(@Nullable T first, @Nullable T second) {
        return new ImmutableTuple<>(first, second);
    }

    @Contract("null -> null")
    static <T> Tuple<T> from(@Nullable Pair<T, T> pair) {
        if (pair == null) {
            return null;
        }
        return new ImmutableTuple<>(pair.getLeft(), pair.getRight());
    }


    @NotNull
    static <T> Set<Tuple<T>> from(Collection<Pair<T, T>> pairs) {
        if (pairs == null) {
            return Collections.emptySet();
        }
        return pairs.stream()
                .map(Tuple::from)
                .collect(Collectors.toSet());
    }

    @NotNull
    static <T> Set<Tuple<T>> from(Collection<T> col1, Collection<T> col2) {
        if (col1 == null || col2 == null) {
            return Collections.emptySet();
        }
        return col1.stream()
                .flatMap(v1 -> col2.stream()
                        .map(v2 -> Tuple.of(v1, v2)))
                .collect(Collectors.toSet());
    }

    /**
     * Gets the first or second value by index. Index start with 0 in order to be consistent with other APIs. Only
     * 0 and 1 are allowed since only two items are hold by a tuple.
     */
    default T get(int index) {
        if (index > 1 && index < 0) {
            throw new IndexOutOfBoundsException("An index > 1 or < 0 is not allowed.");
        }
        return index == 0 ? getFirst() : getSecond();
    }

    /**
     * Converts given {@link Tuple} instance to an instance of {@link Pair}.
     */
    default Pair<T, T> toPair() {
        return Pair.of(getFirst(), getSecond());
    }

}
