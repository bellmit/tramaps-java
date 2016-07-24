package ch.geomo.tramaps.graph;

import ch.geomo.tramaps.util.GeomUtil;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.Point;
import org.jetbrains.annotations.NotNull;

public interface GraphElement {

    default boolean isAdjacent(GraphElement element) {
        if (element instanceof Edge) {
            return isAdjacent((Edge)element);
        }
        if (element instanceof Node) {
            return isAdjacent((Node)element);
        }
        return false;
    }

    boolean isAdjacent(Edge edge);

    boolean isAdjacent(Node node);

    @NotNull
    Geometry getGeometry();

    @NotNull
    default Point getCentroid() {
        return this.getGeometry().getCentroid();
    }

    default LineString getQ(GraphElement element) {
        return GeomUtil.createLineString(this.getCentroid(), element.getCentroid());
    }

}
