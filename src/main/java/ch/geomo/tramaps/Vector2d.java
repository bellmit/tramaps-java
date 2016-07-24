package ch.geomo.tramaps;

import ch.geomo.tramaps.util.GeomUtil;
import com.vividsolutions.jts.geom.LineString;

public class Vector2d {

    private final LineString lineString;

    private final double distance;
    private final double angle;

    public Vector2d(LineString lineString) {
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

    public Vector2d getProjectionAlong(Vector2d vector) {
        return this;
    }


}
