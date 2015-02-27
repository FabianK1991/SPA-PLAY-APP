package models.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import models.spa.api.process.buildingblock.*;

public class Activity {
	private models.spa.api.process.buildingblock.Activity activity;
	private ArrayList<BusinessObject> bos;
	
	public Activity(models.spa.api.ProcessModel pm){
		this.activity = new models.spa.api.process.buildingblock.Activity(pm);
	}
	
	public Activity(models.spa.api.process.buildingblock.Activity activity){
		this.activity = activity;
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
	 * 
	 * @Deprecated Use business object instead to get action
	 */
	@Deprecated
	public String getAction() {
		return null;
	}
	
	/*
	 * Adds a business object to the activity
	 * 
	 */
	public void addBusinessObject(String id, String action, int min, int max){
		this.addBusinessObject(id, action, min, max, null);
	}
	
	/*
	 * Adds a business object to the activity
	 */
	public void addBusinessObject(String id, String action, int min, int max, List<String> neededAttributes){
		// add to internal list
		this.bos.add(new BusinessObject(id, action, min, max, neededAttributes));
		
		// add to spa activity
		models.spa.api.process.buildingblock.BusinessObject bo = new models.spa.api.process.buildingblock.BusinessObject(this.activity.getProcess());
		this.activity.getBusinessObjects().add(bo);
	}
	
	/*
	 * TODO
	 * Returns a List of types of BusinessObjects that will be [created/updated/selected/deleted] by this Activity
	 */
	public List<BusinessObject> getBusinessObjects() {
		/*Set<models.spa.api.process.buildingblock.BusinessObject> bos = this.activity.getBusinessObjects();
		
		ArrayList<BusinessObject> resultList = new ArrayList<BusinessObject>();
		
		for (models.spa.api.process.buildingblock.BusinessObject bo : bos) {
		    BusinessObject b = new BusinessObject(bo.getId());
		    
		    resultList.add(b);
		}*/
		
		return this.bos;
	}
	
	public models.spa.api.process.buildingblock.Activity getSPAActivity(){
		return this.activity;
	}
	
	public void setSPAActivity(models.spa.api.process.buildingblock.Activity activity){
		this.activity = activity;
	}
}
