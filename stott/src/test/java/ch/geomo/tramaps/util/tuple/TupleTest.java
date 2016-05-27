/*
 * Copyright (c) 2016 by Thomas Zuberbühler
 */

package ch.geomo.tramaps.util.tuple;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.Assert;
import org.junit.Test;

public class TupleTest {

    @Test
    public void testEquals() {
        Tuple<String> tuple1 = Tuple.of("A", "B");
        Tuple<String> tuple2 = Tuple.of("B", "A");
        Assert.assertEquals(tuple1, tuple2);
        Tuple<String> tuple3 = Tuple.of("A", "C");
        Assert.assertNotEquals(tuple1, tuple3);
    }

    @Test
    public void testGetters() {
        Tuple<String> tuple = Tuple.of("A", "C");
        Assert.assertEquals("A", tuple.getFirst());
        Assert.assertEquals("C", tuple.getSecond());
        Assert.assertEquals(tuple.getFirst(), tuple.get(0));
        Assert.assertEquals(tuple.getSecond(), tuple.get(1));
    }

    @Test
    public void testToPair() {
        Tuple<String> tuple = Tuple.of("A", "C");
        Pair<String, String> pair = tuple.toPair();
        Assert.assertEquals(tuple.getFirst(), pair.getLeft());
        Assert.assertEquals(tuple.getSecond(), pair.getRight());
    }

}
