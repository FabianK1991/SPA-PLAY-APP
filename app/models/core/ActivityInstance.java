package models.core;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import models.core.exceptions.ActivityInstanceNotFoundException;
import models.core.exceptions.BusinessObjectInstanceNotFoundException;
import models.core.exceptions.ProcessModelNotFoundException;
import models.util.parsing.ProcessParser;

public class ActivityInstance {
	// reference to the activity
	private String id;
	public Activity activity;
	
	private models.spa.api.process.buildingblock.instance.ActivityInstance activityInstance;
	private static List<ActivityInstance> instances = new ArrayList<ActivityInstance>();
	
	/*
	 * Method to internally (PRIVATE method) create an empty ActivityInstance
	 * Should be used only by static method ActivityInstance.create()
	 */
	private ActivityInstance(ProcessInstance pi, Activity activity) {
		this.id = ProcessParser.nsmi + getUID(pi);
		this.activity = activity;
		
		
		this.activityInstance = new models.spa.api.process.buildingblock.instance.ActivityInstance(pi.getSPAProcessInstance());
		this.activityInstance.setId(id);
		this.activityInstance.setActivity(activity.getSPAActivity().getId());
		
		// TODO: decide if we use user name or user id
		this.activityInstance.setAgent(pi.getUser().getName());
		
		// Write time
		DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
	    //get current date time with Date()
	    Date date = new Date();

		this.activityInstance.setDateTime(dateFormat.format(date));
		
		// Add to spa model
		pi.getSPAProcessInstance().getActivities().add(this.activityInstance);
		
		// Update SPA model?
		try {
			pi.getSPAProcessInstance().update();
		} catch (Exception e) {
			// Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/*
	 * Returns a the type of Activity of this ActivityInstance
	 * 
	 * >> Needs to SEARCH in SPA for a ActivityInstance with the given ID <<
	 * >> This activity instance (already existing!) needs to be instantiated, not a new one! <<
	 * 
	 */
	public ActivityInstance(String id, ProcessInstance pi) throws ActivityInstanceNotFoundException {
		Set<models.spa.api.process.buildingblock.instance.ActivityInstance> instances = pi.getSPAProcessInstance().getActivities();
		
		for(models.spa.api.process.buildingblock.instance.ActivityInstance ai : instances){
			if( ai.getId().equals(id) ){
				this.activityInstance = ai;
				this.activity = new Activity(ai.getActivity(), pi.getProcessModel());
				this.id = ai.getId();
				break;
			}
		}
		
		if( this.activityInstance == null ){
			throw new ActivityInstanceNotFoundException();
		}
	}
	
	public models.spa.api.process.buildingblock.instance.ActivityInstance getSPAActivityInstance(){
		return this.activityInstance;
	}
	
	/*
	 * Returns the type of Activity of this ActivityInstance
	 */
	public Activity getActivity() {
		return this.activity;
	}
	
	/*
	 * Returns a List of BusinessObjectInstances (sometimes created and) referenced by this ActivityInstance
	 */
	public List<BusinessObjectInstance> getBusinessObjectInstances() {
		Set<models.spa.api.process.buildingblock.instance.BusinessObjectInstance> list = this.getSPAActivityInstance().getBoi();
		List<BusinessObjectInstance> resultList = new ArrayList<BusinessObjectInstance>();
		
		for(models.spa.api.process.buildingblock.instance.BusinessObjectInstance SPABoi: list ){
			try {
				resultList.add(new BusinessObjectInstance(SPABoi.getId(), this));
			} catch (BusinessObjectInstanceNotFoundException e) {
				continue;
			}
		}
		
		return resultList;
	}
	
	/*
	 * Adds a reference to a BusinessObjectInstance to this ActivityInstance
	 *	Deprecated: Not needed anymore because of the additional parameter in BusinessObjectInstance.create the instance is automatically added to the ActivityInstance
	 */
	@Deprecated
	public void addBusinessObjectInstance(BusinessObjectInstance businessObjectInstance) {
		
	}
	
	/*
	 * Removes the reference to a BusinessObjectInstance from this ActivityInstance
	 */
	public void removeBusinessObjectInstance(BusinessObjectInstance businessObjectInstance) {
		businessObjectInstance.delete();
	}
	
	/*
	 * Creates and returns ActivityInstance referencing the "template" of an Activity,
	 * e.g. ActivityInstance.create(new Activity("create bill"))
	 * 
	 * >> This create method prevents the user of this class from setting IDs!! <<
	 */
	public static ActivityInstance create(ProcessInstance pi, Activity activity) {
		ActivityInstance newActivityInstance = new ActivityInstance(pi, activity);
		
		return newActivityInstance;
	}
	
	public String getTime(){
		return this.getSPAActivityInstance().getDateTime();
	}
	
	private static String getUID(ProcessInstance pi) {
		String id = "";
		
		while (true) {
			id = UUID.randomUUID().toString().replace('-', '0');
			
			try {
				new ActivityInstance(id, pi);
			} catch (ActivityInstanceNotFoundException e) {
				break;
			}
		}
		return id;
	}
}
