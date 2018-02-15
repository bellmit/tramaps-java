/*
 * Copyright (c) 2016-2018 Thomas Zuberbuehler. All rights reserved.
 */

package ch.geomo.util.collection.set;

import ch.geomo.util.collection.list.EnhancedList;
import ch.geomo.util.collection.pair.Pair;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

public interface EnhancedSet<E> extends Set<E> {

    @NotNull
    EnhancedSet<E> union(@NotNull Collection<E> list);

    @NotNull
    EnhancedSet<E> intersection(@NotNull Collection<E> list);

    @NotNull
    Pair<EnhancedSet<E>> diff(@NotNull Collection<E> list);

    /**
     * Convenience method. Gets the first element of this {@link Set}. The element
     * is probably different to the last method call. Do only use this
     * method if you are sure that only one element is available.
     * @return the first element of this {@link Set}
     * @see #hasOneElement()
     */
    @NotNull
    Optional<E> first();

    @SuppressWarnings("unused")
    boolean hasOneElement();

    boolean hasMoreThanOneElement();

    boolean anyMatch(@NotNull Predicate<E> predicate);

    boolean contains(@NotNull Collection<E> list);

    boolean hasEqualContent(@NotNull Collection<E> list);

    @NotNull
    EnhancedSet<E> addElements(@NotNull E... elements);

    @NotNull
    EnhancedSet<E> addElements(@NotNull Collection<E> elements);

    @NotNull
    EnhancedSet<E> addElements(@NotNull Stream<E> elementStream);

    @NotNull
    EnhancedSet<E> removeElements(@NotNull E... elements);

    @NotNull
    EnhancedSet<E> removeElements(@NotNull Predicate<E> predicate);

    @NotNull
    EnhancedSet<E> keepElements(@NotNull Predicate<E> predicate);

    @NotNull
    EnhancedSet<E> without(@NotNull Predicate<E> predicate);

    @NotNull
    EnhancedSet<E> filter(@NotNull Predicate<E> predicate);

    @NotNull <T> EnhancedSet<T> map(@NotNull Function<E, T> function);

    @NotNull <T> EnhancedSet<T> flatMap(Function<E, ? extends Stream<? extends T>> mapper);

    @NotNull
    EnhancedSet<Pair<E>> toPairSet();

    @NotNull
    EnhancedSet<Pair<E>> toPairSet(@NotNull Predicate<Pair<E>> predicate);

    @NotNull
    Stream<Pair<E>> toPairStream(@NotNull Predicate<Pair<E>> predicate);

    @NotNull
    EnhancedList<E> toList();

    @NotNull
    @Override
    E[] toArray();

}
