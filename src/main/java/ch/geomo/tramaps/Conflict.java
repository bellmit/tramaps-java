package ch.geomo.tramaps;

import ch.geomo.tramaps.buffer.ElementBuffer;
import ch.geomo.tramaps.graph.GraphElement;
import ch.geomo.tramaps.util.GeomUtil;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.Polygon;
import org.jetbrains.annotations.NotNull;

import javax.sound.sampled.Line;

public class Conflict {

    private final ElementBuffer bufferA;
    private final ElementBuffer bufferB;

    private Polygon conflictPolygon;
    private LineString q;

    public Conflict(@NotNull ElementBuffer bufferA, @NotNull ElementBuffer bufferB) {
        this.bufferA = bufferA;
        this.bufferB = bufferB;
        this.updateConflictPolygon();
    }

    public void updateConflictPolygon() {
        this.conflictPolygon = (Polygon) this.bufferA.getBuffer().intersection(this.bufferB.getBuffer());
        this.q = GeomUtil.createLineString(bufferA.getElement().getCentroid(), bufferB.getElement().getCentroid());
        // TODO linie verlängern
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

    public LineString getQ() {
        return q;
    }

}
