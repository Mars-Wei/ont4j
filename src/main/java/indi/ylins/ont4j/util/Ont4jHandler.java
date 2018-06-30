package indi.ylins.ont4j.util;

import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.RelationshipType;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.reasoner.NodeSet;
import org.semanticweb.owlapi.reasoner.OWLReasoner;

import java.util.stream.Stream;

/**
 * @author Yue Lin
 * @since 2018-06-30
 */
public class Ont4jHandler {

    public static Relation handleRelation(OWLReasoner reasoner, OWLClass owlClass, Node thingNode) {
        String classString = owlClass.toString();
        if (classString.contains("#"))
            classString = classString.substring(classString.indexOf("#") + 1, classString.lastIndexOf(">"));
        Node classNode = Neo4j.getOrCreateNodeWithUniqueFactory("concept", classString);
        NodeSet<OWLClass> superClasses = reasoner.getSuperClasses(owlClass, true);
        if (superClasses.isEmpty()) {
            classNode.createRelationshipTo(thingNode, RelationshipType.withName("isA"));
        }
        else {
            for (org.semanticweb.owlapi.reasoner.Node<OWLClass> parentOWLNode : superClasses) {
                OWLClassExpression parent = parentOWLNode.getRepresentativeElement();
                String parentString = parent.toString();
                if (parentString.contains("#"))
                    parentString = parentString.substring(parentString.indexOf("#") + 1, parentString.lastIndexOf(">"));
                if (parentString.equals("owl:Thing"))
                    classNode.createRelationshipTo(thingNode, RelationshipType.withName("isA"));
                else {
                    Node parentNode = Neo4j.getOrCreateNodeWithUniqueFactory("concept", parentString);
                    classNode.createRelationshipTo(parentNode, RelationshipType.withName("isA"));
                }
            }
        }
        return new Relation(owlClass, classNode);
    }

    public static void handleIndividual(OWLOntology ontology, OWLReasoner reasoner, OWLClass owlClass, Node classNode) {
        for (org.semanticweb.owlapi.reasoner.Node<OWLNamedIndividual> individual : reasoner.getInstances(owlClass, true)) {
            OWLNamedIndividual indi = individual.getRepresentativeElement();
            String indiString = indi.toString();
            if (indiString.contains("#"))
                indiString = indiString.substring(indiString.indexOf("#") + 1, indiString.lastIndexOf(">"));
            Node indiNode = Neo4j.getOrCreateNodeWithUniqueFactory("individual", indiString);
            indiNode.createRelationshipTo(classNode, RelationshipType.withName("isA"));
            Stream<OWLObjectProperty> objectProperty = ontology.objectPropertiesInSignature();
            objectProperty.forEach(o -> handleObjectProperty(reasoner, indi, indiNode, o));
            Stream<OWLDataProperty> dataProperty = ontology.dataPropertiesInSignature();
            dataProperty.forEach(d -> handleDataProperty(reasoner, indi, indiNode, d));
        }
    }

    private static void handleObjectProperty(OWLReasoner reasoner, OWLNamedIndividual indi, Node indiNode, OWLObjectProperty objectProperty) {
        for (org.semanticweb.owlapi.reasoner.Node<OWLNamedIndividual> object: reasoner.getObjectPropertyValues(indi, objectProperty)) {
            String reltype = objectProperty.toString();
            reltype = reltype.substring(reltype.indexOf("#") + 1, reltype.lastIndexOf(">"));
            String s = object.getRepresentativeElement().toString();
            s = s.substring(s.indexOf("#") + 1, s.lastIndexOf(">"));
            Node objectNode = Neo4j.getOrCreateNodeWithUniqueFactory("individual", s);
            indiNode.createRelationshipTo(objectNode, RelationshipType.withName(reltype));
        }
    }

    private static void handleDataProperty(OWLReasoner reasoner, OWLNamedIndividual indi, Node indiNode, OWLDataProperty dataProperty) {
        for (OWLLiteral object: reasoner.getDataPropertyValues(indi, dataProperty.asOWLDataProperty())) {
            String reltype = dataProperty.asOWLDataProperty().toString();
            reltype = reltype.substring(reltype.indexOf("#") + 1, reltype.lastIndexOf(">"));
            String s = object.toString();
            indiNode.setProperty(reltype, s);
        }
    }

    static class Relation {
        OWLClass className;
        Node classNode;
        Relation(OWLClass className, Node classNode) {
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
}
