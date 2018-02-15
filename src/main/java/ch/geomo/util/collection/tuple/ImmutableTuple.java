/*
 * Copyright (c) 2016-2018 Thomas Zuberbuehler. All rights reserved.
 */

package ch.geomo.util.collection.tuple;

import org.jetbrains.annotations.Nullable;

public class ImmutableTuple<T, S> implements Tuple<T, S> {

    private final T firstValue;
    private final S secondValue;

    public ImmutableTuple(@Nullable T firstValue, @Nullable S secondValue) {
        this.firstValue = firstValue;
        this.secondValue = secondValue;
    }

    @Override
    public T getFirst() {
        return firstValue;
    }

    @Override
    public S getSecond() {
        return secondValue;
    }

}
