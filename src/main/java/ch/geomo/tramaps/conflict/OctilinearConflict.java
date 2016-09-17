/*
 * Copyright (c) 2016 Thomas Zuberbuehler. All rights reserved.
 */

package ch.geomo.tramaps.conflict;

import ch.geomo.tramaps.conflict.buffer.ElementBuffer;
import ch.geomo.tramaps.geom.Axis;
import ch.geomo.tramaps.geom.MoveVector;
import ch.geomo.tramaps.graph.Edge;
import ch.geomo.tramaps.graph.Node;
import ch.geomo.util.collection.pair.Pair;
import com.vividsolutions.jts.math.Vector2D;

public class OctilinearConflict extends AbstractConflict {

    public OctilinearConflict(Pair<ElementBuffer> bufferPair) {
        super(bufferPair);
        evaluateConflictType();
        initConflict();
    }

    private void evaluateConflictType() {
        conflictType = ConflictType.ADJACENT_NODE_NODE_DIAGONAL;
    }

    private void initConflict() {

        Node nodeA = getNodes().get(0);
        Node nodeB = getNodes().get(1);
        Edge adjacentEdge = nodeA.getAdjacentEdgeWith(nodeB);

        if (adjacentEdge == null) {
            solved = true;
            return;
        }

        double dx = Math.abs(nodeA.getX() - nodeB.getX());
        double dy = Math.abs(nodeA.getY() - nodeB.getY());

        // double diff = Math.abs(dx - dy);
        double diff = Math.abs(dx - dy) / 2;

        if (dx > dy) {
            bestDisplaceAxis = Axis.Y;
            displaceVector = new MoveVector(new Vector2D(0, diff));
        }
        else {
            bestDisplaceAxis = Axis.X;
            displaceVector = new MoveVector(new Vector2D(diff, 0));
        }
        bestDisplaceVector = displaceVector;
        bestDisplaceStartPoint = adjacentEdge.getCentroid().getCoordinate();

        projection = displaceVector.getProjection(MoveVector.VECTOR_ALONG_X_AXIS);

        if (!adjacentEdge.hasMajorMisalignment()) {
            solved = true;
        }

    }

    @Override
    public double getDisplaceDistanceAlongX() {
        return Math.abs(projection.getFirst().length());
    }

    @Override
    public double getDisplaceDistanceAlongY() {
        return Math.abs(projection.getSecond().length());
    }

}
