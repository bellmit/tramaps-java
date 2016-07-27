package ch.geomo.tramaps.map;

import ch.geomo.tramaps.graph.Edge;
import ch.geomo.tramaps.graph.Node;
import com.vividsolutions.jts.geom.Envelope;

import java.util.Set;

public class MetroMap {

    private Set<Edge> edges;
    private Set<Node> nodes;

    public Set<Edge> getEdges() {
        return edges;
    }

    public Set<Node> getNodes() {
        return nodes;
    }

    public Envelope getBoundingBox() {
        // TODO
        return null;
    }

}
