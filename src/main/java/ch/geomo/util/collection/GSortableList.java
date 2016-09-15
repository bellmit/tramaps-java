/*
 * Copyright (c) 2016 Thomas Zuberbuehler. All rights reserved.
 */

package ch.geomo.util.collection;

import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Collections;
import java.util.stream.Stream;

public class GSortableList<E extends Comparable<E>> extends GList<E> {

    public GSortableList() {
        super();
    }

    public GSortableList(Collection<E> c) {
        super(c);
    }

    @SafeVarargs
    public GSortableList(E... elements) {
        super(elements);
    }

    public GSortableList(Stream<E> stream) {
        super(stream);
    }

    public GSortableList<E> sort() {
        Collections.sort(this);
        return this;
    }

    public GSortableList<E> reverseSort() {
        return (GSortableList<E>) sort().reverse();
    }

    @NotNull
    public static <E extends Comparable<E>> GSortableList<E> sortableList(Collection<E> c) {
        return new GSortableList<>(c);
    }

}
