/*
 * Copyright (c) 2016 Thomas Zuberbuehler. All rights reserved.
 */

package ch.geomo.util;

import org.junit.Test;

import java.util.*;

public class Playground {

    @Test
    public void testStream() {

        Set<Integer> set = new HashSet<>(Arrays.asList(1, 2, 3));
        set.stream()
                .flatMap(i1 -> set.stream()
                        .map(i2 -> new int[]{i1, i2}))
                .forEach(arr -> System.out.println(Arrays.toString(arr)));

        List<Integer> list = new ArrayList<>(Arrays.asList(1, 2, 3));
        list.stream()
                .flatMap(i1 -> list.stream()
                        .map(i2 -> new int[]{i1, i2}))
                .forEach(arr -> System.out.println(Arrays.toString(arr)));


    }

}
