package indi.ylins.ont4j.util;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;

/**
 * @author Yue Lin
 * @since 2018-06-30
 */
public class Ontology {

    private OWLOntologyManager manager;

    public OWLOntology load(String path) {
        this.manager = OWLManager.createOWLOntologyManager();
        IRI iri = IRI.create("file:" + path);
        OWLOntology ontology = null;
        try {
            ontology = this.manager.loadOntologyFromOntologyDocument(iri);
        } catch (OWLOntologyCreationException e) {
            System.out.println("failed to create Ontology model, please checkout the path of .owl file.");
        }
        return ontology;
    }
}
