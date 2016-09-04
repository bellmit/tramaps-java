/*
 * Copyright (c) 2016 Thomas Zuberbuehler. All rights reserved.
 */

package ch.geomo.tramaps.conflict;

import ch.geomo.tramaps.conflict.buffer.EdgeBuffer;
import ch.geomo.tramaps.conflict.buffer.ElementBuffer;
import ch.geomo.tramaps.geo.Axis;
import ch.geomo.tramaps.geo.MoveVector;
import ch.geomo.tramaps.geo.util.GeomUtil;
import ch.geomo.tramaps.geo.util.PolygonUtil;
import ch.geomo.tramaps.graph.Edge;
import ch.geomo.tramaps.graph.Node;
import ch.geomo.tramaps.graph.util.OctilinearDirection;
import ch.geomo.tramaps.map.MetroMap;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.math.Vector2D;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class Conflict implements Comparable<Conflict> {

    private final ElementBuffer bufferA;
    private final ElementBuffer bufferB;

    private Polygon conflictPolygon;

    private MoveVector displacementVector;
    private Axis bestDisplacementAxis;
    private Vector2D bestDisplacementVectorAlongAxis;

    private LineString g;

    private boolean solved = false;

    public Conflict(@NotNull ElementBuffer bufferA, @NotNull ElementBuffer bufferB) {
        this.bufferA = bufferA;
        this.bufferB = bufferB;
        updateConflict();
    }

    public void updateConflict() {

        // create conflict polygon
        conflictPolygon = (Polygon) bufferA.getBuffer().intersection(bufferB.getBuffer());

        // create line q
        LineString q = GeomUtil.createLineString(bufferA.getElement().getCentroid(), bufferB.getElement().getCentroid());

        // create exact move vector
        displacementVector = PolygonUtil.findLongestParallelLineString(conflictPolygon, q)
                .map(MoveVector::new)
                .orElse(new MoveVector());

        // evaluate best move vector along an axis
        double angleX = displacementVector.angle(MoveVector.VECTOR_ALONG_X_AXIS);
        double angleY = displacementVector.angle(MoveVector.VECTOR_ALONG_Y_AXIS);

        if (angleY < angleX) {
            bestDisplacementVectorAlongAxis = displacementVector.getProjection(MoveVector.VECTOR_ALONG_X_AXIS);
            bestDisplacementAxis = Axis.X;
        }
        else {
            bestDisplacementVectorAlongAxis = displacementVector.getProjection(MoveVector.VECTOR_ALONG_Y_AXIS);
            bestDisplacementAxis = Axis.Y;
        }

        Point centroid = conflictPolygon.getCentroid();
        if (bestDisplacementAxis == Axis.X) {
            g = GeomUtil.createLineString(new Coordinate(centroid.getX(), -1000000), new Coordinate(centroid.getX(), 1000000));
        }
        else {
            g = GeomUtil.createLineString(new Coordinate(-1000000, centroid.getY()), new Coordinate(1000000, centroid.getY()));
        }

        if (conflictPolygon.isEmpty()) {
            solved = true;
        }

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
        return Math.ceil(getBestDisplacementVectorAlongAxis().length() * 100000) / 100000;
    }

    @NotNull
    public Vector2D getBestDisplacementVectorAlongAxis() {
        return bestDisplacementVectorAlongAxis;
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
        return bufferA;
    }

    @NotNull
    public ElementBuffer getBufferB() {
        return bufferB;
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
    public String toString() {
        return "Conflict: {" + bufferA + ", " + bufferB + "}";
    }

    @Override
    public int compareTo(@NotNull Conflict o) {
        if (getDisplacementVector().equals(o.getDisplacementVector())) {
            // same move vector
            // TODO distinct conflict in order to reproduce same sequence
        }
        double l1 = o.getBestDisplacementVectorAlongAxis().length();
        double l2 = getBestDisplacementVectorAlongAxis().length();
        if (l1 == l2) {
            l1 = l1 + o.getDisplacementVector().length();
            l2 = l2 + this.getDisplacementVector().length();
        }
        if (l1 == l2) {
            // same distance
            double x1 = o.getDisplacementVector().getX();
            double x2 = this.getDisplacementVector().getX();
            if (x1 == x2) {
                double y1 = o.getDisplacementVector().getY();
                double y2 = this.getDisplacementVector().getY();
                return Double.compare(y1, y2);
            }
            // same distance but different directions
            return Double.compare(x1, x2);
        }
        return Double.compare(l2, l1);
    }

}
