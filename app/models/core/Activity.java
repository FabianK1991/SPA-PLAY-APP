package models.core;

import java.util.List;

public class Activity {
	/*
	 * TODO
	 * Returns the name of an Activity
	 */
	public String getName() {
		return "";
	}
	
	/*
	 * TODO
	 * Returns a unique identifier of the Activity in the BPMN XML file
	 * This identifier must be derived from the XML file, then saved in SPA and should also be consistently 
	 * used in the HTML SVG created by the CAMUNDA JS-BPMN viewer
	 */
	public void getBPMN_ID() {
		
	}
	
	/*
	 * TODO
	 * Returns the type of action (create, update, select, delete) of this Activity
	 */
	public String getAction() {
		return "";
	}
	
	/*
	 * TODO
	 * Returns a List of types of BusinessObjects that will be [created/updated/selected/deleted] by this Activity
	 */
	public List<BusinessObject> getBusinessObjects() {
		return null;
	}
}
