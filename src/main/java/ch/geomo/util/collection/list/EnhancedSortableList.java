/*
 * Copyright (c) 2016 Thomas Zuberbuehler. All rights reserved.
 */

package ch.geomo.util.collection.list;

import org.jetbrains.annotations.NotNull;

public interface EnhancedSortableList<E extends Comparable<E>> extends EnhancedList<E> {

    @NotNull
    GSortableList<E> sort();

    @NotNull
    GSortableList<E> reverseSort();

}
