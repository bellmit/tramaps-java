/*
 * Copyright (c) 2016 Thomas Zuberbuehler. All rights reserved.
 */

package ch.geomo.util.collection;

import ch.geomo.util.pair.Pair;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.BooleanSupplier;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class GList<E> extends ArrayList<E> implements EnhancedList<E> {

    public GList() {
        super();
    }

    public GList(@NotNull Collection<E> c) {
        super(c);
    }

    @SafeVarargs
    public GList(@NotNull E... elements) {
        super(Arrays.asList(elements));
    }

    public GList(@NotNull Stream<E> stream) {
        super(stream.collect(Collectors.toList()));
    }

    @NotNull
    @Override
    public EnhancedList<E> union(@NotNull Collection<E> list) {
        return list(list).addElements(this);
    }

    @NotNull
    @Override
    public EnhancedList<E> intersection(@NotNull Collection<E> list) {
        return list(stream()
                .filter(list::contains));
    }

    @NotNull
    @Override
    public Pair<EnhancedList<E>> diff(@NotNull Collection<E> list) {
        EnhancedList<E> intersection = intersection(list);
        EnhancedList<E> thisList = list(stream()
                .filter(e -> !intersection.contains(e)));
        EnhancedList<E> otherList = list(list.stream()
                .filter(e -> !intersection.contains(e)));
        return Pair.of(thisList, otherList);
    }

    @NotNull
    @Override
    public EnhancedList<E> without(@NotNull Predicate<E> predicate) {
        return filter(e -> !predicate.test(e));
    }

    @NotNull
    @Override
    public EnhancedList<E> filter(@NotNull Predicate<E> predicate) {
        return list(stream()
                .filter(predicate));
    }

    @NotNull
    @Override
    public EnhancedList<E> reverse() {
        Collections.reverse(this);
        return this;
    }

    @NotNull
    public EnhancedList<E> reverseIf(@NotNull BooleanSupplier supplier) {
        if (supplier.getAsBoolean()) {
            return reverse();
        }
        return this;
    }

    @Override
    public boolean contains(@NotNull Predicate<E> predicate) {
        return stream().anyMatch(predicate);
    }

    @Override
    public boolean contains(@NotNull Collection<E> list) {
        return stream().allMatch(list::contains);
    }

    @Override
    public boolean hasEqualContent(@NotNull Collection<E> list) {
        if (list.size() != list.size()) {
            return false;
        }
        return contains(list::contains);
    }

    @NotNull
    @Override
    public final EnhancedList<E> addElements(@NotNull Stream<E> elementStream) {
        return addElements(elementStream.collect(Collectors.toList()));
    }

    @NotNull
    @Override
    @SafeVarargs
    public final EnhancedList<E> addElements(@NotNull E... elements) {
        return addElements(Arrays.asList(elements));
    }

    @NotNull
    @Override
    public final EnhancedList<E> addElements(@NotNull Collection<E> elements) {
        addAll(elements);
        return this;
    }

    @NotNull
    @Override
    @SafeVarargs
    public final EnhancedList<E> removeElements(@NotNull E... elements) {
        removeAll(Arrays.asList(elements));
        return this;
    }

    @NotNull
    @Override
    public EnhancedList<E> removeElements(@NotNull Predicate<E> predicate) {
        removeIf(predicate);
        return this;
    }

    @NotNull
    @Override
    public EnhancedList<E> keepElements(@NotNull Predicate<E> predicate) {
        return removeElements(e -> !predicate.test(e));
    }

    @NotNull
    @Override
    public <T> EnhancedList<T> map(@NotNull Function<E, T> function) {
        return list(stream()
                .map(function));
    }

    @NotNull
    @Override
    public EnhancedList<Pair<E>> toPairList() {
        return toPairList(e -> true);
    }

    @NotNull
    @Override
    public EnhancedList<Pair<E>> toPairList(@NotNull Predicate<Pair<E>> predicate) {
        return list(toPairStream(predicate).collect(Collectors.toList()));

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
    public Set<E> toSet() {
        return new HashSet<>(this);
    }

    @NotNull
    public List<E> immutable() {
        return Collections.unmodifiableList(this);
    }

    @NotNull
    public static <E> EnhancedList<E> cast(@NotNull List<E> list) {
        if (list instanceof EnhancedList) {
            return (EnhancedList<E>) list;
        }
        throw new ClassCastException("Cannot cast List to EnhancedList.");
    }

    @NotNull
    public static <E> EnhancedList<E> list(@NotNull Collection<E> c) {
        return new GList<>(c);
    }

    @NotNull
    public static <E> EnhancedList<E> list(@NotNull Stream<E> stream) {
        return new GList<>(stream);
    }

    @NotNull
    public static <E> EnhancedList<E> list(@NotNull Stream<E> stream1, @NotNull Stream<E> stream2) {
        return new GList<E>(Stream.concat(stream1, stream2));
    }

    @NotNull
    @SafeVarargs
    public static <E> EnhancedList<E> list(@NotNull E... elements) {
        return new GList<>(elements);
    }

}
