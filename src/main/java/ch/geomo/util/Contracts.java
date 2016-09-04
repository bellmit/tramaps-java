/*
 * Copyright (c) 2016 Thomas Zuberbuehler. All rights reserved.
 */

package ch.geomo.util;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Predicate;
import java.util.function.Supplier;

public final class Contracts {

    private Contracts() {
    }

    /**
     * @throws IllegalStateException if given {@link Predicate} return false (i.e. if test not passed)
     */
    public static <T> void test(@Nullable T obj, @NotNull Predicate<T> predicate, @NotNull Supplier<String> message) throws IllegalStateException {
        if (!predicate.test(obj)) {
            throw new IllegalStateException(message.get());
        }
    }

    /**
     * @throws IllegalStateException if given {@link Predicate} return false (i.e. if test not passed)
     */
    public static <T> void test(@Nullable T obj, @NotNull Predicate<T> predicate, @NotNull String message) throws IllegalStateException {
        test(obj, predicate, () -> message);
    }

    /**
     * @throws IllegalStateException if given {@link Predicate} return false (i.e. if test not passed)
     */
    public static <T> void test(@Nullable T obj, @NotNull Predicate<T> predicate) throws IllegalStateException {
        test(obj, predicate, () -> "Contract broken: Object " + obj + " invalid!");
    }

}
