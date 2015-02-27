package models.core;

import java.util.List;
import models.spa.api.process.buildingblock.*;

public class Activity {
	private models.spa.api.process.buildingblock.Activity activity;
	private String action;
	
	public Activity(models.spa.api.ProcessModel pm){
		this.activity = new models.spa.api.process.buildingblock.Activity(pm);
	}
	
	/*
	 * TODO
	 * Returns the name of an Activity
	 */
	public String getName() {
		return activity.getName();
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
	 */
	public String getAction() {
		return this.action;
	}
	
	/*
	 * TODO
	 * Returns a List of types of BusinessObjects that will be [created/updated/selected/deleted] by this Activity
	 */
	public List<BusinessObject> getBusinessObjects() {
		return null;
	}
}
