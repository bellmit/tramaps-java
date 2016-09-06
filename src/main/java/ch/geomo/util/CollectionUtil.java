/*
 * Copyright (c) 2016 Thomas Zuberbuehler. All rights reserved.
 */

package ch.geomo.util;

import ch.geomo.util.pair.Pair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public final class CollectionUtil {

    private CollectionUtil() {
    }

    /**
     * Creates a {@link Set} of {@link Pair} without {@link Pair}s filtered by
     * given {@link Predicate}.
     *
     * @return a filtered {@link Set} or {@link Pair}
     * @see #makePairs(Collection, Predicate)
     */
    @NotNull
    public static <T> Set<Pair<T>> makePairs(@Nullable Set<T> items, @Nullable Predicate<Pair<T>> filterPredicate) {
        if (items == null) {
            return Collections.emptySet();
        }
        return makePairs(new ArrayList<>(items), filterPredicate);
    }

    /**
     * Creates a {@link Set} of {@link Pair} without {@link Pair}s filtered by
     * given {@link Predicate}.
     *
     * @return a filtered {@link Set} or {@link Pair}
     */
    @NotNull
    public static <T> Set<Pair<T>> makePairs(@Nullable Collection<T> items, @Nullable Predicate<Pair<T>> filterPredicate) {

        if (items == null || items.isEmpty()) {
            return Collections.emptySet();
        }

        Predicate<Pair<T>> predicate = filterPredicate;
        if (predicate == null) {
            predicate = (pair) -> true;
        }

        return items.stream()
                .flatMap(v1 -> items.parallelStream()
                        .map(v2 -> Pair.of(v1, v2)))
                .filter(predicate)
                .distinct()
                .collect(Collectors.toSet());

    }

    /**
     * Sorts the given {@link List} and returns the sorted {@link List}.
     * @see Collections#sort(List)
     */
    @NotNull
    public static <T extends Comparable<? super T>>List<T> sort(@NotNull List<T> list) {
        Collections.sort(list);
        return list;
    }

    /**
     * Reverses the given {@link List} and returns the reversed {@link List}.
     * @see Collections#sort(List)
     */
    @NotNull
    public static <T extends Comparable<? super T>>List<T> reverse(@NotNull List<T> list) {
        Collections.reverse(list);
        return list;
    }

    /**
     * Sorts and reverses the given {@link List} and returns the sorted and reversed {@link List}.
     * @see Collections#sort(List)
     */
    @NotNull
    public static <T extends Comparable<? super T>>List<T> reverseSort(@NotNull List<T> list) {
        Collections.reverse(sort(list));
        return list;
    }

    /**
     * @return true if both collection contains the same values but ignoring ordering of the values
     */
    public static <T>boolean equals(@NotNull Collection<T> collection1, @NotNull Collection<T> collection2) {
        if (collection1.size() != collection2.size()) {
            return false;
        }
        ArrayList<T> list = new ArrayList<>(collection1);
        list.removeAll(collection2);
        return list.isEmpty();
    }

}
