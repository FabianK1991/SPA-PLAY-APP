package models.core;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import controllers.Application;
import models.spa.api.process.buildingblock.Flow;
import models.core.exceptions.ActivityInstanceNotFoundException;
import models.core.exceptions.ProcessInstanceNotFoundException;
import models.core.exceptions.ProcessModelNotFoundException;
import models.util.db.DBHandler;
import models.util.parsing.ProcessParser;
import models.util.sessions.User;

public class ProcessInstance {
	
	private String id; 
	private User user;
	private String process;
	private Date time;
	
	private ProcessModel pm;
	private models.spa.api.ProcessInstance pi;
	
	/*
	 * Method to internally (PRIVATE method) create an empty ProcessInstance
	 * Should be used only by static method ProcessInstance.create()
	 */
	private ProcessInstance(User user, ProcessModel pm) {
		this.id = ProcessParser.nsmi + getUID();
		this.pm = pm;
		this.pi = new models.spa.api.ProcessInstance(pm.getSPAProcessModel());
	
		this.user = user;
		this.pi.setId(id);
	}
	
	/*
	 * Instantiates a ProcessInstance
	 */
	public ProcessInstance(String id) throws ProcessInstanceNotFoundException {
		try {
			this.pi = models.spa.api.ProcessInstance.getProcessInstance(null, id);
			
			this.pm = new ProcessModel(this.pi.getProcessModel());
		} catch (Exception e) {
			throw new ProcessInstanceNotFoundException();
		}
	}
	
	@Override
	public boolean equals(Object o){
		if(this.getId().equals(((ProcessInstance) o).getId())){
			return true;
		}else{
			return false;
		}
	}
	
	/*
	 * Returns the ID of this ProcessInstance
	 */
	public String getId() {
		return this.pi.getId();
	}
	
	/*
	 * Returns the name of this ProcessInstance
	 */
	public String getName() {
		return this.pm.getName() + " - (" + this.getCurrentActivity().getTime()+ ")";
//		return this.pi.getName();
	}
	
	public models.spa.api.ProcessInstance getSPAProcessInstance(){
		return this.pi;
	}
	
	/*
	 * Returns the reference to the ProcessModel used as template by the static method ProcessInstance.create()
	 */
	public ProcessModel getProcessModel() {
		return this.pm;
	}
	
	private models.spa.api.process.buildingblock.Activity getStartActivity(){
		models.spa.api.ProcessModel pm = this.pm.getSPAProcessModel();

 		Set<models.spa.api.process.buildingblock.Node> nodes = pm.getNodes();
 		models.spa.api.process.buildingblock.Node startNode = null;
 		
 		for(models.spa.api.process.buildingblock.Node n : nodes){
 			if(n instanceof models.spa.api.process.buildingblock.Event && ((models.spa.api.process.buildingblock.Event)n).type == models.spa.api.process.buildingblock.Event.EventType.Start.toString()){
 				startNode = n;
 				break;
 			}
 		}
 		
 		// Now get first activity
 		if( startNode != null ){
 			for(models.spa.api.process.buildingblock.Flow f : startNode.getNextFlows()){
 				if( f.getTo() instanceof models.spa.api.process.buildingblock.Activity ){
 					return (models.spa.api.process.buildingblock.Activity)f.getTo();
 				}
 			};
 		}
 		
 		return null;
 	}
	
	/*
	 * Sets the current activity
	 */
	public void setCurrentActivity(Activity activity){
		// creates a new activity instance
		ActivityInstance.create(this, activity);
	}
	
	/*
	 * Returns the ActivityInstance currently active in the ProcessInstance
	 */
	public ActivityInstance getCurrentActivity() {
		Set<models.spa.api.process.buildingblock.instance.ActivityInstance> instances = this.pi.getActivities();
		
		models.spa.api.process.buildingblock.instance.ActivityInstance latestInstance = null;
		
		for (models.spa.api.process.buildingblock.instance.ActivityInstance instance : instances) {
			SimpleDateFormat inFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
			
			try {
				Date dtIn = inFormat.parse(instance.getDateTime());
				
				if( latestInstance == null || dtIn.getTime() > inFormat.parse(latestInstance.getDateTime()).getTime() ){
					latestInstance = instance;
				}
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				
				continue;
			}
	    }
		
		if( latestInstance != null ){
			try {
				return new ActivityInstance(latestInstance.getActivity(), this);
			} catch (ActivityInstanceNotFoundException e) {
				e.printStackTrace();
				
				return null;
			}
		}
		else{
			// get start activity
			models.spa.api.process.buildingblock.Activity a = this.getStartActivity();
			
			return ActivityInstance.create(this, new Activity(a.getId(), this.pm));
		}
	}
	
	/*
	 * Creates and returns a ProcessInstance object that uses the passed "processModel" as template and references the current user passed as "user"
	 * All ProcessInstances need to be stored in database "user_process_instances" (columns: id, user, archive[no])
	 * 
	 * >> This create method prevents the user of this class from setting IDs!! <<
	 */
	public static ProcessInstance create(User user, ProcessModel processModel) {
		ProcessInstance newProcessInstance = null;
		
		while (true) {
			String id = UUID.randomUUID().toString();
			
			try {
				new ProcessInstance(id);
			} catch (ProcessInstanceNotFoundException e) {
				newProcessInstance = new ProcessInstance(user, processModel);
				
				break;
			}
		}
		
		
		// Store it in SPA
		try {
			newProcessInstance.pi.store();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//Store it in DB
		DBHandler db = Application.db;
		db.add(newProcessInstance);
		
		return newProcessInstance;
	}

	public void setId(String id) {
		this.id = id;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String getProcess() {
		return process;
	}

	public void setProcess(String process) {
		this.process = process;
	}

	public Date getTime() {
		return time;
	}

	public void setTime(Date time) {
		this.time = time;
	}
	
	private static String getUID() {
		String id = "";
		
		while (true) {
			id = UUID.randomUUID().toString().replace('-', '0');
			
			try {
				new ProcessInstance(id);
			} catch (ProcessInstanceNotFoundException e) {
				break;
			}
		}
		return id;
	}
}