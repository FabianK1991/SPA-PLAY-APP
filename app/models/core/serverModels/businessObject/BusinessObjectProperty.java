package models.core.serverModels.businessObject;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import controllers.Application;

public class BusinessObjectProperty {
	private String id;
	private String name;
	
	private BusinessObject bo;
	
	/*
	 * Instantiates a BusinessObjectAttribute
	 * All available BusinessObjectAttributes can be found in database "attributes" (columns: id, name)
	 */
	public BusinessObjectProperty(String id, BusinessObject bo) {
		this.name = id;
		this.bo = bo;
	}
	
	/*
	 * Returns the database id
	 */
	public String getId(){
		if( this.id == null ){
			String query = "SELECT id FROM business_object_properties WHERE business_object = '%s' AND name = '%s'";
			
			ArrayList<String> args = new ArrayList<String>();
			args.add(this.bo.getDBId());
			args.add(this.name);
			
			ResultSet rs = Application.db.exec(query, args, true);
			
			try {
				if(rs.next()){
					this.id = rs.getString("id");
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		return this.id;
	}
	
	/*
	 * Returns the name of the BusinessObjectAttribute
	 * 
	 */
	public String getName() {
		/*if(this.name == null){
			this.name = Application.sss.getAttributeName(Integer.parseInt(this.id));
		}*/
		
		return this.name;
	}
	
	public BusinessObject getBo() {
		return this.bo;
	}
}
