package models.core;

import java.util.List;

public class ActivityInstance {
	/*
	 * TODO
	 * Method to internally (PRIVATE method) create an empty ActivityInstance
	 * Should be used only by static method ActivityInstance.create()
	 */
	private ActivityInstance() {
		
	}
	
	/*
	 * TODO
	 * Instantiates a ActivityInstance according to the given id
	 */
	public ActivityInstance(String id) {
		
	}
	
	/*
	 * TODO
	 * Returns a the type of Activity of this ActivityInstance
	 */
	public Activity getActivity() {
		return null;
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
	 */
	public static ActivityInstance create(Activity activity) {
		return null;
	}
}
