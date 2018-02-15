/*
 * Copyright (c) 2016-2018 Thomas Zuberbuehler. All rights reserved.
 */

package ch.geomo.tramaps.graph.direction;

import org.junit.jupiter.api.Test;

import static ch.geomo.tramaps.graph.direction.OctilinearDirection.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DirectionTest {

    @Test
    void testGetAngleTo() {

        Direction direction = new AnyDirection(30);
        assertEquals(direction.getAngleTo(EAST), 60d);
        assertEquals(direction.getAngleTo(SOUTH_EAST), 105d);
        assertEquals(direction.getAngleTo(SOUTH), 150d);
        assertEquals(direction.getAngleTo(SOUTH_WEST), 195d);
        assertEquals(direction.getAngleTo(WEST), 240d);
        assertEquals(direction.getAngleTo(NORTH_WEST), 285d);
        assertEquals(direction.getAngleTo(NORTH), 330d);
        assertEquals(direction.getAngleTo(NORTH_EAST), 15d);

        direction = new AnyDirection(175);
        assertEquals(direction.getAngleTo(EAST), 275d);
        assertEquals(direction.getAngleTo(SOUTH_EAST), 320d);
        assertEquals(direction.getAngleTo(SOUTH), 5d);
        assertEquals(direction.getAngleTo(SOUTH_WEST), 50d);
        assertEquals(direction.getAngleTo(WEST), 95d);
        assertEquals(direction.getAngleTo(NORTH_WEST), 140d);
        assertEquals(direction.getAngleTo(NORTH), 185d);
        assertEquals(direction.getAngleTo(NORTH_EAST), 230d);

    }

}
