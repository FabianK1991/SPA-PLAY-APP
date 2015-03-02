package models.util.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import play.Logger;
import controllers.Application;
import controllers.AuthController;
import models.core.ProcessInstance;
import models.core.exceptions.ProcessInstanceNotFoundException;
import models.util.sessions.Session;
import models.util.sessions.User;

public class DBHandler {
	
	/*
	 * MySQL-Connection information
	 */
	private String host 	= "";
	private int    port 	= 8082;
	private String path 	= "./data_mtp_spa_app";
	private String user 	= "mtp_spa_app"; 
	private String passwd	= "crasuhacru-EC3mewa*ep"; 
	private String Driver 	= "org.h2.Driver";
	private Connection connection;
	
	
	public static SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	public DBHandler(){
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
			
			Class.forName(this.Driver);
			/*
			 * line 1 for MySql, line 2 for h2
			 */ 
//			this.connection = DriverManager.getConnection(this.Driver + this.host + ":" + this.port + "/" + this.path, this.user, this.passwd);
			this.connection = DriverManager.getConnection("jdbc:h2:./mtp_spa_app_data", this.user, this.passwd);
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
	 * Selects all rows for a given id of an Object.
	 * @author Christian
	 * @param o TODO
	 * @return A list of strings filled with the rows.
	 */
	public ArrayList<String> select(Object o, boolean mode){
		ArrayList<String> re = new ArrayList<String>();
		ArrayList<String> args = new ArrayList<String>();
		
		String query = "";
		
		if(this.connect()){
			if(o instanceof User){
				query = "SELECT * FROM `user` WHERE `%s` = '%s'";
				if(mode == false){
					args.add("email");
					args.add(((User) o).getEmail());
				}
				else {
					args.add("id");
					args.add(((User) o).getId());
				}
			}else if (o instanceof Session){
				query = "SELECT * FROM `session` WHERE `id` = '%s'";
				args.add(((Session) o).getId());
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
	public ArrayList selectAll(Class c){
		ArrayList<Object> reObj = new ArrayList<Object>();
		if(this.connect()){
			String query = "";
			if(c.equals(ProcessInstance.class)){
				User u = AuthController.getUser();
				String userid = u.getId();
				query = "SELECT * FROM `user_process_instances` WHERE `user` = '"+ userid +"' ORDER BY `time`";
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
				query = "INSERT INTO `user` (`id`, `name`, `email`, `passwd`, `time`, `session`) VALUES ( '%s', '%s', '%s', '%s', '%s', '%s')";
				args.add(((User) o).getId());
				args.add(((User) o).getName());
				args.add(((User) o).getEmail());
				args.add(((User) o).getPasswd());
				args.add(""+((User) o).getTime());
				args.add(((User) o).getSession().getId());
			}else if(o instanceof Session){
				((Session) o).setId(Application.sha1(""+System.currentTimeMillis()).substring(0, 8));
				query = "INSERT INTO `session` (`id`, `user`, `key`, `time`, `update`) VALUES ('%s', '%s', '%s', '%s', '%s')";
				args.add(((Session) o).getId());
				args.add(((Session) o).getUser().getId());
				args.add(((Session) o).getKey());
				args.add(""+((Session) o).getTime());
				args.add(""+((Session) o).getUpdate());
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

			if(read){
				ResultSet rs = stmt.executeQuery(secureQuery.toString());
				return rs;
			} else{
				stmt.execute(secureQuery.toString());
				return null;
			}

		} catch (Exception e) {
			System.err.println(e.toString());
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