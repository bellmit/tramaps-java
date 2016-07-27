package ch.geomo.tramaps.geo;

import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.math.Vector2D;
import org.jetbrains.annotations.NotNull;

public class MoveVector extends Vector2D {

    @SuppressWarnings("unused")
    public static final Vector2D VECTOR_ALONG_X_AXIS = new Vector2D(1, 0);
    @SuppressWarnings("unused")
    public static final Vector2D VECTOR_ALONG_Y_AXIS = new Vector2D(0, 1);

    private final LineString lineString;

    public MoveVector(LineString lineString) {
        super(lineString.getStartPoint().getCoordinate(), lineString.getEndPoint().getCoordinate());
        this.lineString = lineString;
    }

    @SuppressWarnings("unused")
    public LineString getLineString() {
        return lineString;
    }

    @NotNull
    public Vector2D getProjection(@NotNull Vector2D alongVector) {
        return alongVector.multiply(this.dot(alongVector)/this.dot(this));
    }

}
