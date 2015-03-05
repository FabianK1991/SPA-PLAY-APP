package models.core;

import controllers.Application;

public class BusinessObjectAttribute {
	private String id;
	private String name;
	
	/*
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
	 * Returns the name of the BusinessObjectAttribute
	 * 
	 */
	public String getName() {
		if(this.name == null){
			this.name = Application.sss.getAttributeName(Integer.parseInt(this.id));
		}
		
		return this.name;
	}
}
