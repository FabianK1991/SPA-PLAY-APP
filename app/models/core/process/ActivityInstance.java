package models.core.process;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import controllers.Application;
import models.core.exceptions.ActivityInstanceNotFoundException;
import models.core.exceptions.BusinessObjectInstanceNotFoundException;
import models.core.exceptions.ProcessModelNotFoundException;
import models.core.serverModels.businessObject.BusinessObject;
import models.core.serverModels.businessObject.BusinessObjectInstance;
import models.core.util.parsing.ProcessParser;

public class ActivityInstance {
	// reference to the activity
	private String id;
	public Activity activity;
	
	private models.spa.api.process.buildingblock.instance.ActivityInstance activityInstance;
	private static List<ActivityInstance> instances = new ArrayList<ActivityInstance>();
	
	private ProcessInstance pi;
	
	/*
	 * Method to internally (PRIVATE method) create an empty ActivityInstance
	 * Should be used only by static method ActivityInstance.create()
	 */
	private ActivityInstance(ProcessInstance pi, Activity activity) {
		this(pi, activity, new Date());
	}
	
	private ActivityInstance(ProcessInstance pi, Activity activity, Date date) {
		this.id = ProcessParser.nsmi + getUID(pi);
		this.activity = activity;
		
		this.pi = pi;
		
		
		this.activityInstance = new models.spa.api.process.buildingblock.instance.ActivityInstance(pi.getSPAProcessInstance());
		this.activityInstance.setId(id);
		this.activityInstance.setActivity(activity.getSPAActivity().getId());
		
		// TODO: decide if we use user name or user id
		this.activityInstance.setAgent(pi.getUser().getId());
		
		// Write time
		DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
	    //get current date time with Date()

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
	/*public List<BusinessObjectInstance> getBusinessObjectInstances() {
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
	}*/
	
	/*
	 * Adds a reference to a BusinessObjectInstance to this ActivityInstance
	 *	Deprecated: Not needed anymore because of the additional parameter in BusinessObjectInstance.create the instance is automatically added to the ActivityInstance
	 */
	/*public void addBusinessObjectInstance(BusinessObject businessObject) {
		BusinessObjectInstance.create(this, businessObject);
	}*/
	
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
	
	public static List<ActivityInstance> create(ProcessInstance pi, List<Activity> activities) {
		Date date = new Date();
		List<ActivityInstance> resultList = new ArrayList<ActivityInstance>();
		
		for(Activity activity: activities){
			ActivityInstance newActivityInstance = new ActivityInstance(pi, activity, date);
			resultList.add(newActivityInstance);
		}

		return resultList;
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
	
	/*
	 * int, string, businessobjectinstance
	 * 
	 * 
	 */
	public void setOutputs(List<Object> outputs) {
		String pi_id = this.pi.getDatabaseId();
		String activity_id = this.activity.getDatabaseId();
		
		// Delete old entries
		String query = "DELETE FROM process_activity_instance_outputs WHERE process_instance = '%s' AND activity = '%s'";
		
		ArrayList<String> args = new ArrayList<String>();
		args.add(pi_id);
		args.add(activity_id);
		
		Application.db.exec(query, args, false);
		
		int order = 0;
		
		for (Object temp : outputs) {
			
			query = "INSERT INTO process_activity_instance_outputs (process_instance,activity,output,type,`order`) VALUES ('%s', '%s', '%s', '%s', '%s')";
	        args = new ArrayList<String>();
	        
	        args.add(pi_id);
	        args.add(activity_id);
	        
	        if( temp instanceof BusinessObjectInstance ){
	        	args.add(((BusinessObjectInstance)temp).getInstanceId());
	        	args.add("BusinessObjectInstance");
	        }
	        else{
	        	args.add(temp.toString());
	        	args.add("String");
	        }
	        
	        args.add(Integer.toString(order));
			
	        order++;
		}

	}
	
	public List<Object> getOutputs() {
		String pi_id = this.pi.getDatabaseId();
		String activity_id = this.activity.getDatabaseId();
		ArrayList<Object> returnList = new ArrayList<Object>();
		
		String query = "SELECT * FROM process_activity_instance_outputs WHERE process_instance = '%s' AND activity = '%s' ORDER BY `order` ASC";
		
		ArrayList<String> args = new ArrayList<String>();
		args.add(pi_id);
		args.add(activity_id);
		
		ResultSet rs = Application.db.exec(query, args, true);
		
		try {
			while(rs.next()){
				if( rs.getString("type").equals("BusinessObjectInstance") ){
					try {
						returnList.add(BusinessObjectInstance.getBySAPId(this.activity.getBusinessObject(), rs.getString("output")));
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				else{
					returnList.add(rs.getString("output"));
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return returnList;
	}
	
	public ActivityInstance getPreceedingActivityInstance() {
		Set<models.spa.api.process.buildingblock.instance.ActivityInstance> instances = this.pi.getSPAProcessInstance().getActivities();
		models.spa.api.process.buildingblock.instance.ActivityInstance latestInstance = null;
		
		for (models.spa.api.process.buildingblock.instance.ActivityInstance instance : instances) {
			SimpleDateFormat inFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
			
			try {
				Date dtIn = inFormat.parse(instance.getDateTime());
				
				if( dtIn.getTime() < inFormat.parse(this.activityInstance.getDateTime()).getTime() ){
					if( latestInstance == null || dtIn.getTime() > inFormat.parse(latestInstance.getDateTime()).getTime() ){
						latestInstance = instance;
					}
				}
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				
				continue;
			} 
	    }
		
		if( latestInstance != null ){
			try {
				return new ActivityInstance(latestInstance.getId(), this.pi);
			} catch (ActivityInstanceNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return null;
	}
}
