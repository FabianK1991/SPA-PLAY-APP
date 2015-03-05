package models.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import models.spa.api.process.buildingblock.*;

@SuppressWarnings("unused")
public class Activity {
	private models.spa.api.process.buildingblock.Activity activity;
	private ProcessModel pm;
	
	public Activity(String id, ProcessModel pm){
		this.activity = (models.spa.api.process.buildingblock.Activity)pm.getSPANodeById(id);
		this.pm = pm;
	}
	
	/*
	 * TODO
	 * Returns the name of an Activity
	 */
	public String getName() {
		return activity.getName();
	}
	
	public void setName(String name){
		this.activity.setName(name);
	}
	
	/*
	 * TODO
	 * Returns a unique identifier of the Activity in the BPMN XML file
	 * This identifier must be derived from the XML file, then saved in SPA and should also be consistently 
	 * used in the HTML SVG created by the CAMUNDA JS-BPMN viewer
	 */
	public String getBPMN_ID() {
		
		return activity.getId();
	}
	
	/*
	 * TODO
	 * Returns the type of action (create, update, select, delete) of this Activity
	 * 
	 */
	public String getAction() {
		return this.pm.getActionForActivity(this.activity.getId());
	}
	
	/*
	 * TODO
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
	
	
	public models.spa.api.process.buildingblock.Activity getSPAActivity(){
		return this.activity;
	}
	
	public void setSPAActivity(models.spa.api.process.buildingblock.Activity activity){
		this.activity = activity;
	}
}
