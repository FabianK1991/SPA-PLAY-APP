package models.core.serverModels.businessObject;

import controllers.Application;

public class BusinessObjectProperty {
	private String id;
	private String name;
	
	/*
	 * Instantiates a BusinessObjectAttribute
	 * All available BusinessObjectAttributes can be found in database "attributes" (columns: id, name)
	 */
	public BusinessObjectProperty(String id) {
		this.name = name;
	}
	
	public String getId(){
		return null;
	}
	
	/*
	 * Returns the name of the BusinessObjectAttribute
	 * 
	 */
	public String getName() {
		/*if(this.name == null){
			this.name = Application.sss.getAttributeName(Integer.parseInt(this.id));
		}*/
		
		return this.name;
	}
}
