package models.core;

import java.util.ArrayList;
import java.util.List;

import controllers.Application;

public class BusinessObject {
	private String id;
	private String action;
	private int min;
	private int max;
	
	private List<String> neededAttributes;
	
	public BusinessObject(String id){
		this.id = id;
	}
	
	public BusinessObject(String id, String action, int min, int max){
		this.id = id;
		this.action = action;
		this.min = min;
		this.max = max;
	}
	
	public BusinessObject(String id, String action, int min, int max, List<String> neededAttributes){
		this.id = id;
		this.action = action;
		this.min = min;
		this.max = max;
		this.setNeededAttributes(neededAttributes);
	}
	
	
	/*
	 * TODO
	 * Returns an ordered List of BusinessObjectAttributes that are allowed for this BusinessObject
	 * A List of allowed BusinessObjectAttributes for every BusinessObject can be retrieved from
	 * database "business_object_attributes" (columns: id, business_object, attribute, order)
	 * => List should be ordered ASCending by column order
	 */
	public List<BusinessObjectAttribute> getAttributes() {
		List<String> attr = Application.sss.getBusinessObjectAttributes(Integer.parseInt(this.id));
		
		ArrayList<BusinessObjectAttribute> resultList = new ArrayList<BusinessObjectAttribute>();
		
		for (String a : attr) {
			BusinessObjectAttribute boa = new BusinessObjectAttribute(a);
			resultList.add(boa);
		}
		
		return resultList;
	}
	/*
	 * Returns the action for which the business object is be used
	 */
	public String getAction(){
		return this.action;
	}
	
	/*
	 * TODO
	 * Returns the minimum number of BusinessObjects that need to be defined in a ActivityInstance
	 */
	public int getMinQuantity() {
		return this.min;
	}
	
	/*
	 * TODO
	 * Returns the maximum number of BusinessObjects that can be defined in a ActivityInstance
	 */
	public int getMaxQuantity() {
		return this.max;
	}

	public List<String> getNeededAttributes() {
		return neededAttributes;
	}

	public void setNeededAttributes(List<String> neededAttributes) {
		this.neededAttributes = neededAttributes;
	}
	
	public String getId(){
		return this.id;
	}
}
