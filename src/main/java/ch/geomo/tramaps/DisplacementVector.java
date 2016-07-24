package ch.geomo.tramaps;

import ch.geomo.tramaps.util.GeomUtil;
import com.vividsolutions.jts.geom.LineString;

public class DisplacementVector {

    private final LineString lineString;

    private final double distance;
    private final double angle;

    public DisplacementVector(LineString lineString) {
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
}
