/*
 * Copyright (c) 2016-2018 Thomas Zuberbuehler. All rights reserved.
 */

package ch.geomo.util.math;

import java.util.Comparator;

public enum MathUtil {

    /* util class */;

    /**
     * @return the smaller value according to given {@link Comparator} or the second value if both are equals
     */
    public static <T> T min(T first, T second, Comparator<T> comparator) {
        if (comparator.compare(first, second) < 0) {
            return first;
        }
        return second;
    }

    /**
     * @return the smaller value according to given {@link Comparator} or the second value if both are equals
     */
    public static <T> T max(T first, T second, Comparator<T> comparator) {
        if (comparator.compare(first, second) > 0) {
            return first;
        }
        return second;
    }

}
