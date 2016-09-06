/*
 * Copyright (c) 2016 Thomas Zuberbuehler. All rights reserved.
 */

package ch.geomo.tramaps.geom;

import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.math.Vector2D;
import org.jetbrains.annotations.NotNull;

import static ch.geomo.tramaps.geom.util.GeomUtil.getGeomUtil;

/**
 * An implementation of {@link Vector2D} providing constructor to create a vector with a {@link LineString}.
 *
 * @see Vector2D
 */
public class MoveVector extends Vector2D {

    public static final Vector2D VECTOR_ALONG_X_AXIS = new Vector2D(1, 0);
    public static final Vector2D VECTOR_ALONG_Y_AXIS = new Vector2D(0, 1);

    public MoveVector() {
        super(0, 0);
    }

    public MoveVector(@NotNull LineString lineString) {
        super(lineString.getStartPoint().getCoordinate(), lineString.getEndPoint().getCoordinate());
    }

    public MoveVector(@NotNull Vector2D vector2D) {
        super(vector2D.getX(), vector2D.getY());
    }

    @Override
    public double getX() {
        return getGeomUtil().getPrecisionModel().makePrecise(super.getX());
    }

    @Override
    public double getY() {
        return getGeomUtil().getPrecisionModel().makePrecise(super.getY());
    }

    @NotNull
    public MoveVector getProjection(@NotNull Vector2D alongVector) {
        return new MoveVector(alongVector.multiply(dot(alongVector) / alongVector.dot(alongVector)));
    }

    @Override
    public String toString() {
        return "MoveVector: {x= " + getX() + ", y=" + getY() + "}";
    }

}
