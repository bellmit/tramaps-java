/*
 * Copyright (c) 2016 by Thomas Zuberbühler
 */

package ch.geomo.tramaps.util;

import ch.geomo.util.tuple.Tuple;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public final class CollectionUtil {

    private CollectionUtil() {
    }

    @NotNull
    public static <T> Set<Tuple<T>> makePairs(@Nullable Collection<T> items, @NotNull Predicate<Tuple<T>> filterPredicate) {

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

}
