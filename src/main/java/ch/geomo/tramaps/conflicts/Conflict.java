package ch.geomo.tramaps.conflicts;

import ch.geomo.tramaps.geo.MoveVector;
import ch.geomo.tramaps.conflicts.buffer.ElementBuffer;
import ch.geomo.tramaps.geo.Axis;
import ch.geomo.tramaps.geo.util.GeomUtil;
import ch.geomo.tramaps.geo.util.PolygonUtil;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.math.Vector2D;
import org.jetbrains.annotations.NotNull;

public class Conflict {

    private final ElementBuffer bufferA;
    private final ElementBuffer bufferB;

    private Polygon conflictPolygon;
    private LineString q;
    private MoveVector moveVector;
    private Vector2D bestMoveVectorAlongAnAxis;

    private Vector2D xAxisMoveVector;
    private Vector2D yAxisMoveVector;

    private Axis bestMoveVectorAxis;

    private boolean solved = false;

    public Conflict(@NotNull ElementBuffer bufferA, @NotNull ElementBuffer bufferB) {
        this.bufferA = bufferA;
        this.bufferB = bufferB;
        this.updateConflict();
    }

    public void updateConflict() {

        // create conflict polygon
        this.conflictPolygon = (Polygon) this.bufferA.getBuffer().intersection(this.bufferB.getBuffer());

        // create line q
        this.q = GeomUtil.createLineString(bufferA.getElement().getCentroid(), bufferB.getElement().getCentroid());

        // create exact move vector
        this.moveVector = PolygonUtil.findLongestParallelLineString(this.conflictPolygon, this.q)
                .map(MoveVector::new)
                .orElse(new MoveVector());

        // evaluate best move vector along an axis
        double angleX = this.moveVector.angle(MoveVector.VECTOR_ALONG_X_AXIS);
        double angleY = this.moveVector.angle(MoveVector.VECTOR_ALONG_Y_AXIS);

        this.xAxisMoveVector = this.moveVector.getProjection(MoveVector.VECTOR_ALONG_X_AXIS);
        this.yAxisMoveVector = this.moveVector.getProjection(MoveVector.VECTOR_ALONG_Y_AXIS);

        if (angleY < angleX) {
            this.bestMoveVectorAlongAnAxis = this.xAxisMoveVector;
            this.bestMoveVectorAxis = Axis.X;
        }
        else {
            this.bestMoveVectorAlongAnAxis = yAxisMoveVector;
            this.bestMoveVectorAxis = Axis.Y;
        }

        if (this.conflictPolygon.isEmpty()) {
            this.solved = true;
        }

    }

    @NotNull
    public Polygon getConflictPolygon() {
        return this.conflictPolygon;
    }

    @NotNull
    public MoveVector getMoveVector() {
        return this.moveVector;
    }

    @NotNull
    public Vector2D getBestMoveVectorAlongAnAxis() {
        return this.bestMoveVectorAlongAnAxis;
    }

    public Vector2D getXAxisMoveVector() {
        return this.xAxisMoveVector;
    }

    public Vector2D getYAxisMoveVector() {
        return this.yAxisMoveVector;
    }

    @NotNull
    public Axis getBestMoveVectorAxis() {
        return this.bestMoveVectorAxis;
    }

    public boolean isSolved() {
        return this.solved;
    }

    public ElementBuffer getBufferA() {
        return bufferA;
    }

    public ElementBuffer getBufferB() {
        return bufferB;
    }

}
