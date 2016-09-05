/*
 * Copyright (c) 2016 Thomas Zuberbuehler. All rights reserved.
 */

package ch.geomo.tramaps.conflict;

import ch.geomo.tramaps.conflict.buffer.ElementBuffer;
import ch.geomo.tramaps.conflict.buffer.ElementBufferPair;
import ch.geomo.tramaps.geom.Axis;
import ch.geomo.tramaps.geom.MoveVector;
import ch.geomo.tramaps.geom.util.PolygonUtil;
import ch.geomo.tramaps.graph.Node;
import ch.geomo.tramaps.graph.util.OctilinearDirection;
import ch.geomo.util.Loggers;
import ch.geomo.util.MathUtil;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.math.Vector2D;
import com.vividsolutions.jts.operation.distance.DistanceOp;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import static ch.geomo.tramaps.geom.util.GeomUtil.getGeomUtil;

public class Conflict implements Comparable<Conflict> {

    private ElementBufferPair buffers;

    private Polygon conflictPolygon;

    private MoveVector displacementVector;
    private MoveVector bestDisplacementVector;
    private Axis bestDisplacementAxis;

    private Coordinate[] closestPointToA;
    private Coordinate[] closestPointToB;

    private ConflictType conflictType;
    private boolean solved = false;

    public Conflict(@NotNull ElementBuffer bufferA, @NotNull ElementBuffer bufferB) {
        buffers = new ElementBufferPair(bufferA, bufferB);
        initConflict();
        evaluateConflictType();
    }

    /**
     * Evaluates the conflict type.
     */
    private void evaluateConflictType() {
        if (buffers.isNodePair() && buffers.hasAdjacentElements()) {
            conflictType = ConflictType.ADJACENT_NODE_NODE;
        }
        else if (buffers.isNodePair()) {
            conflictType = ConflictType.NODE_NODE;
        }
        else if (buffers.isEdgePair()) {
            conflictType = ConflictType.EDGE_EDGE;
        }
        else {
            conflictType = ConflictType.NODE_EDGE;
        }
    }

    /**
     * @return polygon of the intersecting area of both buffers
     */
    @NotNull
    private Polygon createConflictPolygon() {
        return (Polygon) getBufferA().getBuffer().intersection(getBufferB().getBuffer());
    }

    /**
     * @return the {@link LineString} between the buffer element's centroid
     */
    @NotNull
    private LineString createQ() {
        return getGeomUtil().createLineString(getBufferA().getElement().getCentroid(), getBufferB().getElement().getCentroid());
    }

    private void initConflict() {

        // create conflict polygon
        conflictPolygon = createConflictPolygon();

        // create exact move vector
        displacementVector = PolygonUtil.findLongestParallelLineString(conflictPolygon, createQ())
                .map(MoveVector::new)
                .orElse(new MoveVector());

        // evaluate best move vector along an axis
        double angleX = displacementVector.angle(MoveVector.VECTOR_ALONG_X_AXIS);
        double angleY = displacementVector.angle(MoveVector.VECTOR_ALONG_Y_AXIS);

        if (angleY < angleX) {
            bestDisplacementVector = displacementVector.getProjection(MoveVector.VECTOR_ALONG_X_AXIS);
            bestDisplacementAxis = Axis.X;
        }
        else {
            bestDisplacementVector = displacementVector.getProjection(MoveVector.VECTOR_ALONG_Y_AXIS);
            bestDisplacementAxis = Axis.Y;
        }

        closestPointToA = DistanceOp.nearestPoints(conflictPolygon, getBufferA().getElement().getCentroid());
        closestPointToB = DistanceOp.nearestPoints(conflictPolygon, getBufferB().getElement().getCentroid());

        if (conflictPolygon.isEmpty()) {
            solved = true;
        }

    }

    public ConflictType getConflictType() {
        return conflictType;
    }

    @NotNull
    public Polygon getConflictPolygon() {
        return conflictPolygon;
    }

