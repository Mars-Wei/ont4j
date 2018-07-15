package indi.ylins.ont4j.util;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.parameters.Imports;
import org.semanticweb.owlapi.reasoner.OWLReasoner;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.stream.Stream;

/**
 * @author Yue Lin
 * @since 2018-06-30
 */
public class Mediator {

    private static String owlPath;
    private static File dbDir;
    private static OWLOntology ontology;
    private static OWLReasoner reasoner;
    private static GraphDatabaseService graphDb;

    public static void getEnv(String propertyFile) throws IOException {
        InputStream in = Mediator.class.getClassLoader().getResourceAsStream(propertyFile);
        Properties properties = new Properties();
        properties.load(in);
        owlPath = properties.getProperty("ontPath");
        dbDir = new File(properties.getProperty("dbDir"));
    }

    public static void init() throws OWLOntologyCreationException {
        ontology = Ontology.load(owlPath);
        reasoner = Ontology.createReasoner();
        graphDb = Neo4j.connect(dbDir);
        Neo4j.setConstraint();
    }

    public static void transfer() {
        try (Transaction tx = graphDb.beginTx()) {
            Node thingNode = Neo4j.getOrCreateNodeWithUniqueFactory("origin", "owl:Thing");
            Stream<OWLClass> classes = ontology.classesInSignature(Imports.INCLUDED);
            Stream<ClassPair> classPairs = classes.map(c -> Handler.handleRelation(reasoner, c, thingNode));
            classPairs.forEach(p -> Handler.handleIndividual(ontology, reasoner, p.getClassName(), p.getClassNode()));
            tx.success();
        }
    }

    public static void close() {
        Neo4j.shutdown();
    }

}
