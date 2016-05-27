package ch.geomo.tramaps.criteria.node;

import ch.geomo.tramaps.criteria.AbstractNodeCriterion;
import ch.geomo.tramaps.grid.GridEdge;
import ch.geomo.tramaps.grid.GridGraph;
import ch.geomo.tramaps.util.tuple.Tuple;
import org.apache.commons.lang3.tuple.Pair;

import java.util.HashMap;
import java.util.Map;

public class EdgeCrossingCriterion extends AbstractNodeCriterion {

    private final Map<String, Pair<Integer, Boolean>> cache = new HashMap<>();

    public EdgeCrossingCriterion(double weight) {
        super(weight, false);
    }

    private boolean intersects(Tuple<GridEdge> tuple) {
        if (cache.containsKey(tuple.toString())) {
            Pair<Integer, Boolean> value = cache.get(tuple.toString());
            if (value.getLeft() == tuple.hashCode()) {
                return value.getRight();
            }
        }
        boolean testResult = tuple.get(0).intersects(tuple.get(1));
        cache.put(tuple.toString(), Pair.of(tuple.hashCode(), testResult));
        return testResult;
    }

    @Override
    public double _calculate(GridGraph graph) {
        return graph.getIntersectingEdgePairs().stream()
                .filter(this::intersects)
                .count();
    }

}