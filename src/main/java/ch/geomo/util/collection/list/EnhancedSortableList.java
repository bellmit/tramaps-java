/*
 * Copyright (c) 2016 Thomas Zuberbuehler. All rights reserved.
 */

package ch.geomo.util.collection.list;

public interface EnhancedSortableList<E extends Comparable<E>> extends EnhancedList<E> {

    GSortableList<E> sort();

    GSortableList<E> reverseSort();

}
