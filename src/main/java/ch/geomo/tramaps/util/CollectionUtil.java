/*
 * Copyright (c) 2016 by Thomas Zuberbühler
 */

package ch.geomo.tramaps.util;

import ch.geomo.util.tuple.Pair;
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
    public static <T> Set<Pair<T>> makePairs(@Nullable Collection<T> items, @NotNull Predicate<Pair<T>> filterPredicate) {

        if (items == null || items.isEmpty()) {
            return Collections.emptySet();
        }

        return items.stream()
                .flatMap(v1 -> items.parallelStream()
                        .map(v2 -> Pair.of(v1, v2)))
                .filter(filterPredicate)
                .distinct()
                .collect(Collectors.toSet());

    }

}
