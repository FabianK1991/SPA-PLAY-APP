package models.util.db;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import models.core.ProcessInstance;
import models.core.exceptions.ProcessInstanceNotFoundException;
import models.util.sessions.Session;
import models.util.sessions.User;
import play.Logger;
import play.db.DB;
import controllers.Application;
import controllers.AuthController;

public class DBHandler {
	
	private Connection connection;
	
	
	public static SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	public DBHandler() {
		this.connect();
	}
	
	/**
	 * Connects to the database.
	 * @author Christian
	 * @return true if successfully connected, false otherwise.
	 */
	public boolean connect(){
		try {	
			if(this.connection != null && this.connection.isValid(0)){
				return true;
			}
			this.connection = DB.getConnection();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * Disconnect for the database.
	 * @author Christian
	 * @return True if disconnected successfully, false otherwise.
	 */
	public boolean disconnect(){
		if (this.connection != null){
			try {
				this.connection.close();
				return true;
			}catch (Exception e){
				return false;
			}
		}
		return true;
	}
	
	/**
	 * Selects a single row for a given id of an Object.
	 * @author Christian
	 * @param o object of either user,session, TODO
	 * @param mode if o = user then if mode = true gets user by id else if mode = false gets user by email
	 * @return A list of strings filled with the rows.
	 */
	public ArrayList<String> select(Object o, boolean mode){
		ArrayList<String> re = new ArrayList<String>();
		ArrayList<String> args = new ArrayList<String>();
		
		String query = "";
		
		if(this.connect()){
			if(o instanceof User){
				query = "SELECT * FROM `users` WHERE `%s` = '%s' LIMIT 1";
				if(mode == false){
					args.add("mail");
					args.add(((User) o).getEmail());
				}
				else {
					args.add("id");
					args.add(((User) o).getId());
				}
			}else if (o instanceof Session){
				query = "SELECT * FROM `user_sessions` WHERE `id` = '%s' LIMIT 1";
				args.add(((Session) o).getId());
			}else if(o instanceof ProcessInstance){
				query = "SELECT * FROM `user_process_instances` WHERE `user`= '%s' ORDER BY `time` DESC LIMIT 1";
				args.add(AuthController.getUser().getId());
			}
			ResultSet rs = this.exec(query, args, true);
			try {
				if(rs.first()){
					for(int i = 1; i <= rs.getMetaData().getColumnCount(); i++){
						re.add(rs.getString(i));
					}
				}else{
					return null;
				}
				
				
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return re;
	}
	
	@SuppressWarnings("rawtypes")//For compatibility with Scala Template Engine.
	/**
	 * @author Christian
	 * @param c the class for which everything is selected; applicable for ProcessInstance,...
	 * @param mode for process instance if true it is only selected of the current user, if false every process instance is selected.
	 * @return
	 */
	public ArrayList selectAll(Class c, boolean mode){
		ArrayList<Object> reObj = new ArrayList<Object>();
		if(this.connect()){
			String query = "";
			if(c.equals(ProcessInstance.class)){
				if(mode){
					User u = AuthController.getUser();
					String userid = u.getId();
					query = "SELECT * FROM `user_process_instances` WHERE `user` = '"+ userid +"' ORDER BY `time`";
				}else{
					query = "SELECT * FROM `user_process_instances` ORDER BY `time`";
				}		
			}
			ResultSet rs = this.exec(query, null, true);
			try {
				while(rs.next()){
					if(c.equals(ProcessInstance.class)){
						ProcessInstance pi = new ProcessInstance(rs.getString("id"));
						pi.setUser(new User(rs.getString("user")));
						pi.setProcess(rs.getString("process"));
						try {
							pi.setTime(DBHandler.format.parse(rs.getString("timestamp")));
						} catch (ParseException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						reObj.add(pi);
					}
				}
			} catch (SQLException | ProcessInstanceNotFoundException e) {
				e.printStackTrace();
			}
		}
		return reObj;
	}
	
	@SuppressWarnings("rawtypes")
	public ArrayList selectAll(Class c){
		return selectAll(c, true);
	}
	
	/**
	 * Adds an Object to the database.
	 * @author Christian
	 * @param o Object of either User or Session.
	 * @return True if stored successfully, false otherwise.
	 */
	public boolean add(Object o){
		if(this.connect()){
			
			String query="";
			ArrayList<String> args = new ArrayList<String>();
			if(o instanceof User){
				((User) o).setId(Application.sha1(""+System.currentTimeMillis()).substring(0, 8));
				query = "INSERT INTO `users` (`id`, `name`, `mail`, `passwd`, `time`, `session`) VALUES ( '%s', '%s', '%s', '%s', '%s', '%s')";
				args.add(((User) o).getId());
				args.add(((User) o).getName());
				args.add(((User) o).getEmail());
				args.add(((User) o).getPasswd());
				args.add(DBHandler.format.format(((User) o).getTime()).toString());
				args.add(((User) o).getSession().getId());
			}else if(o instanceof Session){
				((Session) o).setId(Application.sha1(""+System.currentTimeMillis()).substring(0, 8));
				query = "INSERT INTO `user_sessions` (`id`, `user`, `key`, `time`, `update`) VALUES ('%s', '%s', '%s', '%s', '%s')";
				args.add(((Session) o).getId());
				args.add(((Session) o).getUser().getId());
				args.add(((Session) o).getKey());
				args.add(DBHandler.format.format(((Session) o).getTime()).toString());
				args.add(DBHandler.format.format(((Session) o).getUpdate()).toString());
			}else if(o instanceof ProcessInstance){
				query = "INSERT INTO `user_process_instances` (`user`, `process`) VALUES ('%s', '%s')";
				args.add(((ProcessInstance) o).getUser().getId());
				args.add(((ProcessInstance) o).getProcess());
			}
			this.exec(query, args, false);
			return true;
		}
		return false;
	}
	
	/**
	 * Deletes a given Object from the database.
	 * @author Christian
	 * @param o Object of either Queue or Session.
	 * @return True if deleted successfully, false otherwise.
	 */
	public boolean delete(Object o){
		if(this.connect()){
			String query = "DELETE FROM `%s` WHERE `id` = '%s'";
			ArrayList<String> args = new ArrayList<String>();
			if(o instanceof Session){
				args.add("session");
				args.add(((Session) o).getId());
			}
			this.exec(query, args, false);
			return true;
		}
		return false;	
	}
	
	/**
	 * Updates the a User or Session stored in the Database.
	 * @author Christian
	 * @param o Either a User or a Session Object.
	 * @param prop The property to be updated.
	 * @param value The value for the property.
	 * @return True if updated successfully, false otherwise.
	 */
	public boolean update(Object o, String prop, String value){
		if(this.connect()){
			String query = "UPDATE `%s` SET `%s`='%s' WHERE `id`='%s'";
			ArrayList<String> args = new ArrayList<String>();
			if(o instanceof Session){
				args.add("session");
				args.add(prop);
				args.add(value);
				args.add(((Session) o).getId());
			}
			if(o instanceof User){
				args.add("user");
				args.add(prop);
				args.add(value);
				args.add(((User) o).getId());
			}
			this.exec(query, args, false);
			return true;
		}
		return false;
	}
	
	public boolean archive(Object o){
		if(this.connect()){
			String query = "";
			ArrayList<String> args = new ArrayList<String>();
			if(o instanceof ProcessInstance){
				query = "UPDATE `user_process_instances` SET `archive` = TRUE WHERE `user`= '%s' AND `process` = '%s'";
				args.add(AuthController.getUser().getId());
				args.add(((ProcessInstance) o).getId());
			}
			this.exec(query, args, false);
			return true;
		}
		return false;
	}
	
	
	/**
	 * Exectues a given query on the database.
	 * @author Christian
	 * @param query The query to be executed.
	 * @param args A list of arguments that replace the place holders in the queryString.
	 * @param read Read something from the database with read = true. Write something with read = false.
	 * @return
	 */
	public ResultSet exec(String query, ArrayList<String> args, boolean read) {
		String secureQuery;
		if(args != null){
			int i = 0;
			while(i < args.size()) {
				args.set(i, this.secure(args.get(i)));
				i++;
			}
			secureQuery = String.format(query, args.toArray());
		}else{
			secureQuery = query;
		}

		try {
			Statement stmt = this.connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
			
			Logger.debug(secureQuery.toString());
			
			if(read){
				ResultSet rs = stmt.executeQuery(secureQuery.toString());
				return rs;
			} else{
				stmt.execute(secureQuery.toString());
				return null;
			}

		} catch (Exception e) {
			Logger.debug(e.toString());
			return null;
		}
	}
	
	private String secure(String o) {
		return o;
	}
}

class Encoding {
    
	private final static String charset = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/";
 
    public static String decode(String s) {
    	s = s.replaceAll("[^" + charset + "=]", "");
     
    	String p = (s.charAt(s.length() - 1) == '=' ? (s.charAt(s.length() - 2) == '=' ? "AA" : "A") : "");
    	String r = "";
    	s = s.substring(0, s.length() - p.length()) + p;
     
    	for (int c = 0; c < s.length(); c += 4) {
    	    int n = (charset.indexOf(s.charAt(c)) << 18) + (charset.indexOf(s.charAt(c + 1)) << 12)
    		    + (charset.indexOf(s.charAt(c + 2)) << 6) + charset.indexOf(s.charAt(c + 3));

    	    r += "" + (char) ((n >>> 16) & 0xFF) + (char) ((n >>> 8) & 0xFF) + (char) (n & 0xFF);
    	}
    
    	return r.substring(0, r.length() - p.length());
    }
}