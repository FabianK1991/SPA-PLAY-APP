package models.core;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import models.core.exceptions.BusinessObjectInstanceNotFoundException;
import models.core.exceptions.ForbiddenBusinessObjectAttributeException;
import models.util.parsing.ProcessParser;
import controllers.Application;

public class BusinessObjectInstance {
	private ActivityInstance ai;
	private BusinessObject bo;
	private models.spa.api.process.buildingblock.instance.BusinessObjectInstance boi;
	
	// the id of the instance in the SAP database
	private int databaseId;
	
	/*
	 * Method to internally (PRIVATE method) create an empty BusinessObjectInstance
	 * Should be used only by static method BusinessObjectInstance.create()
	 */
	private BusinessObjectInstance(BusinessObject bo, ActivityInstance ai) {
		this.ai = ai;
		this.bo = bo;
		
		this.boi = new models.spa.api.process.buildingblock.instance.BusinessObjectInstance(this.ai.getSPAActivityInstance().getPi());
		
		// Database SAP NAME NEEDED!!
		this.databaseId = Integer.parseInt(Application.sss.createBusinessObjectInstance(Application.sss.getBusinessObjectDatabaseId(bo.getSAPId()), null));
		
		this.boi.setId(ProcessParser.nsboi + this.databaseId);
		this.boi.setBusinessObject(bo.getId());
		
		// Add to spa model
		this.ai.getSPAActivityInstance().getBoi().add(this.boi);
		
		// update?
		try {
			this.ai.getSPAActivityInstance().getPi().update();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/*
	 * Instantiates a BusinessObjectInstance
	 */
	public BusinessObjectInstance(String id, ActivityInstance ai) throws BusinessObjectInstanceNotFoundException {
		this.ai = ai;
		
		Set<models.spa.api.process.buildingblock.instance.BusinessObjectInstance> list = ai.getSPAActivityInstance().getBoi();
		
		for(models.spa.api.process.buildingblock.instance.BusinessObjectInstance SPABoi: list ){
			if( SPABoi.getId().equals(id) ){
				this.boi = SPABoi;
				break;
			}
		}
		
		if(this.boi == null){
			throw new BusinessObjectInstanceNotFoundException();
		}
	
		this.bo = ai.getActivity().getBusinessObjectById(this.boi.getBusinessObject());
		
		// 
		this.databaseId = Integer.parseInt(this.boi.getId().substring(ProcessParser.nsboi.length()));
	}
	
	public BusinessObjectInstance(String databaseId, BusinessObject bo) throws BusinessObjectInstanceNotFoundException {
		this.bo = bo;
		this.databaseId = Integer.parseInt(databaseId);
	}
	
	/*
	 * Deletes a BusinessObjectInstance and removes all references to it stored in SPA
	 */
	public void delete() {
		Set<models.spa.api.process.buildingblock.instance.BusinessObjectInstance> list = ai.getSPAActivityInstance().getBoi();
		list.remove(this.boi);
		
		// Update ProcessInstance?
		try {
			this.ai.getSPAActivityInstance().getPi().update();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		// Delete in database?
		Application.sss.deleteBusinessObjectInstance(this.databaseId);
	}
	
	private boolean isAttributeAllowed(String name){
		String[] nA = this.bo.getNeededAttributes();
		
		if( nA == null || nA.length == 0 ){
			return true;
		}
		
		for(String a : nA){
			if(a.equals(name)){
				return true;
			}
		}
		
		return false;
	}
	
	/*
	 * Sets the given type of BusinessObjectAttribute to the given value,
	 * e.g. BusinessObjectInstance="BILL-1" => setAttrbuteValue(BusinessObjectAttribute('date'), new Date('2014-01-01'))
	 * => Values need to be saved in SPA, as the BusinessObjectInstance will not be persistent!
	 * 
	 * Should throw an exception if this attribute is not allowed for this kind of BusinessObject
	 */
	public void setAttribute(BusinessObjectAttribute attribute, Object value) throws ForbiddenBusinessObjectAttributeException {
		if(this.isAttributeAllowed(attribute.getName())){
			Application.sss.setBusinessObjectAttribute(this.databaseId, attribute, (String)value);
		}
		else{
			throw new ForbiddenBusinessObjectAttributeException();
		}
	}
	
	/*
	 * Returns the current value of the attribute passed to the method,
	 * e.g. BusinessObjectInstance="BILL-1" => getAttrbuteValue(BusinessObjectAttribute('date')) returns new Date('2014-01-01')
	 * => Values can be retrieved from SPA
	 * 
	 * Should return null if the attribute is not set
	 * Should throw an exception if this attribute is not allowed for this kind of BusinessObject
	 */
	public Object getAttributeValue(BusinessObjectAttribute attribute) throws ForbiddenBusinessObjectAttributeException {
		if(this.isAttributeAllowed(attribute.getName())){
			return Application.sss.getBusinessObjectAttribute(this.databaseId, attribute);
		}
		else{
			throw new ForbiddenBusinessObjectAttributeException();
		}
	}
	
	/*
	 * Returns the "type of" BusinessObject (e.g. BusinessObjectInstance="Bill-1" returns BusinessObject("bill"))
	 */
	public BusinessObject getBusinessObject() {
		return this.bo;
	}
	
	/*
	 * Be careful this.databaseId and this.bo have to be set!!!
	 */
	public void linkToActivityInstance(ActivityInstance ai){
		this.boi = new models.spa.api.process.buildingblock.instance.BusinessObjectInstance(this.ai.getSPAActivityInstance().getPi());
		
		this.boi.setId(ProcessParser.nsboi + this.databaseId);
		this.boi.setBusinessObject(this.bo.getId());
		
		this.ai.getSPAActivityInstance().getBoi().add(this.boi);
		
		try {
			this.ai.getSPAActivityInstance().getPi().update();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/*
	 * Returns a list of documents related to this BusinessObjectInstance,
	 * e.g. BusinessObjectInstance="BILL-1" (BusinessObject="Bill") and documents like PDF-bill,...
	 */
	public List<Document> getDocuments() {
		return null;
	}
	
	/*
	 * Creates and returns BusinessObjectInstance referencing the "template" of a BusinessObject,
	 * e.g. BusinessObjectInstance.create(new BusinessObject('bill'))
	 */
	public static BusinessObjectInstance create(ActivityInstance ai, BusinessObject businessObject) {
		return new BusinessObjectInstance(businessObject, ai);
	}
	
	private static String getUID() {
		String id = UUID.randomUUID().toString().replace('-', '0');
		
		return id;
	}
	
	/*
	 */
	public static List<BusinessObjectInstance> getAll(BusinessObject bo) {
		return Application.sss.getAllBusinessObjectInstances(bo);
	}
}
