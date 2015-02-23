package models.core;

import java.util.List;

import models.core.exceptions.ForbiddenBusinessObjectAttributeException;

public class BusinessObjectInstance {
	/*
	 * TODO
	 * Method to internally (PRIVATE method) create an empty BusinessObjectInstance
	 * Should be used only by static method BusinessObjectInstance.create()
	 */
	private BusinessObjectInstance() {
		
	}
	
	/*
	 * TODO
	 * Instantiates a BusinessObjectInstance
	 */
	public BusinessObjectInstance(String id) {
		
	}
	
	/*
	 * TODO
	 * Deletes a BusinessObjectInstance and removes all references to it stored in SPA
	 */
	public void delete() {
		
	}
	
	/*
	 * TODO
	 * Sets the given type of BusinessObjectAttribute to the given value,
	 * e.g. BusinessObjectInstance="BILL-1" => setAttrbuteValue(BusinessObjectAttribute('date'), new Date('2014-01-01'))
	 * => Values need to be saved in SPA, as the BusinessObjectInstance will not be persistent!
	 * 
	 * Should throw an exception if this attribute is not allowed for this kind of BusinessObject
	 */
	public void setAttribute(BusinessObjectAttribute attribute, Object value) throws ForbiddenBusinessObjectAttributeException {
		
	}
	
	/*
	 * TODO
	 * Returns the current value of the attribute passed to the method,
	 * e.g. BusinessObjectInstance="BILL-1" => getAttrbuteValue(BusinessObjectAttribute('date')) returns new Date('2014-01-01')
	 * => Values can be retrieved from SPA
	 * 
	 * Should return null if the attribute is not set
	 * Should throw an exception if this attribute is not allowed for this kind of BusinessObject
	 */
	public Object getAttributeValue(BusinessObjectAttribute attribute) throws ForbiddenBusinessObjectAttributeException {
		return null;
	}
	
	/*
	 * TODO
	 * Returns the "type of" BusinessObject (e.g. BusinessObjectInstance="Bill-1" returns BusinessObject("bill"))
	 */
	public BusinessObject getBusinessObject() {
		return null;
	}
	
	/*
	 * TODO
	 * Returns a list of documents related to this BusinessObjectInstance,
	 * e.g. BusinessObjectInstance="BILL-1" (BusinessObject="Bill") and documents like PDF-bill,...
	 */
	public List<Document> getDocuments() {
		return null;
	}
	
	/*
	 * TODO
	 * Creates and returns BusinessObjectInstance referencing the "template" of a BusinessObject,
	 * e.g. BusinessObjectInstance.create(new BusinessObject('bill'))
	 */
	public static BusinessObjectInstance create(BusinessObject businessObject) {
		return null;
	}
}
