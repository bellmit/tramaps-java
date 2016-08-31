/*
 * Copyright (c) 2016 by Thomas Zuberbühler
 */

package ch.geomo.tramaps.util;

import ch.geomo.util.pair.Pair;
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

    /**
     * Creates a {@link Set} of {@link Pair} without {@link Pair}s filtered by
     * given {@link Predicate}.
     *
     * @return a filtered {@link Set} or {@link Pair}
     */
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
