package ch.geomo.tramaps;

import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.math.Vector2D;

public class DisplVector extends Vector2D {

    private final LineString lineString;

    public DisplVector(LineString lineString) {
        super(lineString.getStartPoint().getCoordinate(), lineString.getEndPoint().getCoordinate());
        this.lineString = lineString;
    }

    /**
     * @deprecated use {@link #length()}
     */
    @SuppressWarnings("unused")
    public double getDistance() {
        return super.length();
    }

    /**
     * @deprecated use {@link #angle()}
     */
    @SuppressWarnings("unused")
    public double getAngle() {
        return super.angle();
    }

    public LineString getLineString() {
        return lineString;
    }

    public DisplVector getProjectionAlong(DisplVector vector) {
        // TODO
        return this;
    }

}
