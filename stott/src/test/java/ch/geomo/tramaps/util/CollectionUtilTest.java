/*
 * Copyright (c) 2016 by Thomas Zuberbühler
 */

package ch.geomo.tramaps.util;

import ch.geomo.tramaps.util.tuple.Tuple;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class CollectionUtilTest {

    @Test
    public void testMakePermutations() {

        List<String> strings = Arrays.asList("A", "B", "C");
        Set<Tuple<String>> tuples = CollectionUtil.makePermutations(strings);
        Assert.assertEquals(9, tuples.size());

        tuples = CollectionUtil.makePermutations(strings, true);
        Assert.assertEquals(6, tuples.size());

    }

}
