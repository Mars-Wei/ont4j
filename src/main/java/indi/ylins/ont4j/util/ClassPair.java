package indi.ylins.ont4j.util;

import org.neo4j.graphdb.Node;
import org.semanticweb.owlapi.model.OWLClass;

/**
 * @author Yue Lin
 * @since 2018-06-30
 */
public class ClassPair {

    private OWLClass className;
    private Node classNode;
    public ClassPair(OWLClass className, Node classNode) {
        this.className = className;
        this.classNode = classNode;
    }

    public OWLClass getClassName() {
        return className;
    }

    public Node getClassNode() {
        return classNode;
    }
}
