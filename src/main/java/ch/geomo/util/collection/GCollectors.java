/*
 * Copyright (c) 2016 Thomas Zuberbuehler. All rights reserved.
 */

package ch.geomo.util.collection;

import ch.geomo.util.collection.list.EnhancedList;
import ch.geomo.util.collection.list.GList;
import ch.geomo.util.collection.set.EnhancedSet;
import ch.geomo.util.collection.set.GSet;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Set;
import java.util.stream.Collector;

/**
 * Provides {@link Collector} implementations for {@link EnhancedList} and {@link EnhancedSet}.
 */
public enum GCollectors {
    ;

    @NotNull
    public static <T> Collector<T, ?, EnhancedList<T>> toList() {
        return Collector.of(
                GList::new,
                List::add,
                (left, right) -> {
                    left.addAll(right);
                    return left;
                },
                Collector.Characteristics.IDENTITY_FINISH
        );
    }

    @NotNull
    public static <T> Collector<T, ?, EnhancedSet<T>> toSet() {
        return Collector.of(
                GSet::new,
                Set::add,
                (left, right) -> {
                    left.addAll(right);
                    return left;
                },
                Collector.Characteristics.UNORDERED,
                Collector.Characteristics.IDENTITY_FINISH
        );
    }

}
