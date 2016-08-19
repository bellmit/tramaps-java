package ch.geomo.tramaps.map;

import ch.geomo.tramaps.graph.Edge;
import ch.geomo.tramaps.graph.Graph;
import ch.geomo.tramaps.graph.Node;
import com.vividsolutions.jts.geom.Envelope;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class MetroMap extends Graph {

    public MetroMap() {
        super();
    }

    public MetroMap(@NotNull Set<Edge> edges, @NotNull Set<Node> nodes) {
        super(edges, nodes);
    }

    @Override
    public String toString() {
        return new StringBuilder()
                .append("Edges: ")
                .append(getEdges())
                .append("\n")
                .append("Nodes: ")
                .append(getNodes())
                .toString();
    }
}
