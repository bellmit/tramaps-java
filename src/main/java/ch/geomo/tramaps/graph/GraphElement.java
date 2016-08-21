package ch.geomo.tramaps.graph;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Point;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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

    @Contract("null->false")
    boolean isAdjacent(@Nullable Edge edge);

    @Contract("null->false")
    boolean isAdjacent(@Nullable Node node);

    @NotNull
    Geometry getGeometry();

    @NotNull
    default Point getCentroid() {
        return this.getGeometry().getCentroid();
    }

}
