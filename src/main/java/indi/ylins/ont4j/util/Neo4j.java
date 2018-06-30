package indi.ylins.ont4j.util;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.ResourceIterator;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Yue Lin
 * @since 2018-06-30
 */
public class Neo4j {

    private GraphDatabaseService db;

    public void connect(File dbDir) {
        this.db = new GraphDatabaseFactory().newEmbeddedDatabase(dbDir);
        registerShutdownHook(this.db);
    }

    public void shutdown() {
        this.db.shutdown();
    }

    public Node getOrCreateNodeWithUniqueFactory(String concept) {
        Node result;
        ResourceIterator<Node> resultIterator;
        try (Transaction tx = this.db.beginTx()) {
            String queryString = "MERGE (n:Concept {name: $name}) RETURN n";
            Map<String, Object> parameters = new HashMap<>();
            parameters.put("name", concept);
            resultIterator = this.db.execute(queryString, parameters).columnAs( "n" );
            result = resultIterator.next();
            tx.success();
            return result;
        }
    }

    private void setConstraint() {
        try (Transaction tx = this.db.beginTx()) {
            this.db.schema().constraintFor(Label.label( "concept" )).assertPropertyIsUnique( "name" ).create();
            tx.success();
        }
    }

    private static void registerShutdownHook(final GraphDatabaseService graphDb) {
        Runtime.getRuntime().addShutdownHook(new Thread(graphDb::shutdown));
    }
}
