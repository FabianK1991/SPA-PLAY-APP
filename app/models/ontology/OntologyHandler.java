package models.ontology;

import java.util.ArrayList;
import java.util.HashSet;

import models.core.serverModels.document.Document;
import models.ontology.util.OntologyHelper;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;

import play.Logger;
import play.utils.UriEncoding;

public class OntologyHandler {

    public static void addFlowForOntology(String boiId, String boId, Document doc, String docType){
        OWLOntologyManager manager = OntologyManager.manager;
        boiId = UriEncoding.encodePathSegment(boiId, "UTF-8");
        HashSet<OWLAxiom> axioms = new HashSet<OWLAxiom>();
        OWLClass boOWL = OntologyHelper.getClassForName(boId);
        OWLNamedIndividual boiOWL = OntologyHelper.createIndividual(boiId);
        OWLClass docClassOWL = OntologyHelper.getClassForName(docType);
        OWLNamedIndividual docOWL = OntologyHelper.createIndividual(doc.getId());
        
        for(String image:doc.getImages()){
            axioms.addAll(OntologyHelper.addDataPropertyValue(docOWL, OntologyHelper.getDataPropertyForName("image"), image));
        }
        axioms.addAll(OntologyHelper.addDataPropertyValue(docOWL, OntologyHelper.getDataPropertyForName("description"), doc.getDescription()));
        axioms.addAll(OntologyHelper.addDataPropertyValue(docOWL, OntologyHelper.getDataPropertyForName("filepath"), doc.getFilepath()));
        axioms.addAll(OntologyHelper.addDataPropertyValue(docOWL, OntologyHelper.getDataPropertyForName("name"), doc.getName()));
        
        axioms.addAll(OntologyHelper.addClassAssertion(docClassOWL, docOWL));
        
        axioms.addAll(OntologyHelper.addClassAssertion(boOWL, boiOWL));
        
        axioms.addAll(OntologyHelper.addObjectPropertyAssertion(boiOWL, boOWL, docOWL));
        
        manager.applyChanges(manager.addAxioms(OntologyManager.ontology, axioms));   
        
        try {
            manager.saveOntology(OntologyManager.ontology);
        } catch (OWLOntologyStorageException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    public static ArrayList<Document> getRelatedDocuments(String boiId){
        ArrayList<Document> res = new ArrayList<Document>();
        OWLOntology ontology = OntologyManager.ontology;
        boiId = UriEncoding.encodePathSegment(boiId, "UTF-8");
        OWLNamedIndividual boiOWL = OntologyHelper.createIndividual(boiId);
        
        if(ontology.getObjectPropertyAssertionAxioms(boiOWL) == null){
            return res;
        }
        
        for(OWLObjectPropertyAssertionAxiom axiom : ontology.getObjectPropertyAssertionAxioms(boiOWL)){
            OWLNamedIndividual docOWL = (OWLNamedIndividual)axiom.getObject();
            Document doc = new Document(docOWL, docOWL.getDataPropertyValues(ontology));
            res.add(doc);
        }
        
        return res;
    }

	public static ArrayList<Document> getAllDocuments(){
        ArrayList<Document> res = new ArrayList<Document>();
        OWLOntology ontology = OntologyManager.ontology;
        OWLDataFactory factory = OntologyManager.factory;
        
        OWLClass document = factory.getOWLClass(IRI.create(Settings.NS + "Document"));
        for(OWLClassExpression classy:document.getSubClasses(ontology)){
            if(!classy.isAnonymous()){
                for(OWLClassExpression subClass:classy.asOWLClass().getSubClasses(ontology)){
                    Logger.info(((OWLClass)subClass).getIRI().toString());
                    
                    for(OWLIndividual ind1:subClass.asOWLClass().getIndividuals(ontology)){
                        OWLNamedIndividual ind = (OWLNamedIndividual)ind1;
                        Document doc = new Document();
                        ArrayList<String> images = new ArrayList<String>();
                        for(OWLDataProperty dp:ontology.getDataPropertiesInSignature()){
                            if( dp.getDomains(ontology).iterator().hasNext() && !dp.getDomains(ontology).iterator().next().asOWLClass().getIRI().getFragment().equals("Document")){
                                continue;
                            }
                            
                            String frag = dp.getIRI().getFragment();
                            Logger.info(frag);
                            if(frag.equals("name") && ind.getDataPropertyValues(dp, ontology).iterator().hasNext()){
                                doc.setName(ind.getDataPropertyValues(dp, ontology).iterator().next().getLiteral());
                            }
                            else if(frag.equals("description") && ind.getDataPropertyValues(dp, ontology).iterator().hasNext()){
                                doc.setDescription(ind.getDataPropertyValues(dp, ontology).iterator().next().getLiteral());
                            }
                            else if(frag.equals("id") && ind.getDataPropertyValues(dp, ontology).iterator().hasNext()){
                                doc.setId(ind.getDataPropertyValues(dp, ontology).iterator().next().getLiteral());
                            }
                            else if(frag.equals("image") && ind.getDataPropertyValues(dp, ontology).iterator().hasNext()){
                                images.add(ind.getDataPropertyValues(dp, ontology).iterator().next().getLiteral());
                            }
                            else if(frag.equals("filepath") && ind.getDataPropertyValues(dp, ontology).iterator().hasNext()){
                                doc.setFilepath(ind.getDataPropertyValues(dp, ontology).iterator().next().getLiteral());
                            }
                        }
                        doc.setImages(images);
                        res.add(doc);
                    }
                }
            }
        }
        
        return res;
    }
    
}
