package models.core.serverModels.businessObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Iterator;

import models.core.util.parsing.ProcessParser;
import controllers.Application;
import models.core.serverModels.businessObject.BusinessObjectInstance;

import java.util.HashMap;

import play.Logger;

import java.sql.ResultSet;
import java.sql.SQLException;

public class BusinessObject {
	private String id;
	private String dbId;
	private String SAPId;
	
	private String action;
	private String min;
	private String max;
	
	private String name;
	
	private String[] neededAttributes;
	
	public List<BusinessObjectInstance> getAllInstances() throws Exception{
		String bo = this.getRawId();
		
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
	            String value = "null";  
	            
	            if( values.get(name) != null ){
	            	value = values.get(name).toString(); 
	            }
	            
	            Logger.info(key + " " + value);  
			} 
			
			pis.add(new BusinessObjectInstance(this, bo, properties_names.get(0), values));
		}
		
		return pis;
	}
	
	/*
	 * TODO: Fabi
	 * returns all types of Business Objects
	 */
	public static List<BusinessObject> getAll() {
		Application.db.connect();
		
		String query = "SELECT sap_id, id FROM business_objects";
		
		ArrayList<String> args = new ArrayList<String>();		
		ResultSet rs = Application.db.exec(query, args, true);
		
		try {
			ArrayList<BusinessObject> resultList = new ArrayList<BusinessObject>();
			
			while(rs.next()){
				BusinessObject a = new BusinessObject(rs.getString("id"));
				
				resultList.add(a);
			}
			
			return resultList;
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	public List<String> getPropertyNames(){
		return Application.sss.getBusinessObjectPropertiesNames(this.getDBId());
	}
	
	public List<BusinessObjectProperty> getBusinessObjectProperties(){
		String bo = this.getDBId();
		
		List<String> l = Application.sss.getBusinessObjectPropertiesNames(bo);
		List<BusinessObjectProperty> resultList = new ArrayList<BusinessObjectProperty>();
		
		for(int i=0;i<l.size();i++){
			BusinessObjectProperty bop = new BusinessObjectProperty(l.get(i));
			
			resultList.add(bop);
		}
		
		return resultList;
	}
	
	public BusinessObject(String dbId) throws Exception{
		this.dbId = dbId;
		
		String query = "SELECT sap_id, name FROM business_objects WHERE id = '%s' LIMIT 1";
		
		ArrayList<String> args = new ArrayList<String>();
		args.add(dbId);
		
		ResultSet rs = Application.db.exec(query, args, true);
		
		try {
			if(rs.next()){
				this.SAPId = rs.getString("sap_id");
				this.name = rs.getString("name");
			}
			else {
				throw new Exception("Business Object not found!");
			}
		} catch (SQLException e) {
			throw new Exception("Business Object not found!");
		}
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
	
	public String getDBId(){
		return this.dbId;
	}
	
	/*
	 * TODO
	 * Returns an ordered List of BusinessObjectAttributes that are allowed for this BusinessObject
	 * A List of allowed BusinessObjectAttributes for every BusinessObject can be retrieved from
	 * database "business_object_attributes" (columns: id, business_object, attribute, order)
	 * => List should be ordered ASCending by column order
	 */
	public List<BusinessObjectProperty> getAttributes() {
		// DEPRECATED ONLY IN HERE BECAUSE IT AVOIDS COMPILING ERRORS!!!
		List<String> attr = Application.sss.getBusinessObjectProperties(this.getSAPId());
		
		ArrayList<BusinessObjectProperty> resultList = new ArrayList<BusinessObjectProperty>();
		
		for (String a : attr) {
			BusinessObjectProperty boa = new BusinessObjectProperty(a);
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
	public List<BusinessObjectProperty> getNeededAttributesList() {
		List<BusinessObjectProperty> attr = this.getAttributes();
		List<BusinessObjectProperty> re = new ArrayList<BusinessObjectProperty>();
		
		for (String att : neededAttributes) {
			for(BusinessObjectProperty ba : attr){
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
