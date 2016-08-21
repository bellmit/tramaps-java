package ch.geomo.tramaps.conflicts;

import ch.geomo.tramaps.conflicts.buffer.EdgeBuffer;
import ch.geomo.tramaps.conflicts.buffer.NodeBuffer;
import ch.geomo.tramaps.geo.MoveVector;
import ch.geomo.tramaps.conflicts.buffer.ElementBuffer;
import ch.geomo.tramaps.geo.Axis;
import ch.geomo.tramaps.geo.util.GeomUtil;
import ch.geomo.tramaps.geo.util.PolygonUtil;
import ch.geomo.tramaps.graph.Edge;
import ch.geomo.tramaps.graph.Node;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.math.Vector2D;
import org.jetbrains.annotations.NotNull;

import java.util.stream.Stream;

import static ch.geomo.tramaps.conflicts.ConflictType.EDGE_EDGE;
import static ch.geomo.tramaps.conflicts.ConflictType.NODE_EDGE;
import static ch.geomo.tramaps.conflicts.ConflictType.NODE_NODE;

public class Conflict {

    private final ElementBuffer bufferA;
    private final ElementBuffer bufferB;

    private Polygon conflictPolygon;
    private MoveVector moveVector;
    private Vector2D bestMoveVectorAlongAnAxis;

    private Vector2D xAxisMoveVector;
    private Vector2D yAxisMoveVector;

    private Axis bestMoveVectorAxis;

    private ConflictType conflictType;
    private boolean solved = false;

    public Conflict(@NotNull ElementBuffer bufferA, @NotNull ElementBuffer bufferB) {
        this.bufferA = bufferA;
        this.bufferB = bufferB;
        updateConflict();
    }

    private void updateConflictType() {
        long nodeCount = getNodes().count();
        conflictType = (nodeCount == 2 ? NODE_NODE : (nodeCount == 1 ? NODE_EDGE : EDGE_EDGE));
    }

    public void updateConflict() {

        // create conflict polygon
        conflictPolygon = (Polygon) bufferA.getBuffer().intersection(bufferB.getBuffer());

        updateConflictType();

        // create line q
        LineString q = GeomUtil.createLineString(bufferA.getElement().getCentroid(), bufferB.getElement().getCentroid());

        // create exact move vector
        moveVector = PolygonUtil.findLongestParallelLineString(conflictPolygon, q)
                .map(MoveVector::new)
                .orElse(new MoveVector());

        // evaluate best move vector along an axis
        double angleX = moveVector.angle(MoveVector.VECTOR_ALONG_X_AXIS);
        double angleY = moveVector.angle(MoveVector.VECTOR_ALONG_Y_AXIS);

        xAxisMoveVector = moveVector.getProjection(MoveVector.VECTOR_ALONG_X_AXIS);
        yAxisMoveVector = moveVector.getProjection(MoveVector.VECTOR_ALONG_Y_AXIS);

        if (angleY < angleX) {
            bestMoveVectorAlongAnAxis = xAxisMoveVector;
            bestMoveVectorAxis = Axis.X;
        }
        else {
            bestMoveVectorAlongAnAxis = yAxisMoveVector;
            bestMoveVectorAxis = Axis.Y;
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
    public MoveVector getMoveVector() {
        return moveVector;
    }

    public double getBestMoveLengthAlongAnAxis() {
        return Math.ceil(getBestMoveVectorAlongAnAxis().length()*100000)/100000;
    }

    @NotNull
    public Vector2D getBestMoveVectorAlongAnAxis() {
        return bestMoveVectorAlongAnAxis;
    }

    public Vector2D getXAxisMoveVector() {
        return xAxisMoveVector;
    }

    public Vector2D getYAxisMoveVector() {
        return yAxisMoveVector;
    }

    @NotNull
    public Axis getBestMoveVectorAxis() {
        return bestMoveVectorAxis;
    }

    public boolean isSolved() {
        return solved;
    }

    public ElementBuffer getBufferA() {
        return bufferA;
    }

    public ElementBuffer getBufferB() {
        return bufferB;
    }

    public ConflictType getConflictType() {
        return conflictType;
    }

    @NotNull
    @SuppressWarnings("unused")
    public Stream<Edge> getEdges() {
        return Stream.of(getBufferA(), getBufferB())
                .filter(buf -> buf.getElement() instanceof EdgeBuffer)
                .map(buf -> (Edge) ((EdgeBuffer)buf.getElement()).getElement());
    }

    @NotNull
    public Stream<Node> getNodes() {
        return Stream.of(getBufferA(), getBufferB())
                .filter(buf -> buf.getElement() instanceof NodeBuffer)
                .map(buf -> (Node)((NodeBuffer)buf.getElement()).getElement());
    }

}
