/*
 * Copyright (c) 2016 Thomas Zuberbuehler. All rights reserved.
 */

package ch.geomo.util;

import org.junit.Assert;
import org.junit.Test;

import java.util.Comparator;

public class MathUtilTest {

    private final Comparator<String> comparator = String.CASE_INSENSITIVE_ORDER;

    @Test
    public void testMin() {
        String min = MathUtil.min("abcd", "zyxw", comparator);
        Assert.assertEquals("abcd", min);
        min = MathUtil.min("zyxw", "abcd", comparator);
        Assert.assertEquals("abcd", min);
        min = MathUtil.min("abcd", "abcd", comparator);
        Assert.assertEquals("abcd", min);
    }

    @Test
    public void testMax() {
        String max = MathUtil.max("abcd", "zyxw", comparator);
        Assert.assertEquals("zyxw", max);
        max = MathUtil.max("zyxw", "abcd", comparator);
        Assert.assertEquals("zyxw", max);
        max = MathUtil.max("zyxw", "zyxw", comparator);
        Assert.assertEquals("zyxw", max);
    }

}
