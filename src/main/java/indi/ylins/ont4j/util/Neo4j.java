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

    private static File dbDir = new File(PathEnum.DB_DIR.getPath());
    private static GraphDatabaseService db;

    public static GraphDatabaseService connect() {
        db = new GraphDatabaseFactory().newEmbeddedDatabase(dbDir);
        registerShutdownHook(db);
        return db;
    }

    public static void setConstraint() {
        try (Transaction tx = db.beginTx()) {
            if (db.schema().getConstraints(Label.label("origin")) == null) {
                db.schema().constraintFor(Label.label("origin")).assertPropertyIsUnique("name").create();
                tx.success();
            }
        }
    }

    public static Node getOrCreateNodeWithUniqueFactory(String nodeKey, String nodeName) {
        Node result;
        ResourceIterator<Node> resultIterator;
        try (Transaction tx = db.beginTx()) {
            String queryString = "MERGE (n:" + nodeKey + " {name: $name}) RETURN n";
            Map<String, Object> parameters = new HashMap<>();
            parameters.put("name", nodeName);
            resultIterator = db.execute(queryString, parameters).columnAs( "n" );
            result = resultIterator.next();
            tx.success();
            return result;
        }
    }

    private static void registerShutdownHook(final GraphDatabaseService graphDb) {
        Runtime.getRuntime().addShutdownHook(new Thread(graphDb::shutdown));
    }

    public static void shutdown() {
        db.shutdown();
    }

}
