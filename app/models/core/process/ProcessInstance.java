package models.core.process;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import play.Logger;
import controllers.Application;
import controllers.AuthController;
import models.spa.api.process.buildingblock.Flow;
import models.core.exceptions.ActivityInstanceNotFoundException;
import models.core.exceptions.PhaseNotFoundException;
import models.core.exceptions.ProcessInstanceNotFoundException;
import models.core.exceptions.ProcessModelNotFoundException;
import models.core.util.parsing.ProcessParser;
import models.util.db.DBHandler;
import models.util.session.User;

public class ProcessInstance {
	
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
		this.user = user;
		this.pm = pm;
		this.pi = new models.spa.api.ProcessInstance(pm.getSPAProcessModel());
		
		this.pi.setId(ProcessParser.nsmi + getUID());
	}
	
	/*
	 * Instantiates a ProcessInstance
	 */
	public ProcessInstance(String id) throws ProcessInstanceNotFoundException {
		try {
			Logger.info("TEST!!!" + id);
			
			this.pi = models.spa.api.ProcessInstance.getProcessInstance(null, id);
			
			if( this.pi.getId() == null ){
				throw new ProcessInstanceNotFoundException();
			}
			
			this.pm = new ProcessModel(this.pi.getProcessModel());
			this.user = AuthController.getUser();
			
			Logger.info("will be reached even if process instance does not exist!");
		} catch (Exception e) {
			throw new ProcessInstanceNotFoundException();
		}
	}
	
	@Override
	public boolean equals(Object o){
		if(o != null && this.getId().equals(((ProcessInstance) o).getId())){
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
		return this.pm.getName();
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
		List<Activity> activities = new ArrayList<Activity>();
		activities.add(activity);
		
		setCurrentActivities(activities);
	}
	
	/*
	 * Sets the current activities
	 */
	public void setCurrentActivities(List<Activity> activities){
		List<ActivityInstance> currentActivities = this.getCurrentActivities();
		List<String> nextActivities = new ArrayList<String>();
		
		for (ActivityInstance activityInstance : currentActivities) {
			for (Activity possibleNextActivity : activityInstance.getActivity().getNextActivities()) {
				nextActivities.add(possibleNextActivity.getRawId());
			}
			Gateway activeGateway = activityInstance.getActivity().getNextGateway();
			
			if (activeGateway != null) {
				HashMap<Node, HashMap<String, Object>> activeGatewayOptions = activeGateway.getOptions();
				
				for (Node activity : activeGatewayOptions.keySet()) {
					nextActivities.add(activity.getRawId());
				}
			}
		}
				
		for (Activity activity : activities) {
			if (nextActivities.contains(activity.getRawId())) {
				// creates a new activity instance
				ActivityInstance.create(this, activity);
			}
			else {
				/*
				 * TODO
				 * Somehow handle this bad request
				 */
			}
		}
	}
	
	public Phase getCurrentPhase() throws Exception {
		Logger.info(this.getCurrentActivities().get(0).toString());
		return this.getCurrentActivities().get(0).getActivity().getPhase();
	}
	
	public List<ActivityInstance> getCompletedActivities(){
		Set<models.spa.api.process.buildingblock.instance.ActivityInstance> instances = this.pi.getActivities();
		List<ActivityInstance> resultList = new ArrayList<ActivityInstance>();
		
		models.spa.api.process.buildingblock.instance.ActivityInstance latestInstance = null;
		
		for (models.spa.api.process.buildingblock.instance.ActivityInstance instance : instances) {
			SimpleDateFormat inFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
			
			try {
				Date dtIn = inFormat.parse(instance.getDateTime());
				
				if( latestInstance == null || dtIn.getTime() > inFormat.parse(latestInstance.getDateTime()).getTime() ){
					//resultList.clear();
					latestInstance = instance;
				}
				
				resultList.add(new ActivityInstance(instance.getId(), this));
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				
				continue;
			} catch (ActivityInstanceNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				
				continue;
			}
	    }
		
		Collections.sort(resultList, new Comparator<ActivityInstance>() {
	        @Override public int compare(ActivityInstance p1, ActivityInstance p2) {
	        	SimpleDateFormat inFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
	        	
	        	try {
	        		return (int)(inFormat.parse(p2.getTime()).getTime() - inFormat.parse(p1.getTime()).getTime()); // Ascending
	        	} catch (ParseException e) {
					return -1;
				}
	        }

	    });
		
		
		if( latestInstance != null ){
			resultList.remove(latestInstance);
			
			return resultList;
		}
		else{
			// get start activity
			//models.spa.api.process.buildingblock.Activity a = this.getStartActivity();
			
			// should be empty anyway
			resultList.clear();
			//resultList.add(ActivityInstance.create(this, new Activity(a.getId(), this.pm)));
			
			return resultList;
		}
	}
	
	public ActivityInstance getActivityInstance(Activity activity) {
		List<ActivityInstance> instances = this.getCompletedActivities();
		
		for (int i = 0; i < instances.size(); i++) {
			ActivityInstance instance = instances.get(i);
			
			if (instance.getActivity().getRawId().equals(activity.getRawId())) {
				return instance;
			}
		}
		return null;
	}
	
	/*
	 * Returns the ActivityInstance currently active in the ProcessInstance
	 */
	public List<ActivityInstance> getCurrentActivities() {
		Set<models.spa.api.process.buildingblock.instance.ActivityInstance> instances = this.pi.getActivities();
		List<ActivityInstance> resultList = new ArrayList<ActivityInstance>();
		
		models.spa.api.process.buildingblock.instance.ActivityInstance latestInstance = null;
		
		for (models.spa.api.process.buildingblock.instance.ActivityInstance instance : instances) {
			SimpleDateFormat inFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
			
			try {
				Date dtIn = inFormat.parse(instance.getDateTime());
				
				if( latestInstance == null || dtIn.getTime() > inFormat.parse(latestInstance.getDateTime()).getTime() ){
					resultList.clear();
					latestInstance = instance;
					resultList.add(new ActivityInstance(latestInstance.getId(), this));
				}
				else if( latestInstance != null && dtIn.getTime() == inFormat.parse(latestInstance.getDateTime()).getTime() ){
					resultList.add(new ActivityInstance(instance.getId(), this));
				}
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				
				continue;
			} catch (ActivityInstanceNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				
				continue;
			}
	    }
		
		if( latestInstance != null ){
			return resultList;
		}
		else{
			// get start activity
			models.spa.api.process.buildingblock.Activity a = this.getStartActivity();
			
			// should be empty anyway
			resultList.clear();
			try {
			resultList.add(ActivityInstance.create(this, new Activity(a.getId(), this.pm)));
			}
			catch(Exception e) {
				
			}
			return resultList;
		}
	}
	
	/*
	 * Creates and returns a ProcessInstance object that uses the passed "processModel" as template and references the current user passed as "user"
	 * All ProcessInstances need to be stored in database "user_process_instances" (columns: id, user, archive[no])
	 * 
	 * >> This create method prevents the user of this class from setting IDs!! <<
	 */
	public static ProcessInstance create(User user, ProcessModel processModel) {
		ProcessInstance newProcessInstance = new ProcessInstance(user, processModel);
		
		// Store it in SPA
		try {
			newProcessInstance.pi.store();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//Store it in DB
		Application.db.add(newProcessInstance);
		
		return newProcessInstance;
	}

	public void setId(String id) {
		this.pi.setId(id);
	}
	
	public String getRawId() {
		return this.getId().replace(ProcessParser.nsmi, "");
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
	
	public String getDatabaseId(){
		String query = "SELECT id FROM process_instances WHERE process = '%s'";
		
		ArrayList<String> args = new ArrayList<String>();
		args.add(this.getRawId());
		
		ResultSet rs = Application.db.exec(query, args, true);
		
		try {
			if(rs.next()){
				return rs.getString("id");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return null;
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
	
	public void terminate() {
		Application.db.connect();
		
		String query = "UPDATE process_instances SET status = 'completed' WHERE id = '%s'";
		
		ArrayList<String> args = new ArrayList<String>();
		args.add(this.getDatabaseId());
		
		Application.db.exec(query, args, false);
		return;
	}
}