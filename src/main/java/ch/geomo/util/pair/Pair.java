/*
 * Copyright (c) 2016 Thomas Zuberbuehler. All rights reserved.
 */

package ch.geomo.util.pair;

import ch.geomo.tramaps.graph.Node;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Represents a pair of one type. Two pairs are always are equal if both of the first pair values
 * are equals in any combination to the other pair's values.
 *
 * @see java.util.Map.Entry for pairs of different types
 */
public interface Pair<T> {

    /**
     * @return the first value
     */
    T getFirst();

    /**
     * Alias for {@link #getFirst()}.
     */
    default T first() {
        return getFirst();
    }

    /**
     * @return the second value
     */
    T getSecond();

    /**
     * Alias for {@link #getSecond()}.
     */
    default T second() {
        return getSecond();
    }

    /**
     * @return false if element is equals to null at the given index
     * @throws IndexOutOfBoundsException if index is neither 1 or 0
     */
    default boolean isNull(int index) {
        return get(index) == null;
    }

    /**
     * Gets the other value. Throws a {@link NoSuchElementException} if given value is not an item of current pair.
     *
     * @throws NoSuchElementException if given value is not a value of current {@link Pair}
     */
    default T getOtherValue(T value) {
        if (Objects.equals(get(0), value)) {
            return get(1);
        }
        if (Objects.equals(get(1), value)) {
            return get(0);
        }
        throw new NoSuchElementException("Given value is not an item of this pair!");
    }

    /**
     * @return a {@link Stream} for this pair
     */
    default Stream<T> stream() {
        return Stream.of(getFirst(), getSecond());
    }

    /**
     * @return true if given value is contained in this instance
     */
    default boolean contains(T value) {
        return Objects.equals(value, get(0)) || Objects.equals(value, get(1));
    }

    /**
     * Returns <b>one</b> shared value of both tuples.
     *
     * @throws NoSuchElementException if no shared value was found
     */
    default T getSharedValue(Pair<T> otherPair) {
        return otherPair.stream()
                .filter(this::contains)
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("Tuples do not share any value."));
    }

    /**
     * @return true if at least one of the two element of given pair is also contained of this instance
     */
    default boolean hasSharedValue(Pair<T> otherPair) {
        return otherPair.stream()
                .anyMatch(this::contains);
    }

    /**
     * @return a new {@link ImmutablePair} with given elements.
     */
    @NotNull
    static <T> Pair<T> of(@Nullable T first, @Nullable T second) {
        return new ImmutablePair<>(first, second);
    }


    /**
     * @return a new {@link MutablePair} or {@link ImmutablePair} with given elements depending the given boolean
     */
    @NotNull
    static <T> Pair<T> of(@Nullable T first, @Nullable T second, boolean mutable) {
        if (mutable) {
            return new MutablePair<>(first, second);
        }
        return new ImmutablePair<>(first, second);
    }

    /**
     * @return a new {@link ImmutablePair} with given Apache Commons Pair instance
     * @see org.apache.commons.lang3.tuple.Pair
     */
    @Nullable
    @Contract("null -> null")
    static <T> Pair<T> from(@Nullable org.apache.commons.lang3.tuple.Pair<T, T> pair) {
        if (pair == null) {
            return null;
        }
        return new ImmutablePair<>(pair.getLeft(), pair.getRight());
    }

    /**
     * @return a new Set of {@link ImmutablePair}s with given collection of Apache Commons Pair instances
     * @see org.apache.commons.lang3.tuple.Pair
     */
    @NotNull
    static <T> Set<Pair<T>> from(Collection<org.apache.commons.lang3.tuple.Pair<T, T>> pairs) {
        if (pairs == null) {
            return Collections.emptySet();
        }
        return pairs.stream()
                .map(Pair::from)
                .collect(Collectors.toSet());
    }

    /**
     * @return a {@link Set} of {@link ImmutablePair} with given collections
     */
    @NotNull
    static <T> Set<Pair<T>> from(Collection<T> col1, Collection<T> col2) {
        if (col1 == null || col2 == null) {
            return Collections.emptySet();
        }
        return col1.stream()
                .flatMap(v1 -> col2.stream()
                        .map(v2 -> Pair.of(v1, v2)))
                .collect(Collectors.toSet());
    }

    /**
     * Gets the first or second value by index. Index start with 0 in order to be consistent with other APIs. Only
     * 0 and 1 are allowed since only two items are hold by a pair.
     *
     * @return the element at the given index
     * @throws IndexOutOfBoundsException if index is neither 1 or 0
     */
    default T get(int index) {
        if (index > 1 && index < 0) {
            throw new IndexOutOfBoundsException("An index > 1 or < 0 is not allowed.");
        }
        return index == 0 ? getFirst() : getSecond();
    }

    /**
     * Converts given {@link Pair} instance to an instance of {@link org.apache.commons.lang3.tuple.Pair}.
     *
     * @return a Apache Commons Pair instance
     * @see org.apache.commons.lang3.tuple.Pair
     */
    default org.apache.commons.lang3.tuple.Pair toPair() {
        return org.apache.commons.lang3.tuple.Pair.of(getFirst(), getSecond());
    }

    @NotNull
    default List<T> toList() {
        return stream()
                .filter(value -> value != null)
                .collect(Collectors.toList());
    }

}
