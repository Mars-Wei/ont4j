package indi.ylins.ont4j;

import indi.ylins.ont4j.util.Mediator;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

import java.io.IOException;

/**
 * @author Yue Lin
 * @since 2018-06-30
 */
public class Demo {

    public static void main(String[] args) throws OWLOntologyCreationException, IOException {
        Mediator mediator = new Mediator();
        mediator.getEnv("application.properties");
        mediator.init();
        mediator.transfer();
        mediator.close();
    }
}
