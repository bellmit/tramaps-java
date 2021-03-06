/*
 * Copyright (c) 2016-2018 Thomas Zuberbuehler. All rights reserved.
 */

package ch.geomo.tramaps.conflict;

import ch.geomo.tramaps.conflict.buffer.ElementBuffer;
import ch.geomo.tramaps.graph.Edge;
import ch.geomo.tramaps.graph.Node;
import ch.geomo.util.collection.pair.Pair;
import ch.geomo.util.geom.Axis;
import ch.geomo.util.math.MoveVector;
import com.vividsolutions.jts.math.Vector2D;
import org.jetbrains.annotations.NotNull;

/**
 * Conflict between two adjacent nodes with a diagonal edge. This kind of conflict is given when the difference between
 * the angle of the original octilinear diagonal and the current angle is greater than 27.5 degree.
 * <p>
 * Using this conflict avoids prevents a diagonal edge from changing to another quadrant of the cartesian coordinate
 * system.
 * <p>
 * The property {@link OctilinearConflict#correctionFactor} regulates how much the angle should be corrected. The
 * value 1 (one) should restore the octilinearity may leads to a very stretched layout when correcting these kinds
 * of {@link Conflict} iteratively.
 * @see ConflictFinder#hasOctilinearConflict(Pair, boolean)
 */
public class OctilinearConflict extends AbstractConflict {

    private final double correctionFactor;

    public OctilinearConflict(@NotNull Pair<ElementBuffer> bufferPair, double correctionFactor) {
        super(bufferPair);
        this.correctionFactor = correctionFactor;
        conflictType = ConflictType.OCTILINEAR;
        initConflict();
    }

    /**
     * Initialize this conflict.
     */
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

        double diff = Math.abs(dx - dy) * correctionFactor;

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

    /**
     * Returns the displace distance along the x-axis. In order to solve the conflict when
     * using a correction factor of 1, this implementation does not return a rounded value
     * like the default implementation does.
     * @return AbstractConflict#getDisplaceDistanceAlongX
     */
    @Override
    public double getDisplaceDistanceAlongX() {
        return Math.abs(projection.getFirst().length());
    }

    /**
     * Returns the displace distance along the y-axis. In order to solve the conflict when
     * using a correction factor of 1, this implementation does not return a rounded value
     * like the default implementation does.
     * @return AbstractConflict#getDisplaceDistanceAlongY
     */
    @Override
    public double getDisplaceDistanceAlongY() {
        return Math.abs(projection.getSecond().length());
    }

}
