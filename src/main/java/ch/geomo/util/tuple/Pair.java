/*
 * Copyright (c) 2016 by Thomas Zuberbühler
 */

package ch.geomo.util.tuple;

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
public interface Pair<T> {

    /**
     * Gets the first value.
     */
    T getFirst();

    /**
     * Gets the second value.
     */
    T getSecond();

    default boolean isNull(int index) {
        return get(index) == null;
    }

    /**
     * Gets the other value. Throws a {@link NoSuchElementException} if given value is not an item of current tuple.
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
     * @throws NoSuchElementException if no shared value was found
     */
    default T getSharedValue(Pair<T> otherPair) {
        return otherPair.stream()
                .filter(this::contains)
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("Tuples do not share any value."));
    }

    default boolean hasSharedValue(Pair<T> otherPair) {
        return otherPair.stream()
                .anyMatch(this::contains);
    }

    /**
     * Creates a new immutable tuple of given values.
     */
    @NotNull
    static <T> Pair<T> of(@Nullable T first, @Nullable T second) {
        return new ImmutablePair<>(first, second);
    }


    /**
     * Creates a new tuple of given values.
     */
    @NotNull
    static <T> Pair<T> of(@Nullable T first, @Nullable T second, boolean mutable) {
        if (mutable) {
            return new MutablePair<>(first, second);
        }
        return new ImmutablePair<>(first, second);
    }

    @Contract("null -> null")
    static <T> Pair<T> from(@Nullable org.apache.commons.lang3.tuple.Pair<T, T> pair) {
        if (pair == null) {
            return null;
        }
        return new ImmutablePair<T>(pair.getLeft(), pair.getRight());
    }


    @NotNull
    static <T> Set<Pair<T>> from(Collection<org.apache.commons.lang3.tuple.Pair<T, T>> pairs) {
        if (pairs == null) {
            return Collections.emptySet();
        }
        return pairs.stream()
                .map(Pair::from)
                .collect(Collectors.toSet());
    }

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
     * 0 and 1 are allowed since only two items are hold by a tuple.
     */
    default T get(int index) {
        if (index > 1 && index < 0) {
            throw new IndexOutOfBoundsException("An index > 1 or < 0 is not allowed.");
        }
        return index == 0 ? getFirst() : getSecond();
    }

    /**
     * Converts given {@link Pair} instance to an instance of {@link org.apache.commons.lang3.tuple.Pair}.
     */
    default org.apache.commons.lang3.tuple.Pair toPair() {
        return org.apache.commons.lang3.tuple.Pair.of(getFirst(), getSecond());
    }

}
