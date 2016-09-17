/*
 * Copyright (c) 2016 Thomas Zuberbuehler. All rights reserved.
 */

package ch.geomo.util.collection.list;

import ch.geomo.util.collection.set.EnhancedSet;
import ch.geomo.util.collection.pair.Pair;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.BooleanSupplier;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

public interface EnhancedList<E> extends List<E> {

    @NotNull
    EnhancedList<E> union(@NotNull Collection<E> list);

    @NotNull
    EnhancedList<E> intersection(@NotNull Collection<E> list);

    @NotNull
    Pair<EnhancedList<E>> diff(@NotNull Collection<E> list);

    boolean anyMatch(@NotNull Predicate<E> predicate);

    boolean contains(@NotNull Collection<E> list);

    boolean hasEqualContent(@NotNull Collection<E> list);

    @NotNull
    Optional<E> first();

    boolean hasOneElement();
    boolean hasMoreThanOneElement();

    @NotNull
    EnhancedList<E> reverse();

    @NotNull
    EnhancedList<E> reverseIf(@NotNull BooleanSupplier supplier);

    @NotNull
    EnhancedList<E> addElements(@NotNull E... elements);

    @NotNull
    EnhancedList<E> addElements(@NotNull Collection<E> elements);

    @NotNull
    EnhancedList<E> addElements(@NotNull Stream<E> elementStream);

    @NotNull
    EnhancedList<E> removeElements(@NotNull E... elements);

    @NotNull
    EnhancedList<E> removeElements(@NotNull Predicate<E> predicate);

    @NotNull
    EnhancedList<E> keepElements(@NotNull Predicate<E> predicate);

    @NotNull
    EnhancedList<E> without(@NotNull Predicate<E> predicate);

    @NotNull
    EnhancedList<E> filter(@NotNull Predicate<E> predicate);

    @NotNull <T> EnhancedList<T> map(@NotNull Function<E, T> function);

    @NotNull
    EnhancedList<Pair<E>> toPairList();

    @NotNull
    EnhancedList<Pair<E>> toPairList(@NotNull Predicate<Pair<E>> predicate);

    @NotNull
    Stream<Pair<E>> toPairStream(@NotNull Predicate<Pair<E>> predicate);

    @NotNull
    EnhancedSet<E> toSet();

    @NotNull
    @Override
    E[] toArray();

    @NotNull
    List<E> immutable();

}
