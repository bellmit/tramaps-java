/*
 * Copyright (c) 2016 Thomas Zuberbuehler. All rights reserved.
 */

package ch.geomo.util.collection;

import ch.geomo.util.collection.list.EnhancedList;
import ch.geomo.util.collection.set.EnhancedSet;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GCollectorsTest {

    @Test
    public void testToList() {
        List<Integer> list = Arrays.asList(1, 2, 3, 4);
        EnhancedList<Integer> enhancedList = list.stream().collect(GCollectors.toList());
        Assert.assertTrue(enhancedList.hasEqualContent(list));
        Assert.assertEquals(list.get(0), enhancedList.get(0));
        Assert.assertEquals(2, enhancedList.removeElements(i -> i % 2 != 0).size());
    }

    @Test
    public void testToSet() {
        Set<Integer> set = new HashSet<>(Arrays.asList(1, 2, 3, 4));
        EnhancedSet<Integer> enhancedSet = set.stream().collect(GCollectors.toSet());
        Assert.assertTrue(enhancedSet.hasEqualContent(set));
        Assert.assertEquals(set.stream().findFirst().orElse(null), enhancedSet.first().orElse(null));
        Assert.assertEquals(2, enhancedSet.removeElements(i -> i % 2 != 0).size());
        EnhancedList<Integer> enhancedList = set.stream().collect(GCollectors.toList());
        Assert.assertTrue(enhancedList.hasEqualContent(set));
        Assert.assertEquals(set.stream().findFirst().orElse(null), enhancedList.first().orElse(null));
        Assert.assertEquals(2, enhancedList.removeElements(i -> i % 2 != 0).size());
    }

}
