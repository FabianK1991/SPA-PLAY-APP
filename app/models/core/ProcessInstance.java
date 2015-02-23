package models.core;

import models.util.sessions.User;

public class ProcessInstance {
	/*
	 * TODO
	 * Method to internally (PRIVATE method) create an empty ProcessInstance
	 * Should be used only by static method ProcessInstance.create()
	 */
	private ProcessInstance() {
		
	}
	
	/*
	 * TODO
	 * Instantiates a ProcessInstance
	 */
	public ProcessInstance(String id) {
		
	}
	
	/*
	 * TODO
	 * Returns the reference to the ProcessModel used as template by the static method ProcessInstance.create()
	 */
	public ProcessModel getProcessModel() {
		return null;
	}
	
	/*
	 * TODO
	 * Returns the ActivityInstance currently active in the ProcessInstance
	 */
	public ActivityInstance getCurrenctActivity() {
		return null;
	}
	
	/*
	 * TODO
	 * Creates and returns a ProcessInstance object that uses the passed "processModel" as template and references the current user passed as "user"
	 * All ProcessInstances need to be stored in database "user_process_instances" (columns: id, user, archive[no])
	 */
	public static ProcessInstance create(User user, ProcessModel processModel) {
		return null;
	}
}
