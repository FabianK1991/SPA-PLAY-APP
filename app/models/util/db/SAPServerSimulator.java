package models.util.db;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import models.core.BusinessObject;
import models.core.BusinessObjectAttribute;
import models.core.BusinessObjectInstance;
import models.core.exceptions.BusinessObjectInstanceNotFoundException;
import controllers.Application;

public class SAPServerSimulator {
	
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
		String query = "SELECT MAX(id) FROM business_objects_instances";
		
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
		query = "INSERT INTO `business_objects_instances` (`id`, `attribute`, `value`) VALUES ('%s', '%s', '%s')";
		
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
				
				query = "INSERT INTO `business_objects_instances` (`id`, `attribute`, `value`) VALUES ('%s', '%s', '%s')";
				
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
		Application.db.connect();
		
		String query = "SELECT attribute FROM business_object_attributes WHERE business_object = '%s' ORDER BY `order` ASC";
		
		ArrayList<String> args = new ArrayList<String>();
		args.add(getBusinessObjectDatabaseId(id));
		
		List<String> resultList = new ArrayList<String>();
		ResultSet rs = Application.db.exec(query, args, true);
		
		try {
			while(rs.next()){
				resultList.add(rs.getString("attribute"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return resultList;
	}
	
	public List<BusinessObjectInstance> getAllBusinessObjectInstances(BusinessObject bo){
		String sapId = Application.sss.getBusinessObjectDatabaseId(bo.getSAPId());
		
		ArrayList<String> args = new ArrayList<String>();
		args.add(sapId);
		
		String query = "SELECT * FROM `business_objects_instance_attributes` WHERE `attribute` = '66' AND `value` = '%s'";
		
		ResultSet rs = Application.db.exec(query, args, true);
		ArrayList<BusinessObjectInstance> returnList = new ArrayList<BusinessObjectInstance>();
		
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
			String query = "INSERT INTO business_objects_instances (id,attribute,value) VALUES ('%s','%s','%s')";
			
			ArrayList<String> args = new ArrayList<String>();
			args.add(String.valueOf(id));
			args.add(boa.getId());
			args.add(Value);
			
			Application.db.exec(query, args, false);
		}
		else{
			// UPDATE
			String query = "UPDATE business_objects_instances SET value = '%s' WHERE id = '%s' AND attribute = '%s'";
			
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
		Application.db.connect();
		
		String query = "SELECT value FROM business_objects_instances WHERE id = '%s' AND attribute = '%s'";
		
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
		
		String query = "SELECT name FROM attributes WHERE id = '%s'";
		
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
		Application.db.connect();
		
		String query = "DELETE FROM business_objects_instances WHERE id = '%s'";
		
		ArrayList<String> args = new ArrayList<String>();
		args.add(String.valueOf(id));
		
		Application.db.exec(query, args, false);
	}
}