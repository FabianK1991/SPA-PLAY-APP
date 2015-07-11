package models.core.process;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.core.serverModels.businessObject.BusinessObject;
import models.core.util.parsing.ProcessParser;
import models.spa.api.process.buildingblock.Flow;
import models.spa.api.process.buildingblock.Node;
import play.Logger;

@SuppressWarnings("unused")
public class Activity {
	private models.spa.api.process.buildingblock.Activity activity;
	private ProcessModel pm;
	
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
	 * Returns the type of action (create, update, select, delete) of this Activity
	 * 
	 */
	public String getType() {
		Logger.info(this.pm.getActionForActivity(this.activity.getId()));
		Logger.info(this.pm.getActionForActivity(this.activity.getId()));
		return this.pm.getActionForActivity(this.activity.getId());
	}
	
	/*
	 * TODO: Fabi
	 * changes the action type of this Activity
	 */
	public void setType(String type) {
	}
	
	/*
	 * Returns a List of types of BusinessObjects that will be [created/updated/selected/deleted] by this Activity
	 */
	public BusinessObject getBusinessObject() {
		return this.pm.getBosForActivity(this.activity.getId()).get(0);
	}
	
	/*
	 * TODO: Fabi
	 * returns all properties for a BO type that are shown for example in the select BO table
	 */
	public ArrayList<String> getBO_Properties() {
		return null;
	}
	
	/*
	 * TODO: Fabi
	 * returns the minimum number of BO instances that must be selected
	 */
	public int getObjectAmountMin() {
		return 0;
	}
	
	/*
	 * TODO: Fabi
	 * returns the maximum number of BO instances that must be selected
	 */
	public int getObjectAmountMax() {
		return 0;
	}

	
	/*
	 * TODO: Fabi
	 * sets the type of BusinessObjects that will be [created/updated/selected/deleted] by this Activity
	 */
	public void setBusinessObject(BusinessObject businessObject) {
	}
	
	/*
	 * TODO: Fabi
	 * sets all properties for a BO type that are shown for example in the select BO table
	 */
	public void setBO_Properties(ArrayList<String> properties) {
	}
	
	/*
	 * TODO: Fabi
	 * sets the minimum number of BO instances that must be selected
	 */
	public void setObjectAmountMin(int amount) {
	}
	
	/*
	 * TODO: Fabi
	 * sets the maximum number of BO instances that must be selected
	 */
	public void setObjectAmountMax(int amount) {
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
