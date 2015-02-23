package models.core;

import java.util.List;

public class BusinessObject {
	/*
	 * TODO
	 * Returns an ordered List of BusinessObjectAttributes that are allowed for this BusinessObject
	 * A List of allowed BusinessObjectAttributes for every BusinessObject can be retrieved from
	 * database "business_object_attributes" (columns: id, business_object, attribute, order)
	 * => List should be ordered ASCending by column order
	 */
	public List<BusinessObjectAttribute> getAttributes() {
		return null;
	}
	
	/*
	 * TODO
	 * Returns the minimum number of BusinessObjects that need to be defined in a ActivityInstance
	 */
	public int getMinQuantity() {
		return 0;
	}
	
	/*
	 * TODO
	 * Returns the maximum number of BusinessObjects that can be defined in a ActivityInstance
	 */
	public int getMaxQuantity() {
		return 0;
	}
}
