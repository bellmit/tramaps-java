package ch.geomo.tramaps;

import ch.geomo.tramaps.buffer.ElementBuffer;
import ch.geomo.tramaps.geom.GeomUtil;
import ch.geomo.tramaps.geom.PolygonUtil;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.Polygon;
import org.jetbrains.annotations.NotNull;
import org.opengis.referencing.operation.TransformException;

public class Conflict {

    private final ElementBuffer bufferA;
    private final ElementBuffer bufferB;

    private Polygon conflictPolygon;
    private LineString q;
    private MoveVector moveVector;

    public Conflict(@NotNull ElementBuffer bufferA, @NotNull ElementBuffer bufferB) {
        this.bufferA = bufferA;
        this.bufferB = bufferB;
        this.updateConflict();
    }

    public void updateConflict() {
        this.conflictPolygon = (Polygon) this.bufferA.getBuffer().intersection(this.bufferB.getBuffer());
        this.q = GeomUtil.createLineString(bufferA.getElement().getCentroid(), bufferB.getElement().getCentroid());
        try {
            this.moveVector = PolygonUtil.findParallelLineStringStream(this.conflictPolygon, this.q)
                    .max((l1, l2) -> Double.compare(l1.getLength(), l2.getLength()))
                    .map(qp -> this.moveVector = new MoveVector(qp))
                    .orElse(new MoveVector(GeomUtil.createLineString()));
        }
        catch (TransformException e) {
            // TODO log exception
            this.moveVector = new MoveVector(GeomUtil.createLineString());
        }
    }

    @NotNull
    public Polygon getConflictPolygon() {
        return this.conflictPolygon;
    }

    @NotNull
    public Geometry getBoundingBox() {
        return this.conflictPolygon.getEnvelope();
    }

    public MoveVector getMoveVector() {
        return this.moveVector;
    }

    public LineString getQ() {
        return this.q;
    }

}
