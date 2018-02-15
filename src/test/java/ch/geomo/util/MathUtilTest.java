/*
 * Copyright (c) 2016-2018 Thomas Zuberbuehler. All rights reserved.
 */

package ch.geomo.util;

import ch.geomo.util.math.MathUtil;

import java.util.Comparator;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class MathUtilTest {

    private final Comparator<String> comparator = String.CASE_INSENSITIVE_ORDER;

    @Test
    public void testMin() {
        String min = MathUtil.min("abcd", "zyxw", comparator);
        assertEquals("abcd", min);
        min = MathUtil.min("zyxw", "abcd", comparator);
        assertEquals("abcd", min);
        min = MathUtil.min("abcd", "abcd", comparator);
        assertEquals("abcd", min);
    }

    @Test
    public void testMax() {
        String max = MathUtil.max("abcd", "zyxw", comparator);
        assertEquals("zyxw", max);
        max = MathUtil.max("zyxw", "abcd", comparator);
        assertEquals("zyxw", max);
        max = MathUtil.max("zyxw", "zyxw", comparator);
        assertEquals("zyxw", max);
    }

}
