package models.core.servers;

import java.util.ArrayList;

import models.core.serverModels.businessObject.BusinessObjectInstance;
import models.core.serverModels.document.Document;
import models.ontology.OntologyHandler;
import play.mvc.Http.MultipartFormData.FilePart;

public class DocumentServer {
	
    public static ArrayList<Document> getRelatedDocuments(BusinessObjectInstance businessObjectInstance) {
		String boiId = businessObjectInstance.getInstanceId();
		return OntologyHandler.getRelatedDocuments(boiId);
	}
	
	public static void addRelatedDocument(BusinessObjectInstance businessObjectInstance, FilePart file, String fileName, String docType) {
		docType = docType.replace(" ", "");
		String boiId = businessObjectInstance .getInstanceId();
		String boId = businessObjectInstance.getBusinessObject().getSAPId();
	    OntologyHandler.addFlowForOntology(boiId, boId, new Document(file, fileName, docType), docType);
	}
	
	public static ArrayList<Document> getAllDocuments() {
		return OntologyHandler.getAllDocuments();
	}
}
