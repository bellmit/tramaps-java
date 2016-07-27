package ch.geomo.tramaps;

import ch.geomo.tramaps.graph.Edge;
import ch.geomo.tramaps.graph.Node;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;

import java.util.Set;

public class MetroMap {

    private Set<Edge> edges;
    private Set<Node> nodes;

    private Set<Conflict> conflicts;

    public Set<Edge> getEdges() {
        return edges;
    }

    public Set<Node> getNodes() {
        return nodes;
    }

    public Set<Conflict> getConflicts() {
        return conflicts;
    }

    public Envelope getBoundingBox() {
        // TODO
        return null;
    }

}
