package models.core.serverModels.document;

import java.util.ArrayList;
import java.util.Set;

import models.ontology.OntologyManager;

import org.semanticweb.owlapi.model.OWLClass;

public class DocumentType {

    public static ArrayList<String> getDocumentTypes(){
        ArrayList<String> res = new ArrayList<String>();
        Set<OWLClass> classes = OntologyManager.ontology.getClassesInSignature();
        
        for(OWLClass classy:classes){
            String fragment = classy.getIRI().getFragment();
            if(fragment.contains("Document") && !fragment.equals("Document")){
                res.add(fragment.replaceAll("(.)([A-Z])", "$1 $2"));
            }
        }
        
        return res;
    }
    
}
