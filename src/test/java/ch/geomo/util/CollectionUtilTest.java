/*
 * Copyright (c) 2016 Thomas Zuberbuehler. All rights reserved.
 */

package ch.geomo.util;

import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

public class CollectionUtilTest {

    @Test
    public void testMakePairs() {
        // TODO implement unit test
    }

    @Test
    public void testEquals() {
        List<Integer> list1 = Arrays.asList(1, 2, 3);
        List<Integer> list2 = Arrays.asList(1, 2, 3, 4);
        List<Integer> list3 = Arrays.asList(1, 2, 3);
        List<Integer> list4 = Arrays.asList(1, 2);
        Assert.assertTrue(CollectionUtil.equals(list1, list3));
        Assert.assertFalse(CollectionUtil.equals(list1, list2));
        Assert.assertFalse(CollectionUtil.equals(list1, list4));
        Assert.assertFalse(CollectionUtil.equals(list2, list4));
        Assert.assertFalse(CollectionUtil.equals(list3, list4));
        Assert.assertFalse(CollectionUtil.equals(list3, list2));
    }

    @Test
    public void testSort() {
        List<Integer> list = Arrays.asList(1, 3, 2);
        List<Integer> sorted = CollectionUtil.sort(list);
        Assert.assertEquals(1, (int)sorted.get(0));
        Assert.assertEquals(2, (int)sorted.get(1));
        Assert.assertEquals(3, (int)sorted.get(2));
    }

    @Test
    public void testReverse() {
        List<Integer> list = Arrays.asList(1, 3, 2);
        List<Integer> sorted = CollectionUtil.reverse(list);
        Assert.assertEquals(2, (int)sorted.get(0));
        Assert.assertEquals(3, (int)sorted.get(1));
        Assert.assertEquals(1, (int)sorted.get(2));
    }

    @Test
    public void testReverseSort() {
        List<Integer> list = Arrays.asList(1, 3, 2);
        List<Integer> sorted = CollectionUtil.reverseSort(list);
        Assert.assertEquals(3, (int)sorted.get(0));
        Assert.assertEquals(2, (int)sorted.get(1));
        Assert.assertEquals(1, (int)sorted.get(2));
    }

}
