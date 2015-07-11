package models.core.process;

import java.util.ArrayList;
import controllers.Application;
import models.core.process.ProcessModel;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Phase {
	private String id;
	private String name;
	private ArrayList<Activity> activities;
	private ProcessModel pm;
	
	public Phase(String id, String name, ProcessModel pm){
		this.id = id;
		this.name = name;
		this.pm = pm;
	}
	
	/*
	 * TODO: Fabi
	 */
	public String getId() {
		return this.id;
	}
	
	/*
	 * TODO: Fabi
	 */
	public String getName() {
		return this.name;
	}
	
	/*
	 * TODO: Fabi
	 * see DB table: process_phase_activities
	 */
	public ArrayList<Activity> getActivities() {
		Application.db.connect();
		
		String query = "SELECT activity FROM process_phase_activities WHERE process_phase = '%s'";
		
		ArrayList<String> args = new ArrayList<String>();
		args.add(this.getId());
		
		ResultSet rs = Application.db.exec(query, args, true);
		
		try {
			ArrayList<Activity> resultList = new ArrayList<Activity>();
			
			while(rs.next()){
				Activity a = new Activity(rs.getString("activity"), this.pm);
				
				resultList.add(a);
			}
			
			return resultList;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	/*
	 * TODO: Fabi
	 */
	public void addActivity(Activity a) {
		Application.db.connect();
		
		String query = "INSERT INTO process_phase_activities (process_phase,activity) VALUES ('%s','%s')";
		
		ArrayList<String> args = new ArrayList<String>();
		args.add(this.getId());
		args.add(a.getId());
		
		Application.db.exec(query, args, false);

		return;
	}
	
	/*
	 * TODO: Fabi
	 */
	public void removeActivity(Activity a) {
		Application.db.connect();
		
		String query = "DELETE FROM process_phase_activities WHERE process_phase = '%s' AND activity = '%s'";
		
		ArrayList<String> args = new ArrayList<String>();
		args.add(this.getId());
		args.add(a.getId());
		
		Application.db.exec(query, args, false);

		return;
	}
}
