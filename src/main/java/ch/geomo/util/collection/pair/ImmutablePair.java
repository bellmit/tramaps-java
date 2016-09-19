/*
 * Copyright (c) 2016 Thomas Zuberbuehler. All rights reserved.
 */

package ch.geomo.util.collection.pair;

import org.jetbrains.annotations.Nullable;

/**
 * An implementation set an immutable pair.
 */
public class ImmutablePair<T> extends AbstractPair<T> {

    public ImmutablePair() {
        super();
    }

    public ImmutablePair(@Nullable Pair<T> pair) {
        super(pair);
    }

    public ImmutablePair(@Nullable T first, @Nullable T second) {
        super(first, second);
    }

}
