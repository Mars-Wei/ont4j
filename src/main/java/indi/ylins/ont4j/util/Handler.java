package indi.ylins.ont4j.util;

import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.RelationshipType;
import org.semanticweb.owlapi.model.OWLClass;
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
public class Handler {

    public static ClassPair handleRelation(OWLReasoner reasoner, OWLClass owlClass, Node thingNode) {
        String classString = getObjectName(owlClass);
        Node classNode;
        if (classString.equals("owl:Thing"))
            return new ClassPair(owlClass, thingNode);
        else
            classNode = Neo4j.getOrCreateNodeWithUniqueFactory("concept", classString);
        NodeSet<OWLClass> superClasses = reasoner.getSuperClasses(owlClass, true);
        if (superClasses.isEmpty())
            classNode.createRelationshipTo(thingNode, RelationshipType.withName("isA"));
        else {
            for (org.semanticweb.owlapi.reasoner.Node<OWLClass> parentOWLNode : superClasses) {
                OWLClass parent = parentOWLNode.getRepresentativeElement();
                String parentString = getObjectName(parent);
                if (parentString.equals("owl:Thing"))
                        classNode.createRelationshipTo(thingNode, RelationshipType.withName("isA"));
                else {
                    Node parentNode = Neo4j.getOrCreateNodeWithUniqueFactory("concept", parentString);
                    classNode.createRelationshipTo(parentNode, RelationshipType.withName("isA"));
                }
            }
        }
        return new ClassPair(owlClass, classNode);
    }

    public static void handleIndividual(OWLOntology ontology, OWLReasoner reasoner, OWLClass owlClass, Node classNode) {
        for (org.semanticweb.owlapi.reasoner.Node<OWLNamedIndividual> individual : reasoner.getInstances(owlClass, true)) {
            OWLNamedIndividual indi = individual.getRepresentativeElement();
            String indiString = getObjectName(indi);
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
            String relType = getObjectName(objectProperty);
            String s = getObjectName(object.getRepresentativeElement());
            Node objectNode = Neo4j.getOrCreateNodeWithUniqueFactory("individual", s);
            indiNode.createRelationshipTo(objectNode, RelationshipType.withName(relType));
        }
    }

    private static void handleDataProperty(OWLReasoner reasoner, OWLNamedIndividual indi, Node indiNode, OWLDataProperty dataProperty) {
        for (OWLLiteral object: reasoner.getDataPropertyValues(indi, dataProperty.asOWLDataProperty())) {
            String relType = getObjectName(dataProperty.asOWLDataProperty());
            String s = object.toString();
            indiNode.setProperty(relType, s);
        }
    }

    private static String getObjectName(Object o) {
        String objectName = o.toString();
        if (objectName.contains("#"))
            objectName = objectName.substring(objectName.indexOf("#") + 1, objectName.lastIndexOf(">"));
        return objectName;
    }

}
