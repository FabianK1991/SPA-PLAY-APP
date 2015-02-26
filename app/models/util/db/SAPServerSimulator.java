package models.util.db;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import controllers.Application;

public class SAPServerSimulator {
	
	/*
	 * Creates a new BO Instance in the database
	 * @author Fabian
	 * @param id The BO id
	 * @param values A map of the attributes
	 * @return
	 */
	public boolean createBusinessObjectInstance(int id, Map values){
		Application.db.connect();
		
		// retrieve our instance id
		String query = "SELECT MAX(business_object) FROM business_objects_data";
		
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
		query = "INSERT INTO `business_objects_data` (`business_object`, `attribute`, `value`) VALUES ('%s', '%s', '%s')";
		
		ArrayList<String> args = new ArrayList<String>();
		args.add(Integer.toString(InstanceId));
		args.add("66"); // BUSINESS OBJECT ID
		args.add(Integer.toString(id));
		
		// execute query
		Application.db.exec(query, args, false);
		
		// TODO: Inser other attributes
		
		return true;
	}
	
	
	/*
	 * Creates a new BO Instance in the database
	 * @author Fabian
	 * @param id The BO instance id
	 * @param Attributes the attributes which should be shown
	 * @return A map of attributes and values
	 */
	public Map getBussinesObjectInstance(int id, List<Integer> Attributes){
		return null;
	}
	
	/*
	 * Creates a new BO Instance in the database
	 * @author Fabian
	 * @param id the attribute id
	 * @return the name of the attribute
	 */
	public String getAttributeName(int AttributeId){
		return null;
	}
	
	/*
	 * Creates a new BO Instance in the database
	 * @author Fabian
	 * @param Attributes the attributes with values to search for
	 * @return a list of business object ids
	 */
	public List<Integer> searchBusinessObjectInstances(Map Attributes){
		return null;
	}
}