package cz.cvut.fel.stankmic.ds2.neo4j;

import org.neo4j.cypher.internal.compiler.v3_0.EmptyResourceIterator;
import org.neo4j.graphdb.*;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.graphdb.traversal.*;

import java.io.File;
import java.time.Instant;
import java.util.Collection;
import java.util.HashSet;
import java.util.stream.Collectors;

import static cz.cvut.fel.stankmic.ds2.neo4j.NodeLabels.*;
import static cz.cvut.fel.stankmic.ds2.neo4j.NodeProperties.*;
import static cz.cvut.fel.stankmic.ds2.neo4j.ParkRelationships.LIVES_IN;
import static cz.cvut.fel.stankmic.ds2.neo4j.ParkRelationships.VISITED;

public class Main {

    public static void main(String[] args) {
        final File dbFile = new File("target/stankmicNeo4jDB");
        final boolean dataExist = dbFile.exists();
        final GraphDatabaseService db = new GraphDatabaseFactory().newEmbeddedDatabase(dbFile);
        if (!dataExist) {
            System.out.println("Importing data...");
            importData(db);
        } else {
            System.out.println("Data already imported.");
        }
        System.out.println();

        graphTraversals(db);
        cypherQueries(db);

        db.shutdown();
    }

    private static void importData(GraphDatabaseService db) {
        try(Transaction tx = db.beginTx()) {
            // Insert about 10 nodes and 15 relationships into your database, both with a few properties.
            // Work with at least 2 different node labels and 2 relationship types. Associate nodes with user identifiers.
            Node prague = db.createNode(Label.label(PLACE.toString()));
            prague.setProperty(NAME.toString(), "Prague");

            Node mexicoCity = db.createNode(Label.label(PLACE.toString()));
            mexicoCity.setProperty(NAME.toString(), "Mexico City");

            Node hobbiton = db.createNode(Label.label(PLACE.toString()));
            hobbiton.setProperty(NAME.toString(), "Hobbiton");

            Node shire = db.createNode(Label.label(PLACE.toString()));
            shire.setProperty(NAME.toString(), "Shire");

            Node billund = db.createNode(Label.label(PARK.toString()));
            billund.setProperty(NAME.toString(), "Legoland Billund Resort");
            billund.setProperty(OPENED.toString(), 1968);

            Node windsor = db.createNode(Label.label(PARK.toString()));
            windsor.setProperty(NAME.toString(), "Legoland Windsor Resort");
            windsor.setProperty(OPENED.toString(), 1996);

            Node california = db.createNode(Label.label(PARK.toString()));
            california.setProperty(NAME.toString(), "Legoland California");
            california.setProperty(OPENED.toString(), 1999);

            Node guenzburg = db.createNode(Label.label(PARK.toString()));
            guenzburg.setProperty(NAME.toString(), "Legoland Deutschland Resort");
            guenzburg.setProperty(OPENED.toString(), 2002);

            Node florida = db.createNode(Label.label(PARK.toString()));
            florida.setProperty(NAME.toString(), "Legoland Florida Resort");
            florida.setProperty(OPENED.toString(), 2011);

            Node malaysia = db.createNode(Label.label(PARK.toString()));
            malaysia.setProperty(NAME.toString(), "Legoland Malaysia Resort");
            malaysia.setProperty(OPENED.toString(), 2012);

            Node stankmic = db.createNode(Label.label(VISITOR.toString()));
            stankmic.setProperty(NAME.toString(), "Michal Stanke");
            stankmic.createRelationshipTo(prague, LIVES_IN);
            stankmic.createRelationshipTo(guenzburg, VISITED).setProperty(TIMESTAMP.toString(), 1451365554L);

            Node jdoe = db.createNode(Label.label(VISITOR.toString()));
            jdoe.setProperty(NAME.toString(), "John Doe");
            jdoe.createRelationshipTo(mexicoCity, LIVES_IN);
            jdoe.createRelationshipTo(florida, VISITED).setProperty(TIMESTAMP.toString(), 1448755200L);
            jdoe.createRelationshipTo(california, VISITED).setProperty(TIMESTAMP.toString(), 1480333333L);

            Node alice = db.createNode(Label.label(VISITOR.toString()));
            alice.setProperty(NAME.toString(), "Alice");
            alice.createRelationshipTo(prague, LIVES_IN);

            Node bobicek = db.createNode(Label.label(VISITOR.toString()));
            bobicek.setProperty(NAME.toString(), "Bob");
            bobicek.createRelationshipTo(mexicoCity, LIVES_IN);

            Node theRING = db.createNode(Label.label(VISITOR.toString()));
            theRING.setProperty(NAME.toString(), "Bilbo Baggins");
            theRING.createRelationshipTo(hobbiton, LIVES_IN);
            theRING.createRelationshipTo(guenzburg, VISITED).setProperty(TIMESTAMP.toString(), 1451665553L);

            Node mordor = db.createNode(Label.label(VISITOR.toString()));
            mordor.setProperty(NAME.toString(), "Samwise Gamgee");
            mordor.createRelationshipTo(hobbiton, LIVES_IN);
            mordor.createRelationshipTo(guenzburg, VISITED).setProperty(TIMESTAMP.toString(), 1449446400L);
            mordor.createRelationshipTo(windsor, VISITED).setProperty(TIMESTAMP.toString(), 1451365554L);

            Node matrix = db.createNode(Label.label(VISITOR.toString()));
            matrix.setProperty(NAME.toString(), "Peregrin Took");
            matrix.createRelationshipTo(shire, LIVES_IN);
            matrix.createRelationshipTo(malaysia, VISITED).setProperty(TIMESTAMP.toString(), 1480000000L);

            tx.success();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    private static void graphTraversals(GraphDatabaseService db) {
        // Define and process results of at least 2 non-trivial graph traversals (with expanders and evaluators).
        // Describe meaning of both your graph traversals and Cypher expressions, comment your source file.
        try(Transaction ignored = db.beginTx()) {
            {
                System.out.println(":List visits in the year 2016:");
                final Instant start = Instant.parse("2016-01-01T00:00:00Z");
                final Instant stop = Instant.parse("2017-01-01T00:00:00Z");
                TraversalDescription td = db.traversalDescription()
                        .breadthFirst()
                        .uniqueness(Uniqueness.NODE_PATH)
                        .expand(new PathExpander<Object>() {
                            @Override
                            public Iterable<Relationship> expand(Path path, BranchState<Object> state) {
                                return path.endNode().getRelationships(VISITED, Direction.OUTGOING);
                            }

                            @Override
                            public PathExpander<Object> reverse() {
                                return this;
                            }
                        })
                        .evaluator((Path path) -> {
                            Relationship r = path.lastRelationship();
                            if (r != null && r.isType(VISITED)) {
                                Instant visited = Instant.ofEpochSecond(Long.valueOf(r.getProperty(TIMESTAMP.toString()).toString()));
                                if (visited.isAfter(start) && visited.isBefore(stop)) {
                                    return Evaluation.INCLUDE_AND_CONTINUE;
                                } else {
                                    return Evaluation.EXCLUDE_AND_PRUNE;
                                }
                            } else { // exclude but continue (first step)
                                return Evaluation.EXCLUDE_AND_CONTINUE;
                            }
                        });
                Traverser t = td.traverse(db.findNodes(Label.label(VISITOR.toString())).stream().collect(Collectors.toList()));
                t.stream()
                        .map(p -> {
                            Object visitor = p.startNode().getProperty(NAME.toString());
                            Object park = p.endNode().getProperty(NAME.toString());
                            Object time = Instant.ofEpochSecond(Long.valueOf(p.lastRelationship().getProperty(TIMESTAMP.toString()).toString()));
                            return String.format("%s visited %s on %s", visitor, park, time);
                        })
                        .forEach(System.out::println);
            }
            System.out.println();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }

        try(Transaction ignored = db.beginTx()) {
            {
                System.out.println(":List cities, that the visitors in the year 2016 originated from:");
                final Instant start = Instant.parse("2016-01-01T00:00:00Z");
                final Instant stop = Instant.parse("2017-01-01T00:00:00Z");
                TraversalDescription td = db.traversalDescription()
                        .breadthFirst()
                        .expand(new PathExpander<Object>() {
                            @Override
                            public Iterable<Relationship> expand(Path path, BranchState<Object> state) {
                                Collection<String> lastNodeLabels = new HashSet<>();
                                path.endNode().getLabels().forEach(label -> lastNodeLabels.add(label.toString()));
                                if (lastNodeLabels.contains(PARK.toString())){
                                    return path.endNode().getRelationships(VISITED, Direction.INCOMING);
                                } else if (lastNodeLabels.contains(VISITOR.toString())) {
                                    return path.endNode().getRelationships(LIVES_IN, Direction.OUTGOING);
                                } else {
                                    return EmptyResourceIterator::new;
                                }
                            }

                            @Override
                            public PathExpander<Object> reverse() {
                                return this;
                            }
                        })
                        .evaluator((Path path) -> {
                            Relationship r = path.lastRelationship();
                            if (r == null) { // exclude but continue (first step)
                                return Evaluation.EXCLUDE_AND_CONTINUE;
                            }
                            if (r.isType(VISITED)) {
                                Instant visited = Instant.ofEpochSecond(Long.valueOf(r.getProperty(TIMESTAMP.toString()).toString()));
                                if (visited.isAfter(start) && visited.isBefore(stop)) {
                                    return Evaluation.EXCLUDE_AND_CONTINUE;
                                } else {
                                    return Evaluation.EXCLUDE_AND_PRUNE;
                                }
                            } else if (r.isType(LIVES_IN)) {
                                return Evaluation.INCLUDE_AND_PRUNE;
                            } else {
                                return Evaluation.INCLUDE_AND_PRUNE;
                            }
                        });
                Traverser t = td.traverse(db.findNodes(Label.label(PARK.toString())).stream().collect(Collectors.toList()));
                t.stream()
                        .map(p -> {
                            Object park = p.startNode().getProperty(NAME.toString());
                            Object city = p.endNode().getProperty(NAME.toString());
                            return String.format("%s visited from %s", park, city);
                        })
                        .forEach(System.out::println);
            }
            System.out.println();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    private static void cypherQueries(GraphDatabaseService db) {
        // Express, execute and process results of at least 5 non-trivial Cypher query expressions.
        // Describe meaning of both your graph traversals and Cypher expressions, comment your source file.
        // Use at least once MATCH, OPTIONAL MATCH, RETURN, WITH, WHERE, and ORDER BY (sub)clauses.
        try(Transaction ignored = db.beginTx()) {
            {
                System.out.println(":Find parks, that have never been visited and can be closed:");
                Result r = db.execute(
                        "MATCH (p:"+PARK+")" +
                                " WHERE NOT (p)<-[:"+VISITED+"]-(:"+VISITOR+")" +
                                " RETURN p."+NAME
                );
                r.stream().forEach(next -> System.out.println(next.get("p."+NAME)));
            }
            System.out.println();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }

        try(Transaction ignored = db.beginTx()) {
            {
                System.out.println(":Find parks, that have been visited by Bilbo Baggins or Samwise Gamgee:");
                Result r = db.execute(
                        "MATCH (p:"+PARK+")<-[ts:"+VISITED+"]-(v:"+VISITOR+")" +
                                " WITH p."+NAME+" AS pName, v."+NAME+" AS vName, ts."+TIMESTAMP+ " AS time" +
                                " WHERE vName IN ['Bilbo Baggins', 'Samwise Gamgee']" +
                                " RETURN pName,"+"vName" +
                                " ORDER BY time"
                );
                r.stream().forEach(next -> System.out.println(next.get("pName") + " - " + next.get("vName")));
            }
            System.out.println();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }

        try(Transaction ignored = db.beginTx()) {
            {
                System.out.println(":Find places, where the visitors of Guenzburg came from:");
                Result r = db.execute(
                        "MATCH (g:"+PARK+")<-[:"+VISITED+"]-(v:"+VISITOR+")-[:"+LIVES_IN+"]->(p:"+PLACE+")" +
                                " WHERE g."+NAME+" =~ '.*Deutschland.*'" +
                                " RETURN DISTINCT p."+NAME
                );
                r.stream().forEach(next -> System.out.println(next.get("p."+NAME)));
            }
            System.out.println();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }

        try(Transaction ignored = db.beginTx()) {
            {
                System.out.println(":List whole db in table like manner:");
                Result r = db.execute(
                        "MATCH (g:"+PARK+")" +
                                " OPTIONAL MATCH (g)-[ts:"+VISITED+"]-(v:"+VISITOR+")-[li:"+LIVES_IN+"]-(p:"+PLACE+")" +
                                " RETURN g.name, ts.timestamp, v.name, p.name"
                );
                System.out.println("park - time of visit - visitor - lives in");
                System.out.println("=====");
                r.stream().forEach(n ->
                        System.out.printf("%s - %s - %s - %s\n", n.get("g.name"), n.get("ts.timestamp"), n.get("v.name"), n.get("p.name"))
                );
                System.out.println("=====");
            }
            System.out.println();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }

        try(Transaction tx = db.beginTx()) {
            {
                // !!! please note this removes data from the db, you need to remove the db folder to reimport the original data again !!!
                // this can be done e.g. by running 'mvn clean package'
                System.out.println(":Remove all parks visited from Shire:");
                Result r = db.execute(
                        "MATCH (g:"+PARK+")<-[v:"+VISITED+"]-(:"+VISITOR+")-[:"+LIVES_IN+"]->(p:"+PLACE+")" +
                                " WHERE p."+NAME+ " = 'Shire'" +
                                " WITH g, v, g.name AS gName" +
                                " DELETE g, v" +
                                " RETURN gName"
                );
                r.stream().forEach(next -> System.out.println(next.get("gName") + " removed"));
            }

            tx.success();
            System.out.println();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
}
