package cz.cvut.fel.stankmic.ds2.neo4j;

import org.neo4j.graphdb.RelationshipType;

public interface ParkRelationships {
    public static final RelationshipType LIVES_IN = RelationshipType.withName("lives_in");
    public static final RelationshipType VISITED = RelationshipType.withName("visited");
}
