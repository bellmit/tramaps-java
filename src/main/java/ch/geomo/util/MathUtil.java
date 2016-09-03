/*
 * Copyright (c) 2016 Thomas Zuberbuehler. All rights reserved.
 */

package ch.geomo.util;

import java.util.Comparator;
import java.util.function.Function;
import java.util.function.Predicate;

public final class MathUtil {

    private MathUtil() {
    }

    /**
     * @return the smaller value according to given {@link Comparator} or the second value if both are equals
     */
    public static <T>T min(T first, T second, Comparator<T> comparator) {
        if (comparator.compare(first, second) < 0) {
            return first;
        }
        return second;
    }

    /**
     * @return the smaller value according to given {@link Comparator} or the second value if both are equals
     */
    public static <T>T max(T first, T second, Comparator<T> comparator) {
        if (comparator.compare(first, second) > 0) {
            return first;
        }
        return second;
    }

}
