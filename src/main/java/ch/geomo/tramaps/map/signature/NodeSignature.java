package ch.geomo.tramaps.map.signature;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Polygon;
import org.jetbrains.annotations.NotNull;

import java.util.Observable;
import java.util.Observer;

/**
 * Represents a signature of a node.
 */
public abstract class NodeSignature extends Observable implements Observer {

    /**
     * @return the convex hull of the signature's geometry
     * @see #getGeometry()
     */
    @NotNull
    public abstract Geometry getConvexHull();

    /**
     * @return the signature's geometry
     */
    @NotNull
    public abstract Polygon getGeometry();

}
