package models.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import play.Logger;
import models.spa.api.process.buildingblock.*;
import models.util.parsing.ProcessParser;

@SuppressWarnings("unused")
public class Activity {
	private models.spa.api.process.buildingblock.Activity activity;
	private ProcessModel pm;
	
	private static final ArrayList<String> gatewayTypes = new ArrayList<String>();
	
	
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
	public String getAction() {
		return this.pm.getActionForActivity(this.activity.getId());
	}
	
	/*
	 * Returns a List of types of BusinessObjects that will be [created/updated/selected/deleted] by this Activity
	 */
	public List<BusinessObject> getBusinessObjects() {
		return this.pm.getBosForActivity(this.activity.getId());
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
}
