package ch.geomo.tramaps.criteria.node;

import ch.geomo.tramaps.criteria.AbstractNodeCriterion;
import ch.geomo.tramaps.grid.GridEdge;
import ch.geomo.tramaps.grid.GridGraph;
import ch.geomo.tramaps.util.GeomUtil;
import ch.geomo.tramaps.util.point.MutableNodePoint;
import ch.geomo.tramaps.util.point.NodePoint;
import org.jetbrains.annotations.Contract;

import java.util.Set;

public class OctilinearityCriterion extends AbstractNodeCriterion {

    public OctilinearityCriterion(double weight) {
        super(weight, false);
    }

    @Contract(pure = true)
    private double getDifferenceToClosestOctilinearAngle(double angle) {
        double diff = angle % 45;
        return (diff < 45 / 2) ? diff : 45 - diff;
    }

    @Override
    public double _calculate(GridGraph graph) {
        final Set<GridEdge> edges = graph.getEdges();
        return edges.parallelStream()
                .mapToDouble(edge -> {

                    MutableNodePoint reference = new MutableNodePoint(edge.getNodeA());
                    reference.moveX(100);

                    double angle = GeomUtil.getAngleBetweenAsDegree(edge.getNodeA(), edge.getNodeB(), reference);

                    return Math.pow(getDifferenceToClosestOctilinearAngle(angle), 2);

                })
                .sum();
    }

}