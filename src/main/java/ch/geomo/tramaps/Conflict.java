package ch.geomo.tramaps;

import ch.geomo.tramaps.buffer.ElementBuffer;
import ch.geomo.tramaps.geom.Axis;
import ch.geomo.tramaps.geom.GeomUtil;
import ch.geomo.tramaps.geom.PolygonUtil;
import com.vividsolutions.jts.awt.PointShapeFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.math.Vector2D;
import org.jetbrains.annotations.NotNull;
import org.opengis.referencing.cs.AxisDirection;

public class Conflict {

    private final ElementBuffer bufferA;
    private final ElementBuffer bufferB;

    private Polygon conflictPolygon;
    private LineString q;
    private MoveVector moveVector;
    private Vector2D bestMoveVectorAlongAnAxis;
    private Axis bestMoveVectorAxis;

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
                .orElse(new MoveVector(GeomUtil.createLineString()));

        // evaluate best move vector along an axis
        double angleX = this.moveVector.angle(MoveVector.VECTOR_ALONG_X_AXIS);
        double angleY = this.moveVector.angle(MoveVector.VECTOR_ALONG_Y_AXIS);
        if (angleX < angleY) {
            this.bestMoveVectorAlongAnAxis = this.moveVector.getProjection(MoveVector.VECTOR_ALONG_X_AXIS);
            this.bestMoveVectorAxis = Axis.X;
        }
        else {
            this.bestMoveVectorAlongAnAxis = this.moveVector.getProjection(MoveVector.VECTOR_ALONG_Y_AXIS);
            this.bestMoveVectorAxis = Axis.Y;
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

    @NotNull
    public Axis getBestMoveVectorAxis() {
        return bestMoveVectorAxis;
    }
}
