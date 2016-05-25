/*
 * Copyright (c) 2016 by Thomas Zuberbühler
 */

package ch.geomo.tramaps.util;

import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public final class CollectionUtil {

    private CollectionUtil() {
    }

    @Contract("null -> null")
    public static <T> List<Pair<T, T>> makePairs(@Nullable Collection<T> collection) {
        return makePairs(collection, false);
    }

    @Contract("null,_ -> null")
    public static <T> List<Pair<T, T>> makePairs(@Nullable Collection<T> collection, boolean closeCircle) {
        return makePairs(collection, closeCircle, false);
    }

    @Contract("null,_,_ -> null")
    public static <T> List<Pair<T, T>> makePairs(@Nullable Collection<T> collection, boolean closeCircle, boolean emptyListWhenNoPairAvailable) {

        if (collection == null) {
            return null;
        }

        if (emptyListWhenNoPairAvailable && collection.size() < 2) {
            return Collections.emptyList();
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

    @Contract("!null, _ -> !null")
    public static <T>T defaultIfNull(T value, @NotNull Supplier<T> nullValueSupplier) {
        if (value != null) {
            return value;
        }
        return nullValueSupplier.get();
    }

}
