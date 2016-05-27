package ch.geomo.tramaps.graph;

import java.util.Set;

public interface Graph<E extends Edge<N>, N extends Node<E>> {
    Set<N> getNodes();
    Set<E> getEdges();
}
