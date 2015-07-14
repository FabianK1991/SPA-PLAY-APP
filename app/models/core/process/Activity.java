package models.core.process;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Arrays;

import models.core.serverModels.businessObject.BusinessObject;
import models.core.serverModels.businessObject.BusinessObjectProperty;
import models.core.util.parsing.ProcessParser;
import models.spa.api.process.buildingblock.Flow;
import models.spa.api.process.buildingblock.Node;
import play.Logger;

import java.sql.ResultSet;
import java.sql.SQLException;

import controllers.Application;

@SuppressWarnings("unused")
public class Activity {
	private models.spa.api.process.buildingblock.Activity activity;
	private ProcessModel pm;
	private BusinessObject bo;
	
	private static final ArrayList<String> gatewayTypes = new ArrayList<String>();

	private static final Map<String, String> activityTypes;
	
	static {
        HashMap<String, String> aMap = new HashMap<String, String>();
        
        aMap.put("bo_select", "Business Object Selection");
        aMap.put("bo_create", "Business Object Creation");
        aMap.put("bo_update", "Business Object Update");
        aMap.put("bo_delete", "Business Object Deletion");
        aMap.put("document_upload", "Document Upload");
        aMap.put("gateway_decision", "Gateway Decision");
        
        activityTypes = (Map<String, String>) Collections.unmodifiableMap(aMap);
    }
	
	
	public Activity(String id, ProcessModel pm){
		this.activity = (models.spa.api.process.buildingblock.Activity)pm.getSPANodeById(id);
		this.pm = pm;
	}
	
	public String getId() {
		return activity.getId();
	}
	
	public String getRawId() {
		return this.getId().replace(ProcessParser.nsm, "");
	}
	
	
	/*
	 * Returns the name of an Activity
	 */
	public String getName() {
		return activity.getName();
	}
	
	public void setName(String name){
		this.activity.setName(name);
	}
	
	/*
	 * Returns a unique identifier of the Activity in the BPMN XML file
	 * This identifier must be derived from the XML file, then saved in SPA and should also be consistently 
	 * used in the HTML SVG created by the CAMUNDA JS-BPMN viewer
	 */
	public String getBPMN_ID() {
		
		return activity.getId();
	}
	
