package ch.geomo.tramaps.buffer;

import ch.geomo.tramaps.graph.GraphElement;
import com.vividsolutions.jts.geom.Polygon;
import org.jetbrains.annotations.NotNull;

import java.util.Observer;

public interface ElementBuffer extends Observer {

    @NotNull
    Polygon getBuffer();

    void updateBuffer();

    @NotNull
    GraphElement getElement();

}
