package ch.geomo.tramaps;

import ch.geomo.tramaps.util.GeomUtil;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.math.Vector2D;

public class DisplVector extends Vector2D {

    private final LineString lineString;

    private final double distance;
    private final double angle;

    public DisplVector(LineString lineString) {
        super(lineString.getStartPoint().getCoordinate(), lineString.getEndPoint().getCoordinate());
        this.lineString = lineString;
        this.distance = lineString.getLength();
        this.angle = GeomUtil.getAngleToXAxis(lineString);
    }

    public double getDistance() {
        return distance;
    }

    public double getAngle() {
        return angle;
    }

    public LineString getLineString() {
        return lineString;
    }

    public DisplVector getProjectionAlong(DisplVector vector) {
        return this;
    }


}
