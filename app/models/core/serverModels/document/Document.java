package models.core.serverModels.document;

import java.io.File;
import java.util.ArrayList;

import models.core.serverModels.businessObject.BusinessObject;
import models.core.serverModels.businessObject.BusinessObjectInstance;

public class Document {
	/*
	 * TODO
	 * Returns the name of the document
	 */
	public String getName() {
		return "";
	}
	
	/*
	 * TODO
	 * Returns a description of the document
	 */
	public String getContent() {
		return "";
	}
	
	/*
	 * TODO
	 * Returns a description of the document
	 */
	public ArrayList<Image> getImages() {
		return null;
	}
	
	/*
	 * TODO
	 * Returns the locally stored document file
	 */
	public File getFile() {
		return null;
	}
	
	/*
	 * TODO
	 * Returns the business obejct instance
	 */
	public BusinessObjectInstance getBusinessObjectInstance() {
		return null;
	}
	
	/*
	 * TODO
	 * Returns the business obejct
	 */
	public BusinessObject getBusinessObject() {
		return null;
	}
}
