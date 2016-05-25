/*
 * Copyright (c) 2016 by Thomas Zuberbühler
 */

package ch.geomo.tramaps.util;

public final class NumberUtil {

    private NumberUtil() {
    }

    /**
     * Returns true if first number is bigger/greater than second number.
     */
    public static <N extends Number & Comparable<N>> boolean isGreaterThan(N firstNumber, N secondNumber) {
        return firstNumber.compareTo(secondNumber) > 0;
    }

    /**
     * Returns true if first number is smaller/less than second number.
     */
    public static <N extends Number & Comparable<N>> boolean isLessThan(N firstNumber, N secondNumber) {
        return firstNumber.compareTo(secondNumber) < 0;
    }

}
