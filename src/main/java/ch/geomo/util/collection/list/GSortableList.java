/*
 * Copyright (c) 2016 Thomas Zuberbuehler. All rights reserved.
 */

package ch.geomo.util.collection.list;

import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Collections;
import java.util.stream.Stream;

public class GSortableList<E extends Comparable<E>> extends GList<E> implements EnhancedSortableList<E> {

    protected GSortableList() {
        super();
    }

    protected GSortableList(Collection<E> c) {
        super(c);
    }

    @SafeVarargs
    protected GSortableList(E... elements) {
        super(elements);
    }

    protected GSortableList(Stream<E> stream) {
        super(stream);
    }

    @Override
    public GSortableList<E> sort() {
        Collections.sort(this);
        return this;
    }

    @Override
    public GSortableList<E> reverseSort() {
        return (GSortableList<E>) sort().reverse();
    }

    @NotNull
    public static <E extends Comparable<E>> GSortableList<E> createSortableList(E... elements) {
        return new GSortableList<>(elements);
    }

    @NotNull
    public static <E extends Comparable<E>> GSortableList<E> createSortableList(Collection<E> c) {
        return new GSortableList<>(c);
    }

}
