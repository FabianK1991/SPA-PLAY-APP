package models.core.servers;

import java.util.ArrayList;

import models.core.serverModels.businessObject.BusinessObjectInstance;
import models.core.serverModels.document.Document;
import models.ontology.OntologyHandler;
import play.mvc.Http.MultipartFormData.FilePart;

public class DocumentServer {
	
    public static ArrayList<Document> getRelatedDocuments(BusinessObjectInstance businessObjectInstance) {
		ArrayList<Document> res = new ArrayList<Document>();
		
		
		return res;
	}
	
	public static void addRelatedDocument(BusinessObjectInstance businessObjectInstance, FilePart file, String fileName, String docType) {
		docType = docType.replace(" ", "");
	    OntologyHandler.addFlowForOntology(businessObjectInstance, new Document(file, fileName), docType);
	}
	
	public static ArrayList<Document> getAllDocuments() {
		return OntologyHandler.getAllDocuments();
	}
}
