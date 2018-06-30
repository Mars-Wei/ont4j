package indi.ylins.ont4j.util;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.reasoner.BufferingMode;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.SimpleConfiguration;
import org.semanticweb.owlapi.reasoner.structural.StructuralReasoner;

/**
 * @author Yue Lin
 * @since 2018-06-30
 */
public class Ontology {

    private static String path = PathEnum.ONT_PATH.getPath();
    private static OWLOntology ontology;

    public static OWLOntology load() throws OWLOntologyCreationException {
        OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
        IRI iri = IRI.create("file:" + path);
        ontology = manager.loadOntologyFromOntologyDocument(iri);
        return ontology;
    }

    public static OWLReasoner createReasoner() {
        return new StructuralReasoner(ontology, new SimpleConfiguration(), BufferingMode.BUFFERING);
    }

}