    @NotNull
    public MoveVector getDisplacementVector() {
        return displacementVector;
    }

    public int getBestDisplacementLengthAlongAxis() {
        // we do work with int values only
        return (int) Math.ceil(getBestDisplacementVectorAlongAxis().length());
    }

    @NotNull
    public Vector2D getBestDisplacementVectorAlongAxis() {
        return bestDisplacementVector;
    }

    @NotNull
    public Axis getBestDisplacementAxis() {
        return bestDisplacementAxis;
    }

    /**
     * @return best move direction along an axis
     * @see OctilinearDirection#NORTH
     * @see OctilinearDirection#EAST
     */
    public OctilinearDirection getBestDisplacementDirection() {
        if (bestDisplacementAxis == Axis.X) {
            return OctilinearDirection.NORTH;
        }
        return OctilinearDirection.EAST;
    }

    /**
     * @return best move distance along the best move direction
     * @see #getBestDisplacementDirection()
     */
    public int getBestDisplacementLength() {
        return getBestDisplacementLengthAlongAxis();
    }

    @NotNull
    public Coordinate getDisplaceStartPoint() {
        switch (conflictType) {
            case NODE_NODE:
            case ADJACENT_NODE_NODE: {
                return conflictPolygon.getCentroid().getCoordinate();
            }
            case EDGE_EDGE: {
                if (closestPointToA[0].distance(closestPointToA[1]) < closestPointToB[0].distance(closestPointToB[1])) {
                    return closestPointToA[0];
                }
                return closestPointToB[0];
            }
            case NODE_EDGE: {
                if (getBufferA().getElement() instanceof Node) {
                    return closestPointToA[0];
                }
                return closestPointToB[0];
            }
            default: {
                return conflictPolygon.getCentroid().getCoordinate();
            }
        }
    }

    /**
     * @return true if solved
     */
    public boolean isSolved() {
        return solved;
    }

    @NotNull
    public ElementBuffer getBufferA() {
        return buffers.first();
    }

    @NotNull
    public ElementBuffer getBufferB() {
        return buffers.second();
    }

    @Override
    public int compareTo(@NotNull Conflict o) {

        double length1a = getBestDisplacementLengthAlongAxis();
        double length2a = o.getBestDisplacementLengthAlongAxis();

        if (length1a != length2a) {
            return Double.compare(length1a, length2a);
        }

        double length1b = displacementVector.length();
        double length2b = o.getDisplacementVector().length();

        if (length1b != length2b) {
            return Double.compare(length1b, length2b);
        }

        double x1 = displacementVector.getX();
        double x2 = o.getDisplacementVector().getX();

        if (x1 != x2) {
            return Double.compare(x1, x2);
        }

        double y1 = displacementVector.getY();
        double y2 = o.getDisplacementVector().getY();

        if (y1 != y2) {
            return Double.compare(y1, y2);
        }

        Loggers.warning(this, "Conflicts are equals. Output might not be reproduceable.");
        return 0;

    }

    @Override
    public boolean equals(Object obj) {
        double length = getBestDisplacementLengthAlongAxis();
        return obj instanceof Conflict
                && Objects.equals(length, ((Conflict) obj).getBestDisplacementLengthAlongAxis())
                && Objects.equals(displacementVector.length(), ((Conflict) obj).getDisplacementVector().length())
                && Objects.equals(displacementVector.getX(), ((Conflict) obj).getDisplacementVector().getX())
                && Objects.equals(displacementVector.getY(), ((Conflict) obj).getDisplacementVector().getY());
    }

    @Override
    public int hashCode() {
        double length = getBestDisplacementLengthAlongAxis();
        return Objects.hash(length, displacementVector.length(), displacementVector.getX(), displacementVector.getY());
    }

    @Override
    public String toString() {
        return "Conflict: {" + getBufferA() + ", " + getBufferB() + "}";
    }

}
