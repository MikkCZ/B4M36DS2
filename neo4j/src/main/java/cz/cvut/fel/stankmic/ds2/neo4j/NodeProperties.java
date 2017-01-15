package cz.cvut.fel.stankmic.ds2.neo4j;

public enum NodeProperties {
    NAME("name"), OPENED("opened"), TIMESTAMP("timestamp");

    private final String text;

    NodeProperties(final String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return text;
    }
}
