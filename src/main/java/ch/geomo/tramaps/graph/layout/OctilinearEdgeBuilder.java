/*
 * Copyright (c) 2016 Thomas Zuberbuehler. All rights reserved.
 */

package ch.geomo.tramaps.graph.layout;

import ch.geomo.tramaps.graph.Edge;
import ch.geomo.tramaps.graph.Node;
import ch.geomo.tramaps.graph.util.OctilinearDirection;
import ch.geomo.tramaps.map.signature.BendNodeSignature;
import ch.geomo.util.collection.GCollectors;
import ch.geomo.util.collection.list.EnhancedList;
import ch.geomo.util.collection.pair.MutablePair;
import ch.geomo.util.logging.Loggers;
import org.jetbrains.annotations.NotNull;

import static ch.geomo.tramaps.graph.util.OctilinearDirection.*;

public class OctilinearEdgeBuilder {

    private MutablePair<Node> vertices;
    private Edge originalEdge;

    public OctilinearEdgeBuilder() {
        vertices = new MutablePair<>();
    }

    @NotNull
    public OctilinearEdgeBuilder setOriginalEdge(@NotNull Edge edge) {
        originalEdge = edge;
        return this;
    }

    @NotNull
    public OctilinearEdge build() {
        createNodes();
        OctilinearEdge octilinearEdge = new OctilinearEdge(originalEdge);
        octilinearEdge.setVertices(vertices);
        return octilinearEdge;
    }

    private Node getNodeA() {
        return originalEdge.getNodeA();
    }

    private Node getNodeB() {
        return originalEdge.getNodeB();
    }

    private void createNodeC(double x, double y) {
        vertices.set(0, new Node(x, y, BendNodeSignature::new));
    }

    private void createNodeD(double x, double y) {
        vertices.set(1, new Node(x, y, BendNodeSignature::new));
    }

    private boolean isReversedOrder() {
        return !getNodeA().isSouthOf(getNodeB());
    }

