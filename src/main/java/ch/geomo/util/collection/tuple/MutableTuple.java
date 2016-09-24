/*
 * Copyright (c) 2016 Thomas Zuberbuehler. All rights reserved.
 */

package ch.geomo.util.collection.tuple;

public class MutableTuple<T, S> implements Tuple<T, S> {

    private T firstValue;
    private S secondValue;

    public MutableTuple(T firstValue, S secondValue) {
        this.firstValue = firstValue;
        this.secondValue = secondValue;
    }

    @Override
    public T getFirst() {
        return firstValue;
    }

    public void setFirst(T firstValue) {
        this.firstValue = firstValue;
    }

    @Override
    public S getSecond() {
        return secondValue;
    }

    public void setSecond(S secondValue) {
        this.secondValue = secondValue;
    }

}
