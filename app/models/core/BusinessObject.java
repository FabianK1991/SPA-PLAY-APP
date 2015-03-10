package models.core;

import java.util.ArrayList;
import java.util.List;

import controllers.Application;

public class BusinessObject {
	private String id;
	private String SAPId;
	
	private String action;
	private String min;
	private String max;
	
	private String name;
	
	private String[] neededAttributes;
	
	public BusinessObject(String id){
		this.id = id;
	}
	
	public BusinessObject(String id, String action, String min, String max){
		this.id = id;
		this.action = action;
		this.min = min;
		this.max = max;
	}
	
	public BusinessObject(String id, String action, String min, String max, String[] neededAttributes){
		this.id = id;
		this.action = action;
		this.min = min;
		this.max = max;
		this.neededAttributes = neededAttributes;
	}
	
	public void setName(String name){
		this.name = name;
	}
	
	public void setSAPId(String SAPId){
		this.SAPId = SAPId;
	}
	
	public String getSAPId(){
		return SAPId;
	}
	
	/*
	 * TODO
	 * Returns an ordered List of BusinessObjectAttributes that are allowed for this BusinessObject
	 * A List of allowed BusinessObjectAttributes for every BusinessObject can be retrieved from
	 * database "business_object_attributes" (columns: id, business_object, attribute, order)
	 * => List should be ordered ASCending by column order
	 */
	public List<BusinessObjectAttribute> getAttributes() {
		List<String> attr = Application.sss.getBusinessObjectAttributes(this.getSAPId());
		
		ArrayList<BusinessObjectAttribute> resultList = new ArrayList<BusinessObjectAttribute>();
		
		for (String a : attr) {
			BusinessObjectAttribute boa = new BusinessObjectAttribute(a);
			resultList.add(boa);
		}
		
		return resultList;
	}
	
	public void setAction(String action){
		this.action = action;
	}
	
	public void setMin(String min){
		this.min = min;
	}
	
	public void setMax(String max){
		this.max = max;
	}
	
	public String getMin(){
		return this.min;
	}
	
	public String getMax(){
		return this.max;
	}
	
	public String getName(){
		return this.name;
	}
	
	/*
	 * Returns the action for which the business object is be used
	 */
	public String getAction(){
		return this.action;
	}
	
	/*
	 * Returns the minimum number of BusinessObjects that need to be defined in a ActivityInstance
	 */
	public String getMinQuantity() {
		return this.min;
	}
	
	/*
	 * Returns the maximum number of BusinessObjects that can be defined in a ActivityInstance
	 */
	public String getMaxQuantity() {
		return this.max;
	}
	
	/*
	 * TODO
	 * Better: public List<BusinessObjectAttribute> getNeededAttributes() {}
	 */
	public List<BusinessObjectAttribute> getNeededAttributesList() {
		List<BusinessObjectAttribute> attr = this.getAttributes();
		List<BusinessObjectAttribute> re = new ArrayList<BusinessObjectAttribute>();
		
		for (String att : neededAttributes) {
			for(BusinessObjectAttribute ba : attr){
				if( att.equals(ba.getName()) ){
					re.add(ba);
				}
			}
		}
		
		return re;
	}
	
	public String[] getNeededAttributes() {
		return neededAttributes;
	}

	public void setNeededAttributes(String[] neededAttributes) {
		this.neededAttributes = neededAttributes;
	}
	
	public String getId(){
		return this.id;
	}
}
