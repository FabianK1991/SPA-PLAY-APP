package models.ontology.util;

import java.util.HashSet;
import java.util.Set;

import models.ontology.OntologyManager;
import models.ontology.Settings;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLEquivalentClassesAxiom;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectOneOf;
import org.semanticweb.owlapi.model.OWLObjectProperty;

import uk.ac.manchester.cs.owl.owlapi.OWLObjectOneOfImpl;

public class OntologyHelper {
    
    public static OWLNamedIndividual createIndividual(String name){
        return OntologyManager.factory.getOWLNamedIndividual(IRI.create(Settings.NS + name));
    }
    
    public static OWLClass getClassForName(String name){
        return OntologyManager.factory.getOWLClass(IRI.create(Settings.NS + name));
    }
    
    public static OWLDataProperty getDataPropertyForName(String name){
        return OntologyManager.factory.getOWLDataProperty(IRI.create(Settings.NS + name));
    }
    
    public static Set<OWLAxiom> addClassAssertion(OWLClass classy, OWLNamedIndividual individual){
        OWLDataFactory factory = OntologyManager.factory;
        HashSet<OWLAxiom> set = new HashSet<OWLAxiom>();
        
        if(OntologyManager.ontology.containsIndividualInSignature(individual.getIRI())){
           return set;
        }
        
        set.add(factory.getOWLClassAssertionAxiom(classy, individual));
        Set<OWLEquivalentClassesAxiom> eqAxiomSet = OntologyManager.ontology.getEquivalentClassesAxioms(factory.getOWLThing()); 
        
        if(eqAxiomSet.iterator().hasNext() == false){
            Set<OWLNamedIndividual> individuals = new HashSet<OWLNamedIndividual>();
            individuals.add(individual);
            
            OWLObjectOneOf oneOf = new OWLObjectOneOfImpl(individuals);
            OWLClassExpression expres = (OWLClassExpression) oneOf;
            
            set.add(factory.getOWLEquivalentClassesAxiom(factory.getOWLThing(), expres));
        }
        else{
           OWLEquivalentClassesAxiom eqAxiom = eqAxiomSet.iterator().next();
           Set<OWLNamedIndividual> individuals = eqAxiom.getIndividualsInSignature();
        
            individuals.add(individual);
            
            OWLObjectOneOf oneOf = new OWLObjectOneOfImpl(individuals);
            OWLClassExpression expres = (OWLClassExpression) oneOf;
            
            set.add(factory.getOWLEquivalentClassesAxiom(factory.getOWLThing(), expres));
            
//            OntologyManager.manager.removeAxiom(OntologyManager.ontology, eqAxiom); 
        }
  
        return set;
    }
    
    public static Set<OWLAxiom> addDataPropertyValue(OWLNamedIndividual ind, OWLDataProperty dataProp, String value){
        HashSet<OWLAxiom> set = new HashSet<OWLAxiom>();
        OWLDataFactory factory = OntologyManager.factory;        
        set.add(factory.getOWLDataPropertyAssertionAxiom(dataProp, ind, value));
        return set;
    }
    
    public static Set<OWLAxiom> addObjectPropertyAssertion(OWLNamedIndividual boInd, OWLClass boClass, OWLNamedIndividual docInd){
        HashSet<OWLAxiom> set = new HashSet<OWLAxiom>();
        OWLDataFactory factory = OntologyManager.factory;
        
        String sapId = boClass.getIRI().getFragment();
        
        OWLObjectProperty relatedToBO = factory.getOWLObjectProperty(IRI.create(Settings.NS + "relatedTo" + sapId));
        OWLObjectProperty relatedToDoc =  factory.getOWLObjectProperty(IRI.create(Settings.NS + "relatedTo" + sapId + "_Document"));
        
        set.add(factory.getOWLObjectPropertyAssertionAxiom(relatedToBO, docInd, boInd));
        set.add(factory.getOWLObjectPropertyAssertionAxiom(relatedToDoc, boInd, docInd));
        
        return set;
    }
    
    
    
}
