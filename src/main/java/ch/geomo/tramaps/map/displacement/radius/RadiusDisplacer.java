/*
 * Copyright (c) 2016 Thomas Zuberbuehler. All rights reserved.
 */

package ch.geomo.tramaps.map.displacement.radius;

import ch.geomo.tramaps.conflict.Conflict;
import ch.geomo.tramaps.graph.Node;
import ch.geomo.tramaps.map.MetroMap;
import ch.geomo.util.Loggers;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.math.Vector2D;
import org.jetbrains.annotations.NotNull;

public class RadiusDisplacer {

    private final MetroMap map;
    private final Conflict conflict;

    public RadiusDisplacer(@NotNull MetroMap map, @NotNull Conflict conflict) {
        this.map = map;
        this.conflict = conflict;
    }

    private Coordinate getDisplacePoint() {
        return conflict.getDisplaceOriginPoint();
    }

    private boolean isNorth(@NotNull Node node) {
        return node.getX() == getDisplacePoint().x && node.getY() > getDisplacePoint().y;
    }

    private boolean isWest(@NotNull Node node) {
        return node.getX() < getDisplacePoint().x && node.getY() == getDisplacePoint().y;
    }

    private boolean isSouth(@NotNull Node node) {
        return node.getX() == getDisplacePoint().x && node.getY() < getDisplacePoint().y;
    }

    private boolean isEast(@NotNull Node node) {
        return node.getX() > getDisplacePoint().x && node.getY() == getDisplacePoint().y;
    }

    private boolean isNorthEast(@NotNull Node node) {
        return node.getX() > getDisplacePoint().x && node.getY() > getDisplacePoint().y;
    }

    private boolean isNorthWest(@NotNull Node node) {
        return node.getX() < getDisplacePoint().x && node.getY() > getDisplacePoint().y;
    }

    private boolean isSouthWest(@NotNull Node node) {
        return node.getX() < getDisplacePoint().x && node.getY() < getDisplacePoint().y;
    }

    private boolean isSouthEast(@NotNull Node node) {
        return node.getX() > getDisplacePoint().x && node.getY() < getDisplacePoint().y;
    }

    public void displace() {

        double displaceDistance = conflict.getBestDisplaceDistance();

        for (Node node : map.getNodes()) {

            if (isNorthEast(node)) {
                Loggers.flag(this, "Displace node " + node.getName() + " to northeast (distance=" + conflict.getBestDisplaceDistance() + ").");
                node.updatePosition(node.getX() + displaceDistance, node.getY() + displaceDistance);
            }
            else if (isNorthWest(node)) {
                Loggers.flag(this, "Displace node " + node.getName() + " to northwest (distance=" + conflict.getBestDisplaceDistance() + ").");
                node.updatePosition(node.getX() - displaceDistance, node.getY() + displaceDistance);
            }
            else if (isSouthWest(node)) {
                Loggers.flag(this, "Displace node " + node.getName() + " to southwest (distance=" + conflict.getBestDisplaceDistance() + ").");
                node.updatePosition(node.getX() - displaceDistance, node.getY() - displaceDistance);
            }
            else if (isSouthEast(node)) {
                Loggers.flag(this, "Displace node " + node.getName() + " to southeast (distance=" + conflict.getBestDisplaceDistance() + ").");
                node.updatePosition(node.getX() + displaceDistance, node.getY() - displaceDistance);
            }
            else if (isNorth(node)) {
                Loggers.flag(this, "Displace node " + node.getName() + " to north (distance=" + conflict.getBestDisplaceDistance() + ").");
                node.updatePosition(node.getX(), node.getY() + displaceDistance);
            }
            else if (isEast(node)) {
                Loggers.flag(this, "Displace node " + node.getName() + " to east (distance=" + conflict.getBestDisplaceDistance() + ").");
                node.updatePosition(node.getX() + displaceDistance, node.getY());
            }
            else if (isSouth(node)) {
                Loggers.flag(this, "Displace node " + node.getName() + " to south (distance=" + conflict.getBestDisplaceDistance() + ").");
                node.updatePosition(node.getX(), node.getY() - displaceDistance);
            }
            else if (isWest(node)) {
                Loggers.flag(this, "Displace node " + node.getName() + " to west (distance=" + conflict.getBestDisplaceDistance() + ").");
                node.updatePosition(node.getX() - displaceDistance, node.getY());
            }

//            // Point point = conflict.getBufferA().getElement().getCentroid();
//            Point point = GeomUtil.createPoint(getDisplacePoint());
//            if (node.getPoint().equals(point)) {
//                return;
//            }
//
//            double a = Math.abs(node.getX() - point.getX());
//            double b = Math.abs(node.getY() - point.getY());
//            double c = Math.sqrt(Math.pow(a, 2) + Math.pow(b, 2));
//
//            double dx = a / c * displaceDistance;
//            double dy = b / c * displaceDistance;
//
//            Loggers.flag(this, "Displace node " + node.getName() + " (vector=" + new Vector2D(dx, dy) + ", distance=" + conflict.getBestDisplaceDistance() + ").");
//
//            if (isNorth(node) || isNorthEast(node)) {
//                node.updatePosition(node.getX() + dx, node.getY() + dy);
//            }
//            else if (isWest(node) || isNorthWest(node)) {
//                node.updatePosition(node.getX() - dx, node.getY() + dy);
//            }
//            else if (isSouth(node) || isSouthWest(node)) {
//                node.updatePosition(node.getX() - dx, node.getY() - dy);
//            }
//            else if (isEast(node) || isSouthEast(node)) {
//                node.updatePosition(node.getX() + dx, node.getY() - dy);
//            }

        }

    }

}
