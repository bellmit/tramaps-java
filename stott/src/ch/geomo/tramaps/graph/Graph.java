package ch.geomo.tramaps.graph;

import java.util.Set;

public interface Graph<T extends Number & Comparable<T>, E extends Edge<T, N>, N extends Node<T, E>> {
    Set<N> getNodes();
    Set<E> getEdges();
}
