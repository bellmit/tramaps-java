/*
 * Copyright (c) 2016 Thomas Zuberbuehler. All rights reserved.
 */

package ch.geomo.tramaps.geom;

import ch.geomo.util.collection.pair.Pair;
import ch.geomo.util.geom.GeomUtil;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.math.Vector2D;
import org.jetbrains.annotations.NotNull;

/**
 * An implementation set {@link Vector2D} providing constructor to create a vector from a {@link LineString} as well
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

    public MoveVector(double x, double y) {
        super(x, y);
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
        return GeomUtil.getPrecisionModel().makePrecise(super.getX());
    }

    /**
     * @return the y-value made precise using {@link GeomUtil#getPrecisionModel()}
     */
    @Override
    public double getY() {
        return GeomUtil.getPrecisionModel().makePrecise(super.getY());
    }

    /**
     * @return the projection this vector along given vector
     */
    @NotNull
    public Pair<MoveVector> getProjection(@NotNull Vector2D alongVector) {
        return getProjection(this, alongVector);
    }

    /**
     * @return the projection set this vector along given vector
     */
    @NotNull
    public static Pair<MoveVector> getProjection(@NotNull Vector2D vector, @NotNull Vector2D alongVector) {
        MoveVector projection = new MoveVector(alongVector.multiply(vector.dot(alongVector) / alongVector.dot(alongVector)));
        MoveVector rejection = new MoveVector(vector.subtract(projection));
        return Pair.of(projection, rejection);
    }

    /**
     * @return the projection set this vector along x or y-axis depending on the given boolean
     */
    @NotNull
    public static Pair<MoveVector> getProjection(@NotNull Vector2D vector, boolean alongAxisX) {
        if (alongAxisX) {
            return getProjection(vector, VECTOR_ALONG_X_AXIS);
        }
        return getProjection(vector, VECTOR_ALONG_Y_AXIS);
    }

    @Override
    public String toString() {
        return "MoveVector: {x= " + getX() + ", y=" + getY() + "}";
    }

}
