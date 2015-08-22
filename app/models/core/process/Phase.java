package models.core.process;

import java.util.ArrayList;

import controllers.Application;
import models.core.exceptions.PhaseNotFoundException;
import models.core.process.ProcessModel;
import models.core.util.parsing.ProcessParser;

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
	
	public Phase(String phaseId) throws PhaseNotFoundException {
		Application.db.connect();
		
		String query = "SELECT name, process FROM process_phases WHERE id = '%s'";
		
		ArrayList<String> args = new ArrayList<String>();
		args.add(phaseId);
		
		ResultSet rs = Application.db.exec(query, args, true);
		
		try {
			if(rs.next()){
				this.id = phaseId;
				this.name = rs.getString("name");
				this.pm = new ProcessModel(ProcessParser.nsm + rs.getString("process"));
			}
		} catch (Exception e) {
			throw new PhaseNotFoundException();
		}
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
	
	public void setName(String name){
		Application.db.connect();
		
		String query = "UPDATE process_phases SET name = '%s' WHERE id = '%s'";
		
		ArrayList<String> args = new ArrayList<String>();
		args.add(name);
		args.add(this.getId());
		
		Application.db.exec(query, args, false);

		return;
	}
	
	public void delete(){
		Application.db.connect();
		
		String query = "DELETE FROM process_phases WHERE id = '%s'";
		
		ArrayList<String> args = new ArrayList<String>();
		args.add(this.getId());
		
		Application.db.exec(query, args, false);

		return;
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
				Activity a = null;
				try {
					a = new Activity(ProcessParser.nsm + rs.getString("activity"), this.pm);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
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
		args.add(a.getRawId());
		
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
		args.add(a.getRawId());
		
		Application.db.exec(query, args, false);

		return;
	}
	
	public ProcessModel getProcessModel() {
		return this.pm;
	}
}