	/*
	 * Todo fabi
	 * Returns the type of action (create, update, select, delete) of this Activity
	 * 
	 */
	public String getType() {
		Application.db.connect();
		
		String query = "SELECT activity_type FROM process_activities WHERE process_model = '%s' AND activity_id = '%s'";
		
		ArrayList<String> args = new ArrayList<String>();
		args.add(this.pm.getRawId());
		args.add(this.getRawId());
		
		ResultSet rs = Application.db.exec(query, args, true);
		
		try {
			if(rs.next()){
				return rs.getString("activity_type");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return null;
		//Logger.info(this.pm.getActionForActivity(this.activity.getId()));
		//Logger.info(this.pm.getActionForActivity(this.activity.getId()));
		//return this.pm.getActionForActivity(this.activity.getId());
	}
	
	/*
	 * TODO: Fabi
	 * changes the action type of this Activity
	 */
	public void setType(String type) {
		Application.db.connect();
		
		String query = "UPDATE process_activities SET activity_type = '%s' WHERE process_model = '%s' AND activity_id = '%s'";
		
		ArrayList<String> args = new ArrayList<String>();
		args.add(type);
		args.add(this.pm.getRawId());
		args.add(this.getRawId());
		
		Application.db.exec(query, args, false);
		return;
	}
	
	/*
	 * Returns a List of types of BusinessObjects that will be [created/updated/selected/deleted] by this Activity
	 */
	public BusinessObject getBusinessObject() {
		Application.db.connect();
		
		String query = "SELECT business_object FROM process_activities WHERE process_model = '%s' AND activity_id = '%s'";
		
		ArrayList<String> args = new ArrayList<String>();
		args.add(this.pm.getRawId());
		args.add(this.getRawId());
		
		ResultSet rs = Application.db.exec(query, args, true);
		
		try {
			if(rs.next()){
				return new BusinessObject(rs.getString("business_object"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	/*
	 * TODO: Fabi
	 * returns all properties for a BO type that are shown for example in the select BO table
	 */
	public List<BusinessObjectProperty> getBO_Properties() {
		Application.db.connect();
		
		String query = "SELECT bo_properties FROM process_activities WHERE process_model = '%s' AND activity_id = '%s'";
		
		ArrayList<String> args = new ArrayList<String>();
		args.add(this.pm.getRawId());
		args.add(this.getRawId());
		
		ResultSet rs = Application.db.exec(query, args, true);
		
		try {
			if(rs.next()){
				String[] props = rs.getString("bo_properties").split(",");
				ArrayList<BusinessObjectProperty> resultList = new ArrayList<BusinessObjectProperty>();
				
				for(int i=0;i<props.length;i++){
					resultList.add(new BusinessObjectProperty(props[i]));
				}
				
				return resultList;
			}
		} catch (Exception e) {
			//e.printStackTrace();
		}
		
		return null;
	}
	
	/*
	 * TODO: Fabi
	 * returns the minimum number of BO instances that must be selected
	 */
	public int getObjectAmountMin() {
		Application.db.connect();
		
		String query = "SELECT min_amount FROM process_activities WHERE process_model = '%s' AND activity_id = '%s'";
		
		ArrayList<String> args = new ArrayList<String>();
		args.add(this.pm.getRawId());
		args.add(this.getRawId());
		
		ResultSet rs = Application.db.exec(query, args, true);
		
		try {
			if(rs.next()){
				return rs.getInt("min_amount");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return -1;
	}
	
	/*
	 * TODO: Fabi
	 * returns the maximum number of BO instances that must be selected
	 */
	public int getObjectAmountMax() {
		Application.db.connect();
		
		String query = "SELECT max_amount FROM process_activities WHERE process_model = '%s' AND activity_id = '%s'";
		
		ArrayList<String> args = new ArrayList<String>();
		args.add(this.pm.getRawId());
		args.add(this.getRawId());
		
		ResultSet rs = Application.db.exec(query, args, true);
		
		try {
			if(rs.next()){
				return rs.getInt("max_amount");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return -1;
	}

	
	/*
	 * TODO: Fabi
	 * sets the type of BusinessObjects that will be [created/updated/selected/deleted] by this Activity
	 * => I don't think this is useful. Rather implement a create method and use setBO, setMax/Min and setProperties separately
	 */
	public void setBusinessObject(BusinessObject businessObject, String[] properties, String min, String max, String type) {
		// get bo id from name
		Application.db.connect();
		
		String databaseID = Application.sss.getBusinessObjectDatabaseId(businessObject.getId());
		//String query = "INSERT INTO process_activities (process_model,activity_id,activity_type,business_object,bo_properties,min_amount,max_amount) VALUES ('%s','%s','%s','%s','%s','%s','%s')";
		String query = "UPDATE process_activities SET activity_type = '%s',business_object = '%s',bo_properties = '%s',min_amount = '%s',max_amount = '%s' WHERE process_model = '%s' AND activity_id = '%s'";
		
		
		ArrayList<String> args = new ArrayList<String>();
		
		args.add(type);
		args.add(databaseID);
		
		String propertyString = "";
		
		for(int i=0;i<properties.length;i++){
			propertyString += properties[i];
			
		}
		
		args.add(propertyString);
		args.add(min);
		args.add(max);
		
		args.add(this.pm.getRawId());
		args.add(this.getRawId());
		
		Application.db.exec(query, args, false);

		return;
	}
	
	/*
	 * TODO: Fabi
	 * sets the type of BusinessObjects that will be [created/updated/selected/deleted] by this Activity
	 */
	public void setBusinessObject(BusinessObject businessObject) {
		// get bo id from name
		Application.db.connect();
		
		this.bo = businessObject;
		
		String databaseID = Application.sss.getBusinessObjectDatabaseId(businessObject.getId());
		String query = "UPDATE process_activities SET business_object = '%s', bo_properties = '', max_amount = 0, min_amount = 0 WHERE process_model = '%s' AND activity_id = '%s'";
		
		ArrayList<String> args = new ArrayList<String>();
		args.add(this.bo.getDBId());
		args.add(this.pm.getRawId());
		args.add(this.getRawId());
		
		Application.db.exec(query, args, false);

		return;
	}
	
	/*
	 * TODO: Fabi
	 * sets all properties for a BO type that are shown for example in the select BO table
	 */
	public void setBO_Properties(ArrayList<String> properties) {
		Application.db.connect();
		
		String joined = "";
		
		for(int i=0;i<properties.size();i++){
			joined += properties.get(i);
			
			if( i+1 < properties.size() ){
				joined += ",";
			}
		}
		
		String query = "UPDATE process_activities SET bo_properties = '%s' WHERE process_model = '%s' AND activity_id = '%s'";
		
		ArrayList<String> args = new ArrayList<String>();
		args.add(joined);
		args.add(this.pm.getRawId());
		args.add(this.getRawId());
		
		Application.db.exec(query, args, false);
		return;
	}
	
	/*
	 * TODO: Fabi
	 * sets the minimum number of BO instances that must be selected
	 */
	public void setObjectAmountMin(int amount) {
		Application.db.connect();
		
		String query = "UPDATE process_activities SET min_amount = '%s' WHERE process_model = '%s' AND activity_id = '%s'";
		
		ArrayList<String> args = new ArrayList<String>();
		args.add(Integer.toString(amount));
		args.add(this.pm.getRawId());
		args.add(this.getRawId());
		
		Application.db.exec(query, args, false);
		return;
	}
	
	/*
	 * TODO: Fabi
	 * sets the maximum number of BO instances that must be selected
	 */
	public void setObjectAmountMax(int amount) {
		Application.db.connect();
		
		String query = "UPDATE process_activities SET max_amount = '%s' WHERE process_model = '%s' AND activity_id = '%s'";
		
		ArrayList<String> args = new ArrayList<String>();
		args.add(Integer.toString(amount));
		args.add(this.pm.getRawId());
		args.add(this.getRawId());
		
		Application.db.exec(query, args, false);
		return;
	}
	
	public BusinessObject getBusinessObjectById(String id){
		List<BusinessObject> lBos = this.pm.getBosForActivity(this.activity.getId());
		
		for(BusinessObject bo: lBos){
			if(bo.getId().equals(id)){
				return bo;
			}
		}
		
		return null;
	}
	
	public List<Activity> getNextActivities() {
		List<Activity> resultList = new ArrayList<Activity>();
		
		for (Flow e : this.getSPAActivity().getNextFlows()) {
			Node n = e.getTo();
			
			if (n.type.equals("Node")) {
				resultList.add(new Activity(n.getId(), this.pm));
			}
		}
		return resultList;
	}
	
	/*
	 * TODO
	 */
	public Gateway getNextGateway() {
		List<Gateway> resultList = new ArrayList<Gateway>();
		
		for (Flow e : this.getSPAActivity().getNextFlows()) {
			Node n = e.getTo();
			
			if (Gateway.isGatewayType(n.type)) {
				return new Gateway(n.getId(), this.pm);
			}
		}
		return null;
	}
	
	public ProcessModel getModel() {
		return this.pm;
	}
	
	
	public models.spa.api.process.buildingblock.Activity getSPAActivity(){
		return this.activity;
	}
	
	public void setSPAActivity(models.spa.api.process.buildingblock.Activity activity){
		this.activity = activity;
	}
	
	public static Map<String, String> getTypes() {
		return Activity.activityTypes;
	}
}
