/*
 * Copyright (c) 2016 Thomas Zuberbuehler. All rights reserved.
 */

package ch.geomo.util.collection.list;

import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Collections;
import java.util.stream.Stream;

// TODO -> extend from another class !
public class GSortableList<E extends Comparable<E>> extends GList<E> implements EnhancedSortableList<E> {

    protected GSortableList(@NotNull Collection<E> c) {
        super(c);
    }

    @SafeVarargs
    protected GSortableList(@NotNull E... elements) {
        super(elements);
    }

    protected GSortableList(@NotNull Stream<E> stream) {
        super(stream);
    }

    @NotNull
    @Override
    public GSortableList<E> sort() {
        Collections.sort(this);
        return this;
    }

    @NotNull
    @Override
    public GSortableList<E> reverseSort() {
        return (GSortableList<E>) sort().reverse();
    }

    @NotNull
    @SafeVarargs
    public static <E extends Comparable<E>> GSortableList<E> createSortableList(@NotNull E... elements) {
        return new GSortableList<>(elements);
    }

    @NotNull
    public static <E extends Comparable<E>> GSortableList<E> createSortableList(@NotNull Collection<E> c) {
        return new GSortableList<>(c);
    }

    @NotNull
    public static <E extends Comparable<E>> GSortableList<E> createSortableList(@NotNull Stream<E> stream) {
        return new GSortableList<>(stream);
    }

}
