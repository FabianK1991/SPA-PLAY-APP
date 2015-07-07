package models.core.servers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import play.Logger;
import play.db.DB;

import models.core.exceptions.BusinessObjectInstanceNotFoundException;
import models.core.serverModels.businessObject.BusinessObject;
import models.core.serverModels.businessObject.BusinessObjectAttribute;
import models.core.serverModels.businessObject.BusinessObjectInstance;
import controllers.Application;

public class BusinessObjectServer {
	private Connection connection;
	
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
				args.set(i, args.get(i));
				i++;
			}
			secureQuery = String.format(query, args.toArray());
		}else{
			secureQuery = query;
		}

		try {
			Statement stmt = this.connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
			
			Logger.info(secureQuery.toString());
			
			if(read){
				ResultSet rs = stmt.executeQuery(secureQuery.toString());
				return rs;
			} else{
				stmt.execute(secureQuery.toString());
				return null;
			}

		} catch (Exception e) {
			Logger.info(e.toString());
			return null;
		}
	}
	
	/**
	 * Connects to the database.
	 * @author Fabian
	 * @return true if successfully connected, false otherwise.
	 */
	public boolean connect(){
		try {	
			if(this.connection != null && this.connection.isValid(0)){
				return true;
			}
			this.connection = DB.getConnection("app_mysql_sap");
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	/*
	 * Creates a new BO Instance in the database
	 * @author Fabian
	 * @param id The BO id
	 * @param values A map of the attributes
	 * @return the business object instance id
	 */
	public String createBusinessObjectInstance(String id, Map values){
		Application.db.connect();
		
		// retrieve our instance id
		String query = "SELECT MAX(id) FROM business_object_instances";
		
		ResultSet rs = Application.db.exec(query, null, true);
		
		int InstanceId = 1;
		
		try {
			if(rs.first()){
				for(int i = 1; i <= rs.getMetaData().getColumnCount(); i++){
					InstanceId = rs.getInt(1) + 1;
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// Create BO instance with id
		query = "INSERT INTO `business_object_instances` (`id`, `attribute`, `value`) VALUES ('%s', '%s', '%s')";
		
		ArrayList<String> args = new ArrayList<String>();
		args.add(Integer.toString(InstanceId));
		args.add("66"); // BUSINESS OBJECT ID
		args.add(id);
		
		// execute query
		Application.db.exec(query, args, false);
		
		// insert other attributes
		if( values != null ){
			Set keys = values.keySet();
			
			for(Object key : keys){
				String value = (String) values.get(key);
				
				query = "INSERT INTO `business_object_instances` (`id`, `attribute`, `value`) VALUES ('%s', '%s', '%s')";
				
				args = new ArrayList<String>();
				args.add(Integer.toString(InstanceId));
				args.add((String)key); // BUSINESS OBJECT ID
				args.add(value);
				
				// execute query
				Application.db.exec(query, args, false);
			}
		}
		
		return Integer.toString(InstanceId);
	}
	
	/*
	 * Retrieves the attributes ids of a business object
	 * @author Fabian
	 * @param id The BO id
	 * @return A list of attribute ids
	 */
	public List<String> getBusinessObjectAttributes(String id){
		List<String> resultList = new ArrayList<String>();

		Application.db.connect();
		
		String query = "SELECT id FROM business_object_properties WHERE business_object = '%s' ORDER BY `order` ASC";
		
		ArrayList<String> args = new ArrayList<String>();
		args.add(getBusinessObjectDatabaseId(id));
		
		ResultSet rs = Application.db.exec(query, args, true);
		
		try {
			while(rs.next()){
				Logger.info(rs.getString("id"));
				resultList.add(rs.getString("id"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return resultList;
	}
	
	public List<BusinessObjectInstance> getAllBusinessObjectInstances(BusinessObject bo){
		ArrayList<BusinessObjectInstance> returnList = new ArrayList<BusinessObjectInstance>();
		/*TODO: Fabi
		String sapId = Application.sss.getBusinessObjectDatabaseId(bo.getSAPId());
		
		ArrayList<String> args = new ArrayList<String>();
		args.add(sapId);
		
		String query = "SELECT * FROM `business_object_instance_attributes` WHERE `attribute` = '66' AND `value` = '%s'";
		
		ResultSet rs = Application.db.exec(query, args, true);
		
		try {
			while(rs.next()){
				returnList.add(new BusinessObjectInstance(rs.getString("bo_instance"), bo));
			}
			//validate code and fill return list;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (BusinessObjectInstanceNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
*/
		return returnList;
	}
	
	/*
	 * @author Fabian
	 * @param id the bo instance id in the database
	 * @param boa the businessobjectattribute to change
	 * @param value the value to set to the attribute
	 * @return
	 */
	public void setBusinessObjectAttribute(int id, BusinessObjectAttribute boa, String Value){
		Application.db.connect();
		
		// Check if attribute exists
		if( this.getBusinessObjectAttribute(id, boa) == null ){
			// INSERT
			String query = "INSERT INTO business_object_instances (id,attribute,value) VALUES ('%s','%s','%s')";
			
			ArrayList<String> args = new ArrayList<String>();
			args.add(String.valueOf(id));
			args.add(boa.getId());
			args.add(Value);
			
			Application.db.exec(query, args, false);
		}
		else{
			// UPDATE
			String query = "UPDATE business_object_instances SET value = '%s' WHERE id = '%s' AND attribute = '%s'";
			
			ArrayList<String> args = new ArrayList<String>();
			args.add(Value);
			args.add(String.valueOf(id));
			args.add(boa.getId());
			
			Application.db.exec(query, args, false);
		}
	}
	
	/*
	 * @author Fabian
	 * @param the bo instance id
	 * @param boa the object attribute
	 * @return the value of the attribute
	 */
	public String getBusinessObjectAttribute(int id, BusinessObjectAttribute boa){
		this.connect();
		
		
		
		/*TODO: Fabi
		Application.db.connect();
		
		String query = "SELECT value FROM business_object_instance_attributes WHERE bo_instance = '%s' AND attribute = '%s'";
		
		ArrayList<String> args = new ArrayList<String>();
		args.add(String.valueOf(id));
		args.add(boa.getId());
		
		ResultSet rs = Application.db.exec(query, args, true);
		
		try {
			if(rs.first()){
				return rs.getString("value");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		*/
		return null;
	}
	
	/*
	 * @author Fabian
	 * @param SAPName the business object sap name
	 * @return the value of the attribute
	 */
	public String getBusinessObjectDatabaseId(String SAPId){
		Application.db.connect();
		
		String query = "SELECT id FROM business_objects WHERE sap_id = '%s'";
		
		ArrayList<String> args = new ArrayList<String>();
		args.add(SAPId);
		
		ResultSet rs = Application.db.exec(query, args, true);
		
		try {
			if(rs.first()){
				return rs.getString("id");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	/*
	 * @author Fabian
	 * @param id the attribute id
	 * @return the name of the attribute
	 */
	public String getAttributeName(int AttributeId){
		Application.db.connect();
		
		String query = "SELECT name FROM business_object_properties WHERE id = '%s'";
		
		ArrayList<String> args = new ArrayList<String>();
		args.add(String.valueOf(AttributeId));
		
		ResultSet rs = Application.db.exec(query, args, true);
		
		try {
			if(rs.first()){
				return rs.getString("name");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	/*
	 * @author Fabian
	 * @param id the id of the business object instance
	 * @return
	 */
	public void deleteBusinessObjectInstance(int id){
		/*TODO: Fabi
		Application.db.connect();
		
		String query = "DELETE FROM business_object_instances WHERE id = '%s'";
		
		ArrayList<String> args = new ArrayList<String>();
		args.add(String.valueOf(id));
		
		Application.db.exec(query, args, false);
		*/
	}
}