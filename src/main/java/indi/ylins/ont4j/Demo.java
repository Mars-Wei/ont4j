package indi.ylins.ont4j;

import indi.ylins.ont4j.util.Ont4jMediator;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

/**
 * @author Yue Lin
 * @since 2018-06-30
 */
public class Demo {

    public static void main(String[] args) throws OWLOntologyCreationException {
        Ont4jMediator.init();
        Ont4jMediator.transfer();
        Ont4jMediator.close();
    }
}
