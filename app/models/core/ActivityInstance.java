package models.core;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import models.core.exceptions.ActivityInstanceNotFoundException;
import models.core.exceptions.ProcessModelNotFoundException;

public class ActivityInstance {
	// reference to the activity
	private Activity activity;
	private String id;
	
	private models.spa.api.process.buildingblock.instance.ActivityInstance activityInstance;
	
	/*
	 * TODO
	 * Method to internally (PRIVATE method) create an empty ActivityInstance
	 * Should be used only by static method ActivityInstance.create()
	 */
	private ActivityInstance(ProcessInstance pi, Activity activity) {
		this.id = getUID();
		this.activity = activity;
		
		this.activityInstance = new models.spa.api.process.buildingblock.instance.ActivityInstance(pi.getSPAProcessInstance());
		this.activityInstance.setId(id);
		
		// TODO: decide if we use user name or user id
		this.activityInstance.setAgent(pi.getUser().getName());
		
		// Write time
		DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
	    //get current date time with Date()
	    Date date = new Date();

		this.activityInstance.setDateTime(dateFormat.format(date));
		
		// TODO: handle bos
	}
	
	/*
	 * TODO
	 * Returns a the type of Activity of this ActivityInstance
	 * 
	 * >> Needs to SEARCH in SPA for a ActivityInstance with the given ID <<
	 * >> This activity instance (already existing!) needs to be instantiated, not a new one! <<
	 */
	public ActivityInstance(String id) throws ActivityInstanceNotFoundException {
		/*IF id does not exists, throw exception*/
		if (true) {
			throw new ActivityInstanceNotFoundException();
		}/*
		else {
			retrieve instance from ID and fill the properties
		}*/
	}
	
	public models.spa.api.process.buildingblock.instance.ActivityInstance getSPAActivityInstance(){
		return this.activityInstance;
	}
	
	/*
	 * TODO
	 * Returns a the type of Activity of this ActivityInstance
	 */
	public Activity getActivity() {
		return this.getActivity();
	}
	
	/*
	 * TODO
	 * Returns a List of BusinessObjectInstances (sometimes created and) referenced by this ActivityInstance
	 */
	public List<BusinessObjectInstance> getBusinessObjectInstances() {
		return null;
	}
	
	/*
	 * TODO
	 * Adds a reference to a BusinessObjectInstance to this ActivityInstance
	 */
	public void addBusinessObjectInstance(BusinessObjectInstance businessObjectInstance) {
		
	}
	
	/*
	 * TODO
	 * Removes the reference to a BusinessObjectInstance from this ActivityInstance
	 */
	public void removeBusinessObjectInstance(BusinessObjectInstance businessObjectInstance) {
		
	}
	
	/*
	 * TODO
	 * Creates and returns ActivityInstance referencing the "template" of an Activity,
	 * e.g. ActivityInstance.create(new Activity("create bill"))
	 * 
	 * >> This create method prevents the user of this class from setting IDs!! <<
	 */
	public static ActivityInstance create(ProcessInstance pi, Activity activity) {
		ActivityInstance newActivityInstance = new ActivityInstance(pi, activity);
		
		return newActivityInstance;
	}
	
	private static String getUID() {
		String id = "";
		
		while (true) {
			id = UUID.randomUUID().toString();
			
			try {
				new ActivityInstance(id);
			} catch (ActivityInstanceNotFoundException e) {
				break;
			}
		}
		return id;
	}
}
