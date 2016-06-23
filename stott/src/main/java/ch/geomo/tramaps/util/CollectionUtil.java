/*
 * Copyright (c) 2016 by Thomas Zuberbühler
 */

package ch.geomo.tramaps.util;

import ch.geomo.tramaps.util.tuple.Tuple;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public final class CollectionUtil {

    private CollectionUtil() {
    }

    /**
     * Makes a permutation with given {@link Collection}.
     */
    @NotNull
    public static <T> Set<Tuple<T>> makePermutations(@Nullable Collection<T> items, boolean disallowEqualCombination) {
        return makePermutations(items, p -> !disallowEqualCombination || p.getFirst() != p.getSecond() );
    }

    /**
     * Makes a permutation with given {@link Collection}. This implementation <b>allows</b> equal combinations.
     * @see #makePermutations(Collection, boolean)
     */
    @NotNull
    public static <T> Set<Tuple<T>> makePermutations(@Nullable Collection<T> items) {
        return makePermutations(items, p -> true);
    }

    @NotNull
    public static <T> Set<Tuple<T>> makePermutations(@Nullable Collection<T> items, @NotNull Predicate<Tuple<T>> filterPredicate) {

        if (items == null || items.isEmpty()) {
            return Collections.emptySet();
        }

        return items.parallelStream()
                .flatMap(v1 -> items.parallelStream()
                        .map(v2 -> Tuple.of(v1, v2)))
                .filter(filterPredicate)
                .distinct()
                .collect(Collectors.toSet());

    }

    @Contract("null,_,_ -> null")
    public static <T> List<Pair<T, T>> makePairs(@Nullable Collection<T> collection, boolean closeCircle, boolean emptyListWhenNoPairAvailable) {

        if (collection == null) {
            return null;
        }

        if (collection.size() < 2) {
            return emptyListWhenNoPairAvailable ? Collections.emptyList() : null;
        }

        List<T> right = collection.stream()
                // omit first value
                .skip(1)
                .collect(Collectors.toList());

        List<T> left = collection.stream()
                // omit last value
                .limit(collection.size() - 1)
                .collect(Collectors.toList());

        if (closeCircle) {

            Optional<T> first = collection.stream().limit(1).findFirst();
            Optional<T> last = collection.stream().skip(collection.size() - 1).findFirst();

            if (first.isPresent() && last.isPresent()) {
                left.add(first.get());
                right.add(last.get());
            }

        }

        return IntStream.range(0, right.size())
                .mapToObj(index -> Pair.of(right.get(index), left.get(index)))
                .collect(Collectors.toList());

    }

}
