/*
 * Copyright (c) 2016 Thomas Zuberbuehler. All rights reserved.
 */

package ch.geomo.tramaps.graph.util;

import org.junit.Assert;
import org.junit.Test;

import static ch.geomo.tramaps.graph.util.OctilinearDirection.*;

public class DirectionTest {

    @Test
    public void testGetAngleTo() {

        Direction direction = new AnyDirection(30);
        Assert.assertEquals(direction.getAngleTo(EAST), 60, 0);
        Assert.assertEquals(direction.getAngleTo(SOUTH_EAST), 105, 0);
        Assert.assertEquals(direction.getAngleTo(SOUTH), 150, 0);
        Assert.assertEquals(direction.getAngleTo(SOUTH_WEST), 195, 0);
        Assert.assertEquals(direction.getAngleTo(WEST), 240, 0);
        Assert.assertEquals(direction.getAngleTo(NORTH_WEST), 285, 0);
        Assert.assertEquals(direction.getAngleTo(NORTH), 330, 0);
        Assert.assertEquals(direction.getAngleTo(NORTH_EAST), 15, 0);

        direction = new AnyDirection(175);
        Assert.assertEquals(direction.getAngleTo(EAST), 275, 0);
        Assert.assertEquals(direction.getAngleTo(SOUTH_EAST), 320, 0);
        Assert.assertEquals(direction.getAngleTo(SOUTH), 5, 0);
        Assert.assertEquals(direction.getAngleTo(SOUTH_WEST), 50, 0);
        Assert.assertEquals(direction.getAngleTo(WEST), 95, 0);
        Assert.assertEquals(direction.getAngleTo(NORTH_WEST), 140, 0);
        Assert.assertEquals(direction.getAngleTo(NORTH), 185, 0);
        Assert.assertEquals(direction.getAngleTo(NORTH_EAST), 230, 0);

    }

}
