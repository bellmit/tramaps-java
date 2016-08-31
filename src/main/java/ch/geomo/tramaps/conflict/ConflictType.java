package ch.geomo.tramaps.conflict;

public enum ConflictType {

    NODE_NODE(3),
    NODE_EDGE(2),
    EDGE_EDGE(1);

    private int weight;

    ConflictType(int weight) {
        this.weight = weight;
    }

    public int getWeight() {
        return weight;
    }

}
