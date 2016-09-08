/*
 * Copyright (c) 2016 Thomas Zuberbuehler. All rights reserved.
 */

package ch.geomo.tramaps.geom;

import ch.geomo.tramaps.geom.util.GeomUtil;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.math.Vector2D;
import org.jetbrains.annotations.NotNull;

import static ch.geomo.tramaps.geom.util.GeomUtil.getGeomUtil;

/**
 * An implementation of {@link Vector2D} providing constructor to create a vector from a {@link LineString} as well
 * as getter-methods returning x- and y-values made precise using {@link GeomUtil#getPrecisionModel()}.
 *
 * @see Vector2D
 */
public class MoveVector extends Vector2D {

    public static final Vector2D VECTOR_ALONG_X_AXIS = new Vector2D(1, 0);
    public static final Vector2D VECTOR_ALONG_Y_AXIS = new Vector2D(0, 1);

    public MoveVector() {
        super(0, 0);
    }

    public MoveVector(@NotNull Vector2D vector) {
        super(vector);
    }

    public MoveVector(@NotNull LineString lineString) {
        super(lineString.getStartPoint().getCoordinate(), lineString.getEndPoint().getCoordinate());
    }

    /**
     * @return the x-value made precise using {@link GeomUtil#getPrecisionModel()}
     */
    @Override
    public double getX() {
        return getGeomUtil().getPrecisionModel().makePrecise(super.getX());
    }

    /**
     * @return the y-value made precise using {@link GeomUtil#getPrecisionModel()}
     */
    @Override
    public double getY() {
        return getGeomUtil().getPrecisionModel().makePrecise(super.getY());
    }

    /**
     * @return the projection of this vector along given vector
     */
    @NotNull
    public MoveVector getProjection(@NotNull Vector2D alongVector) {
        return new MoveVector(alongVector.multiply(dot(alongVector) / alongVector.dot(alongVector)));
    }

    @Override
    public String toString() {
        return "MoveVector: {x= " + getX() + ", y=" + getY() + "}";
    }

}
