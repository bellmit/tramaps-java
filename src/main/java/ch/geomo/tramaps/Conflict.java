package ch.geomo.tramaps;

import ch.geomo.tramaps.buffer.ElementBuffer;
import ch.geomo.tramaps.geom.GeomUtil;
import ch.geomo.tramaps.geom.PolygonUtil;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.Polygon;
import org.jetbrains.annotations.NotNull;

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
        this.moveVector = PolygonUtil.findLongestParallelLineString(this.conflictPolygon, this.q)
                .map(MoveVector::new)
                .orElse(new MoveVector(GeomUtil.createLineString()));
    }

    @NotNull
    public Polygon getConflictPolygon() {
        return this.conflictPolygon;
    }

    @NotNull
    public Geometry getBoundingBox() {
        return this.conflictPolygon.getEnvelope();
    }

    @NotNull
    public MoveVector getMoveVector() {
        return this.moveVector;
    }

    @NotNull
    public LineString getQ() {
        return this.q;
    }

}
