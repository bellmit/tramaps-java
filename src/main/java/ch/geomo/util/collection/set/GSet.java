/*
 * Copyright (c) 2016 Thomas Zuberbuehler. All rights reserved.
 */

package ch.geomo.util.collection.set;

import ch.geomo.util.collection.GCollectors;
import ch.geomo.util.collection.list.EnhancedList;
import ch.geomo.util.collection.list.GList;
import ch.geomo.util.collection.pair.Pair;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class GSet<E> extends HashSet<E> implements EnhancedSet<E> {

    public GSet() {
        super();
    }

    public GSet(@NotNull Collection<E> c) {
        super(c);
    }

    @SafeVarargs
    public GSet(@NotNull E... elements) {
        super(Arrays.asList(elements));
    }

    public GSet(@NotNull Stream<E> stream) {
        super(stream.collect(Collectors.toList()));
    }

    @NotNull
    @Override
    public Optional<E> first() {
        return stream().findFirst();
    }

    @Override
    public boolean hasOneElement() {
        return size() == 1;
    }

    @Override
    public boolean hasMoreThanOneElement() {
        return size() > 1;
    }

    @NotNull
    @Override
    public EnhancedSet<E> union(@NotNull Collection<E> c) {
        return createSet(c).addElements(this);
    }

    @NotNull
    @Override
    public EnhancedSet<E> intersection(@NotNull Collection<E> c) {
        return createSet(stream()
                .filter(c::contains));
    }

    @NotNull
    @Override
    public Pair<EnhancedSet<E>> diff(@NotNull Collection<E> c) {
        List<E> intersection = intersection(c).toList();
        EnhancedSet<E> thisCollection = createSet(stream()
                .filter(e -> !intersection.contains(e)));
        EnhancedSet<E> otherCollection = createSet(c.stream()
                .filter(e -> !intersection.contains(e)));
        return Pair.of(thisCollection, otherCollection);
    }

    @NotNull
    @Override
    public EnhancedSet<E> without(@NotNull Predicate<E> predicate) {
        return filter(e -> !predicate.test(e));
    }

    @NotNull
    @Override
    public EnhancedSet<E> filter(@NotNull Predicate<E> predicate) {
        return createSet(stream()
                .filter(predicate));
    }

    @Override
    public boolean anyMatch(@NotNull Predicate<E> predicate) {
        return stream().anyMatch(predicate);
    }

    @Override
    public boolean contains(@NotNull Collection<E> c) {
        return stream().allMatch(c::contains);
    }

    @Override
    public boolean hasEqualContent(@NotNull Collection<E> c) {
        if (size() != c.size()) {
            return false;
        }
        return anyMatch(c::contains);
    }

    @NotNull
    @Override
    public final EnhancedSet<E> addElements(@NotNull Stream<E> elementStream) {
        return addElements(elementStream.collect(Collectors.toList()));
    }

    @NotNull
    @Override
    @SafeVarargs
    public final EnhancedSet<E> addElements(@NotNull E... elements) {
        return addElements(Arrays.asList(elements));
    }

    @NotNull
    @Override
    public final EnhancedSet<E> addElements(@NotNull Collection<E> elements) {
        addAll(elements);
        return this;
    }

    @NotNull
    @Override
    @SafeVarargs
    public final EnhancedSet<E> removeElements(@NotNull E... elements) {
        removeAll(Arrays.asList(elements));
        return this;
    }

    @NotNull
    @Override
    public EnhancedSet<E> removeElements(@NotNull Predicate<E> predicate) {
        removeIf(predicate);
        return this;
    }

    @NotNull
    @Override
    public EnhancedSet<E> keepElements(@NotNull Predicate<E> predicate) {
        return removeElements(e -> !predicate.test(e));
    }

    @NotNull
    @Override
    public <T> EnhancedSet<T> map(@NotNull Function<E, T> function) {
        return createSet(stream()
                .map(function));
    }

    @NotNull
    @Override
    public <T> EnhancedSet<T> flatMap(Function<E, ? extends Stream<? extends T>> mapper) {
        return createSet(stream()
                .flatMap(mapper));
    }

    @NotNull
    @Override
    public EnhancedSet<Pair<E>> toPairSet() {
        return toPairSet(e -> true);
    }

    @NotNull
    @Override
    public EnhancedSet<Pair<E>> toPairSet(@NotNull Predicate<Pair<E>> predicate) {
        return toPairStream(predicate).collect(GCollectors.toSet());

    }

    @NotNull
    @Override
    public Stream<Pair<E>> toPairStream(@NotNull Predicate<Pair<E>> predicate) {

        if (isEmpty()) {
            return Stream.of();
        }

        return stream()
                .flatMap(v1 -> stream()
                        .map(v2 -> Pair.of(v1, v2)))
                .filter(predicate)
                .distinct();

    }

    @NotNull
    @Override
    public EnhancedList<E> toList() {
        return GList.createList(this);
    }

    @NotNull
    @Override
    @SuppressWarnings("unchecked")
    public E[] toArray() {
        return (E[])super.toArray();
    }

    @NotNull
    public Set<E> immutable() {
        return Collections.unmodifiableSet(this);
    }

    @NotNull
    public static <E> EnhancedSet<E> emptySet() {
        return new GSet<>();
    }

    @NotNull
    public static <E> EnhancedSet<E> createSet(@NotNull Collection<E> c) {
        return new GSet<>(c);
    }

    @NotNull
    public static <E> EnhancedSet<E> createSet(@NotNull Stream<E> stream) {
        return new GSet<>(stream);
    }

    @NotNull
    public static <E> EnhancedSet<E> createSet(@NotNull Stream<E> stream1, @NotNull Stream<E> stream2) {
        return new GSet<>(Stream.concat(stream1, stream2));
    }

    @NotNull
    @SafeVarargs
    public static <E> EnhancedSet<E> createSet(@NotNull E... elements) {
        return new GSet<>(elements);
    }

}
