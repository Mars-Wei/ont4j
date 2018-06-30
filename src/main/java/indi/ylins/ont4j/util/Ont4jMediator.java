package indi.ylins.ont4j.util;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.parameters.Imports;
import org.semanticweb.owlapi.reasoner.OWLReasoner;

import java.util.stream.Stream;

/**
 * @author Yue Lin
 * @since 2018-06-30
 */
public class Ont4jMediator {

    private static OWLOntology ontology;
    private static OWLReasoner reasoner;
    private static GraphDatabaseService graphDb;

    public static void init() throws OWLOntologyCreationException {
        ontology = Ontology.load();
        reasoner =Ontology.createReasoner();
        graphDb = Neo4j.connect();
        Neo4j.setConstraint();
    }

    public static void transfer() {
        try (Transaction tx = graphDb.beginTx()) {
            Node thingNode = Neo4j.getOrCreateNodeWithUniqueFactory("origin", "owl:Thing");
            Stream<OWLClass> classes = ontology.classesInSignature(Imports.INCLUDED);
            Stream<Ont4jHandler.Relation> classNodes = classes.map(c -> Ont4jHandler.handleRelation(reasoner, c, thingNode));
            classNodes.forEach(n -> Ont4jHandler.handleIndividual(ontology, reasoner, n.getClassName(), n.getClassNode()));
            tx.success();
        }
    }

    public static void close() {
        Neo4j.shutdown();
    }

}
