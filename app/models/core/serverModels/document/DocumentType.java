package models.core.serverModels.document;

import java.util.ArrayList;
import java.util.Set;

import models.ontology.OntologyManager;
import models.ontology.util.OntologyHelper;

import org.semanticweb.owlapi.model.OWLClassExpression;

public class DocumentType {

    public static ArrayList<String> getDocumentTypes(){
        ArrayList<String> res = new ArrayList<String>();
        Set<OWLClassExpression> classes = OntologyHelper.getClassForName("Document").getSubClasses(OntologyManager.ontology);
        
        for(OWLClassExpression classy : classes){
            for(OWLClassExpression subClass : classy.asOWLClass().getSubClasses(OntologyManager.ontology)){
                String fragment = subClass.asOWLClass().getIRI().getFragment();
                res.add(fragment.replaceAll("(.)([A-Z])", "$1 $2"));
            }
            
        }
        
        return res;
    }
    
}
