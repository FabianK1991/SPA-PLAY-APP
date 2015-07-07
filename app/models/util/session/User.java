package models.util.session;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import models.core.process.ProcessInstance;
import models.core.process.ProcessModel;
import models.core.util.parsing.ProcessParser;
import models.util.db.DBHandler;
import play.Logger;
import play.mvc.Controller;
import controllers.Application;
import controllers.AuthController;

public class User {
	
	private String id;
	private String role;
	private String email;
	private String name;
	private String passwd;
	private Date time;
	private Session session;
	private ProcessInstance currentProcessInstance;
	
	
	/**
	 * Instantiates a User object and fills it automatically with the given attributes provided in the database.
	 * @author Christian
	 * @param id The id of the user
	 */
	public User(String id){
		time = new Date();
		
		this.id = id;
		ArrayList<String> filling = Application.db.select(this, true);
		if(filling != null){
			this.role = filling.get(1);
			this.email = filling.get(2);
			this.name = filling.get(3);
			this.passwd = filling.get(4);
			try {
				this.time = DBHandler.format.parse(filling.get(5));
			} catch (ParseException e) {
				e.printStackTrace();
			}
			try{
				this.currentProcessInstance = new ProcessInstance(ProcessParser.nsmi + filling.get(6));
			}catch (Exception e){
				e.printStackTrace();
			}
			if(Session.o != null){
				this.session = Session.o;
			}
		}
		
		
	}
	
	// TEST CONSTRUCTOR TO AVOID DB
	public User(String id, String name) {
		this.id = id;
		this.name = name;
	}
	
	public void setCurrentProcessInstance(ProcessInstance pi){
		this.currentProcessInstance = pi;
		Application.db.update(this, "current_process", pi.getRawId());
	}
	
	
	public User() {
		
	}

	/**
	 * Fills the attributes of a user Object by providing an email address.
	 * @author Christian
	 * @param email The email of the user.
	 * @return True if the filling process was successful, false otherwise.
	 */
	public boolean init(String email){
		this.setEmail(email);
		ArrayList<String> filling = Application.db.select(this, false);
		if(filling != null){
			this.id = filling.get(0);
			this.role = filling.get(1);
			this.email = filling.get(2);
			this.name = filling.get(3);
			this.passwd = filling.get(4);
			try {
				this.time = DBHandler.format.parse(filling.get(5));
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				
			}
			try{
				this.currentProcessInstance = new ProcessInstance(ProcessParser.nsmi + filling.get(6));
			}catch (Exception e){
				if (AuthController.getSession() != null && AuthController.getUser() != null) {
					this.getCurrentProcessInstance();
				}
			}
			
			return true;
		}else{
			return false;
		}
		
	}
	/**
	 * Creates a Session that belongs to the User and adds it to the database.
	 * @author Christian
	 * @param passwd The password of the User.
	 * @return True if the password provided by the user matches the password of the User Objecet. False otherwise.
	 */
	public boolean createSession(String passwd){
		String password = Application.sha1(passwd);
//		Logger.debug(password);
//		Logger.debug(passwd);
//		Logger.debug(this.passwd);
		if(this.passwd != null && passwd != null && this.passwd.equals(password)){
			this.session = new Session();
			this.session.setKey(Application.sha1("askejdknr"+System.currentTimeMillis()+""+Math.random()*400));
			this.session.setUser(this);
			Application.db.add(this.session);
			Controller.response().setCookie("sessid", this.session.getId());
			Controller.response().setCookie("sesskey", Application.sha1(this.session.getKey()));
			return true;
		}
		return false;
	}

	public String getId() {
		return id;
	}
	
	public void setId(String id){
		this.id = id;
	}
	
	public void setEmail(String email){
		this.email = email;
	}

	public String getName() {
		return name;
	}

	public String getPasswd() {
		return passwd;
	}

	public String getEmail() {
		return email;
	}

	public Date getTime() {
		return time;
	}

	public Session getSession() {
		return session;
	}
	
	public void setSession(Session session){
		this.session = session;
	}
	
	/*
	 * TODO
	 */
	@SuppressWarnings("unchecked")
	public List<ProcessInstance> getProcessInstances() {
		return Application.db.selectAll(ProcessInstance.class);
	}
	
	public ProcessInstance getCurrentProcessInstance() {
		if (this.currentProcessInstance == null) {
			List<ProcessInstance> processInstances = getProcessInstances();
			
			if (processInstances.size() > 0) {
				ProcessInstance firstProcess = processInstances.get(0);
				
				setCurrentProcessInstance(firstProcess);
				
				return firstProcess;
			}
		}
		return this.currentProcessInstance;
	}
	
	/*
	 * TODO
	 */
	public ProcessInstance createProcessInstance(ProcessModel processModel) {
		return ProcessInstance.create(this, processModel);
	}
	
	/*
	 * TODO
	 */
	public void archiveProcessInstance(ProcessInstance p) {
		
	}
}
