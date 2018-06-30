package indi.ylins.ont4j.util;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;

import java.io.File;

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

    private void setConstraint() {
        try (Transaction tx = this.db.beginTx()) {
            db.schema().constraintFor(Label.label( "concept" )).assertPropertyIsUnique( "name" ).create();
            tx.success();
        }
    }

    private static void registerShutdownHook(final GraphDatabaseService graphDb) {
        Runtime.getRuntime().addShutdownHook(new Thread(graphDb::shutdown));
    }
}
