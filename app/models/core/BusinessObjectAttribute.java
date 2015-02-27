package models.core;

import controllers.Application;

public class BusinessObjectAttribute {
	private String id;
	
	/*
	 * TODO
	 * Instantiates a BusinessObjectAttribute
	 * All available BusinessObjectAttributes can be found in database "attributes" (columns: id, name)
	 */
	public BusinessObjectAttribute(String id) {
		this.id = id;
	}
	
	public String getId(){
		return this.id;
	}
	
	/*
	 * TODO
	 * Returns the name of the BusinessObjectAttribute
	 * 
	 */
	public String getName() {
		return Application.sss.getAttributeName(this.id);
	}
}
