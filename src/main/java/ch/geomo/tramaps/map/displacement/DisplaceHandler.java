package ch.geomo.tramaps.map.displacement;

import ch.geomo.tramaps.conflicts.Conflict;
import ch.geomo.tramaps.geo.Axis;
import ch.geomo.tramaps.map.MetroMap;
import com.vividsolutions.jts.geom.Point;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.stream.Collectors;

public class DisplaceHandler implements MakeSpaceHandler {

    private void correctMap(MetroMap map) {
        System.out.println("Non-Octilinear Edges:" + map.evaluateNonOctilinearEdges().count());
    }

    private void makeSpace(MetroMap map, double routeMargin, double edgeMargin, int count) {

        count++;

        List<Conflict> conflicts = map.evaluateConflicts(routeMargin, edgeMargin, true)
                .collect(Collectors.toList());

        System.out.println("Iteration: " + count);
        System.out.println("Conflicts found: " + conflicts.size());

        if (!conflicts.isEmpty()) {

            Conflict conflict = conflicts.get(0);

            Point centroid = conflict.getConflictPolygon().getCentroid();

            if (conflict.getBestMoveVectorAxis() == Axis.X) {
                map.getNodes().stream()
                        .filter(node -> node.getPoint().getX() > centroid.getX())
                        .forEach(node -> node.setX(node.getX() + conflict.getBestMoveLengthAlongAnAxis()));
            }
            else {
                map.getNodes().stream()
                        .filter(node -> node.getPoint().getY() > centroid.getY())
                        .forEach(node -> node.setY(node.getY() + conflict.getBestMoveLengthAlongAnAxis()));
            }

            correctMap(map);

            if (count < 100) {
                makeSpace(map, routeMargin, edgeMargin, count);
            }

        }

    }

    @Override
    public void makeSpace(@NotNull MetroMap map, double routeMargin, double edgeMargin) {
        this.makeSpace(map, routeMargin, edgeMargin, 0);
        System.out.println(map);
    }

}
