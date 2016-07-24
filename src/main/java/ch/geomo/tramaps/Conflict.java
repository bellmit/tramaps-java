package ch.geomo.tramaps;

import ch.geomo.tramaps.buffer.ElementBuffer;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Polygon;
import org.jetbrains.annotations.NotNull;

public class Conflict {

    private final ElementBuffer bufferA;
    private final ElementBuffer bufferB;

    private Polygon conflictPolygon;

    public Conflict(@NotNull ElementBuffer bufferA, @NotNull ElementBuffer bufferB) {
        this.bufferA = bufferA;
        this.bufferB = bufferB;
        this.updateConflictPolygon();
    }

    public void updateConflictPolygon() {
        this.conflictPolygon = (Polygon) this.bufferA.getBuffer().intersection(this.bufferB.getBuffer());
    }

    @NotNull
    public Polygon getConflictPolygon() {
        return conflictPolygon;
    }

    @NotNull
    public Geometry getBoundingBox() {
        return this.conflictPolygon.getEnvelope();
    }

    public void getDisplacementVector() {
        // TODO
    }

}
