package models.util.sessions;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import controllers.Application;

public class Session {
	public static Session o=null;
	private String id;
	private User user;
	private String key;
	private Date time;
	private Date update;
	
	public Session(){
	}
	
	/**
	 * Instantiates a new Session object and fills it automatically with the given attributes provided in the database.
	 * @author Christian
	 * @param id The id of the queue. 
	 */
	public Session(String id){
		Session.o = this;
		this.id = id;
		ArrayList<String> filling = Application.db.select(this, true);
		if(filling != null){
			this.user = new User(filling.get(1));
			this.key = filling.get(2);
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			try {
				this.time = format.parse(filling.get(3));
				this.update = format.parse(filling.get(4));
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
	}

	public String getId() {
		return id;
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

	public String getKey() {
		return key;
	}
	
	public void setKey(String key) {
		this.key = key;
	}

	public Date getTime() {
		return time;
	}

	public void setTime(Date time) {
		this.time = time;
	}

	public Date getUpdate() {
		return update;
	}
	
	public void setUpdate(Date update) {
		this.update = update;
	}
	


}
