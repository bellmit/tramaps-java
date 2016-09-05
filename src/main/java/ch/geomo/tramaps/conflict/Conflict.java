/*
 * Copyright (c) 2016 Thomas Zuberbuehler. All rights reserved.
 */

package ch.geomo.tramaps.conflict;

import ch.geomo.tramaps.conflict.buffer.EdgeBuffer;
import ch.geomo.tramaps.conflict.buffer.ElementBuffer;
import ch.geomo.tramaps.conflict.buffer.ElementBufferPair;
import ch.geomo.tramaps.geom.Axis;
import ch.geomo.tramaps.geom.MoveVector;
import ch.geomo.tramaps.geom.util.PolygonUtil;
import ch.geomo.tramaps.graph.Edge;
import ch.geomo.tramaps.graph.Node;
import ch.geomo.tramaps.graph.util.OctilinearDirection;
import ch.geomo.util.Loggers;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.math.Vector2D;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.stream.Stream;

import static ch.geomo.tramaps.geom.util.GeomUtil.getGeomUtil;

public class Conflict implements Comparable<Conflict> {

    private final static double MIN_DISPLACEMENT_LENGTH = 0.2d;

    private ElementBufferPair buffers;

    private Polygon conflictPolygon;

    private MoveVector displacementVector;
    private Axis bestDisplacementAxis;
    private Vector2D bestDisplacementVector;

    private LineString g;

    private ConflictType conflictType;
    private boolean solved = false;

    public Conflict(@NotNull ElementBuffer bufferA, @NotNull ElementBuffer bufferB) {
        buffers = new ElementBufferPair(bufferA, bufferB);
        initConflict();
        updateConflict();
    }

    private void initConflict() {
        // initialize conflict types
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

    public void updateConflict() {

        // create conflict polygon
        conflictPolygon = (Polygon) getBufferA().getBuffer().intersection(getBufferB().getBuffer());

        // create line q
        LineString q = getGeomUtil().createLineString(getBufferA().getElement().getCentroid(), getBufferB().getElement().getCentroid());

        // create exact move vector
        displacementVector = PolygonUtil.findLongestParallelLineString(conflictPolygon, q)
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

        // make precise
        double x = getGeomUtil().makePrecise(bestDisplacementVector.getX());
        double y = getGeomUtil().makePrecise(bestDisplacementVector.getY());
        bestDisplacementVector = new Vector2D(x, y);

        Point centroid = conflictPolygon.getCentroid();
        if (bestDisplacementAxis == Axis.X) {
            g = getGeomUtil().createLineString(new Coordinate(centroid.getX(), -1000000), new Coordinate(centroid.getX(), 1000000));
        }
        else {
            g = getGeomUtil().createLineString(new Coordinate(-1000000, centroid.getY()), new Coordinate(1000000, centroid.getY()));
        }

        if (conflictPolygon.isEmpty() || getBestDisplacementLength() < MIN_DISPLACEMENT_LENGTH) {
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

    public double getBestDisplacementLengthAlongAxis() {
        return getGeomUtil().makePrecise(getBestDisplacementVectorAlongAxis().length());
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
    public double getBestDisplacementLength() {
        return getBestDisplacementLengthAlongAxis();
    }

    @NotNull
    public LineString getG() {
        return g;
    }

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

    @NotNull
    public Stream<Edge> getEdges() {
        return Stream.of(getBufferA(), getBufferB())
                .filter(buf -> buf.getElement() instanceof EdgeBuffer)
                .map(buf -> (Edge) buf.getElement());
    }

    @NotNull
    public Stream<Node> getNodes() {
        return Stream.of(getBufferA(), getBufferB())
                .filter(buf -> buf.getElement() instanceof Node)
                .map(buf -> (Node) buf.getElement());
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
