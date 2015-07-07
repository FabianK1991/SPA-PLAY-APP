package models.core.serverModels.businessObject;

import java.util.ArrayList;
import java.util.List;

import models.core.util.parsing.ProcessParser;
import controllers.Application;
import models.core.serverModels.businessObject.BusinessObjectInstance;
import java.util.HashMap;
import play.Logger;

public class BusinessObject {
	private String id;
	private String SAPId;
	
	private String action;
	private String min;
	private String max;
	
	private String name;
	
	private String[] neededAttributes;
	
	public static List<BusinessObjectInstance> getAll(String bo) throws Exception{
		List<String> properties = Application.sss.getBusinessObjectProperties(bo);
		List<String> properties_names = Application.sss.getBusinessObjectPropertiesNames(bo);
		
		ArrayList<List<String>> valueList = new ArrayList<List<String>>();
		
		for(int i=0;i<properties.size();i++){
			valueList.add(Application.sss.getBusinessObjectProperty(properties.get(i)));
		}
		
		// now create the processinstances
		List<BusinessObjectInstance> pis = new ArrayList<BusinessObjectInstance>();
		
		// loop over value entries
		for(int i=0;i<valueList.get(0).size();i++){
			HashMap<String,String> values = new HashMap<String,String>();
			
			// loop over properties
			for(int j=0;j<valueList.size();j++){
				values.put(properties_names.get(j), valueList.get(j).get(i));
			}
			
			for (String name: values.keySet()){
	            String key =name.toString();
	            String value = values.get(name).toString();  
	            Logger.info(key + " " + value);  
			} 
			
			pis.add(new BusinessObjectInstance(bo, properties_names.get(0), values));
		}
		
		return pis;
	}
	
	public static List<String> getBusinessObjectProperties(String bo){
		return Application.sss.getBusinessObjectPropertiesNames(bo);
	}
	
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
		List<String> attr = Application.sss.getBusinessObjectProperties(this.getSAPId());
		
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
	
	public String getRawId() {
		return this.getId().replace(ProcessParser.nsm, "");
	}
}