    /**
     * Creates the vertices based on the original edge and checks the result
     */
    private void createNodes() {

        Loggers.info(this, "Create vertices for octilinear edge.");

        vertices.clear();


        Node a = isReversedOrder() ? getNodeB() : getNodeA();
        Node b = isReversedOrder() ? getNodeA() : getNodeB();

        EnhancedList<OctilinearDirection> adjacentEdgeDirectionsA = a.getAdjacentEdges().stream()
                .filter(edge -> originalEdge.isNotEquals(edge))
                .map(edge -> edge.getOriginalDirection(a).toOctilinear())
                .collect(GCollectors.toList());

        EnhancedList<OctilinearDirection> adjacentEdgeDirectionsB = b.getAdjacentEdges().stream()
                .filter(edge -> originalEdge.isNotEquals(edge))
                .map(edge -> edge.getOriginalDirection(b).toOctilinear())
                .collect(GCollectors.toList());

        double dx = Math.abs(b.getX() - a.getX());
        double dy = Math.abs(b.getY() - a.getY());

        OctilinearDirection originalEdgeDirection = originalEdge.getOriginalDirection(a).toOctilinear();

        double diff = Math.abs(dx - dy);

        if (originalEdgeDirection == OctilinearDirection.NORTH_EAST) {
            if (dx > dy) {
                if (!adjacentEdgeDirectionsA.contains(EAST) && !adjacentEdgeDirectionsB.contains(WEST)) { // 3
                    double x1 = a.getX() + diff / 2;
                    double y1 = a.getY();
                    double x2 = b.getX() - diff / 2;
                    double y2 = b.getY();
                    createNodeC(x1, y1);
                    createNodeD(x2, y2);
                }
                else if (!adjacentEdgeDirectionsA.contains(EAST)) { // 1
                    double x = a.getX() + diff;
                    double y = a.getY();
                    createNodeC(x, y);
                }
                else if (!adjacentEdgeDirectionsB.contains(WEST)) { // 2
                    double x = b.getX() - diff;
                    double y = b.getY();
                    createNodeC(x, y);
                }
                else { // 4
                    double x1 = a.getX() + dy / 2;
                    double y1 = a.getY() + dy / 2;
                    double x2 = b.getX() - dy / 2;
                    double y2 = b.getY() - dy / 2;
                    createNodeC(x1, y1);
                    createNodeD(x2, y2);
                }
            }
            else { // dy > dx
                if (!adjacentEdgeDirectionsA.contains(NORTH) && !adjacentEdgeDirectionsB.contains(SOUTH)) { // 9
                    double x1 = a.getX();
                    double y1 = a.getY() + diff / 2;
                    double x2 = b.getX();
                    double y2 = b.getY() - diff / 2;
                    createNodeC(x1, y1);
                    createNodeD(x2, y2);
                }
                else if (!adjacentEdgeDirectionsA.contains(NORTH)) { // 7
                    double x = a.getX();
                    double y = a.getY() + diff;
                    createNodeC(x, y);
                }
                else if (!adjacentEdgeDirectionsB.contains(SOUTH)) { // 8
                    double x = b.getX();
                    double y = b.getY() - diff;
                    createNodeC(x, y);
                }
                else { // 10
                    double x1 = a.getX() + dx / 2;
                    double y1 = a.getY() + dx / 2;
                    double x2 = b.getX() - dx / 2;
                    double y2 = b.getY() - dx / 2;
                    createNodeC(x1, y1);
                    createNodeD(x2, y2);
                }
            }
        }
        else if (originalEdgeDirection == OctilinearDirection.NORTH_WEST) {
            if (dx > dy) {
                if (!adjacentEdgeDirectionsA.contains(EAST) && !adjacentEdgeDirectionsB.contains(WEST)) { // 7b
                    double x1 = a.getX() - diff / 2;
                    double y1 = a.getY();
                    double x2 = b.getX() + diff / 2;
                    double y2 = b.getY();
                    createNodeC(x1, y1);
                    createNodeD(x2, y2);
                }
                else if (!adjacentEdgeDirectionsA.contains(WEST)) { // 6b
                    double x = a.getX() - diff;
                    double y = a.getY();
                    createNodeC(x, y);
                }
                else if (!adjacentEdgeDirectionsB.contains(EAST)) { // 5b
                    double x = b.getX() + diff;
                    double y = b.getY();
                    createNodeC(x, y);
                }
                else { // 8b
                    double x1 = a.getX() - dy / 2;
                    double y1 = a.getY() + dy / 2;
                    double x2 = b.getX() + dy / 2;
                    double y2 = b.getY() - dy / 2;
                    createNodeC(x1, y1);
                    createNodeD(x2, y2);
                }
            }
            else { // dy > dx
                if (!adjacentEdgeDirectionsA.contains(NORTH) && !adjacentEdgeDirectionsB.contains(SOUTH)) { // 1b
                    double x1 = a.getX();
                    double y1 = a.getY() + diff / 2;
                    double x2 = b.getX();
                    double y2 = b.getY() - diff / 2;
                    createNodeC(x1, y1);
                    createNodeD(x2, y2);
                }
                else if (!adjacentEdgeDirectionsA.contains(NORTH)) { // 3b
                    double x = a.getX();
                    double y = a.getY() + diff;
                    createNodeC(x, y);
                }
                else if (!adjacentEdgeDirectionsB.contains(SOUTH)) { // 2b
                    double x = b.getX();
                    double y = b.getY() - diff;
                    createNodeC(x, y);
                }
                else { // 4b
                    double x1 = a.getX() - dx / 2;
                    double y1 = a.getY() + dx / 2;
                    double x2 = b.getX() + dx / 2;
                    double y2 = b.getY() - dx / 2;
                    createNodeC(x1, y1);
                    createNodeD(x2, y2);
                }
            }
        }

        if (vertices.nonNullStream().count() == 1) {
            vertices.get(0).setName(getNodeA().getName() + "-" + getNodeB().getName());
        }
        else if (vertices.nonNullStream().count() == 2) {
            if (isReversedOrder()) {
                vertices.swapValues();
            }
            vertices.get(0).setName(getNodeA().getName() + "+-" + getNodeB().getName());
            vertices.get(1).setName(getNodeA().getName() + "-+" + getNodeB().getName());
        }

    }

}
