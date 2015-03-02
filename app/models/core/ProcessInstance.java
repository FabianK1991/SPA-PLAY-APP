package models.core;

import java.util.Date;
import java.util.UUID;

import models.core.exceptions.ActivityInstanceNotFoundException;
import models.core.exceptions.ProcessInstanceNotFoundException;
import models.core.exceptions.ProcessModelNotFoundException;
import models.util.sessions.User;

public class ProcessInstance {
	
	private String id; 
	private User user;
	private String process;
	private Date time;
	
	private ProcessModel pm;
	private models.spa.api.ProcessInstance pi;
	
	private ActivityInstance currentActivity;
	
	/*
	 * TODO
	 * Method to internally (PRIVATE method) create an empty ProcessInstance
	 * Should be used only by static method ProcessInstance.create()
	 */
	private ProcessInstance(User user, ProcessModel pm) {
		this.id = getUID();
		this.pm = pm;
		this.pi = new models.spa.api.ProcessInstance(pm.getSPAProcessModel());
		
		this.pi.setId(id);
	}
	
	/*
	 * TODO
	 * Instantiates a ProcessInstance
	 */
	public ProcessInstance(String id) throws ProcessInstanceNotFoundException {
		/*IF id does not exists, throw exception*/
		if (true) {
			throw new ProcessInstanceNotFoundException();
		}/*
		else {
			retrieve instance from ID and fill the properties
		}*/
	}
	
	/*
	 * TODO
	 * Returns the ID of this ProcessInstance
	 */
	public String getID() {
		return this.pi.getId();
	}
	
	/*
	 * TODO
	 * Returns the name of this ProcessInstance
	 */
	public String getName() {
		return this.pi.getName();
	}
	
	public models.spa.api.ProcessInstance getSPAProcessInstance(){
		return this.pi;
	}
	
	/*
	 * TODO
	 * Returns the reference to the ProcessModel used as template by the static method ProcessInstance.create()
	 */
	public ProcessModel getProcessModel() {
		return this.pm;
	}
	
	/*
	 * TODO
	 * Returns the ActivityInstance currently active in the ProcessInstance
	 */
	public ActivityInstance getCurrentActivity() {
		return this.currentActivity;
	}
	
	/*
	 * TODO
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
			id = UUID.randomUUID().toString();
			
			try {
				new ProcessInstance(id);
			} catch (ProcessInstanceNotFoundException e) {
				break;
			}
		}
		return id;
	}
}