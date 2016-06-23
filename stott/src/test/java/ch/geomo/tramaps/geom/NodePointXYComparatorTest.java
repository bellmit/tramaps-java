/*
 * Copyright (c) 2016 by Thomas Zuberbühler
 */

package ch.geomo.tramaps.geom;

import ch.geomo.tramaps.util.point.NodePoint;
import ch.geomo.tramaps.util.point.NodePointXYComparator;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

public class NodePointXYComparatorTest {

    @Test
    public void test() {

        NodePoint p1 = NodePoint.of(1, 2);
        NodePoint p2 = NodePoint.of(3, 2);
        NodePoint p3 = NodePoint.of(2, 1);
        NodePoint p4 = NodePoint.of(2, 2);
        NodePoint p5 = NodePoint.of(2, 3);

        // create sorted list
        List<NodePoint> points = Arrays.asList(p1, p2, p3, p4, p5);
        points.sort(new NodePointXYComparator());

        Assert.assertEquals(p1, points.get(0));
        Assert.assertEquals(p3, points.get(1));
        Assert.assertEquals(p4, points.get(2));
        Assert.assertEquals(p5, points.get(3));
        Assert.assertEquals(p2, points.get(4));

    }

}
