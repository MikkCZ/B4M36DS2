package cz.cvut.fel.stankmic.ds2.neo4j;

public enum NodeLabels {
    VISITOR("visitor"), PLACE("place"), PARK("park");

    private final String text;

    NodeLabels(final String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return text;
    }
}
