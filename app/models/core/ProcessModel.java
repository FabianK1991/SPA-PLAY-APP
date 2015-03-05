package models.core;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import javax.xml.parsers.DocumentBuilderFactory;

import controllers.Application;
import models.core.exceptions.ActivityInstanceNotFoundException;
import models.core.exceptions.IncorrectNumberOfProcessModelsExeption;
import models.core.exceptions.ProcessInstanceNotFoundException;
import models.core.exceptions.ProcessModelNotFoundException;
import models.util.parsing.ProcessParser;

public class ProcessModel {
	models.spa.api.ProcessModel pm;
	private static final String xmlPath = "public/processes/";
	
	// Maps activities to bos
	/* TABLE FORMAT CREATE TABLE DataAssociationBusinessObjects (
		ProcessModelId VARCHAR(128),
		ActivityId VARCHAR(128),
		BoId VARCHAR(128),
		BoSAPId VARCHAR(32),
		BoAction VARCHAR(32),
		BoMin VARCHAR(16),
		BoMax VARCHAR(16),
		BoName VARCHAR(128),
		BoneededAttributes VARCHAR(255),
		RID int(11) NOT NULL auto_increment, primary KEY (RID));
	*/
	public List<DataAssociation> dataAssoc;
	public List<BusinessObject> bos;
	
	/*
	 * Method to internally (PRIVATE method) create an empty ProcessModel
	 * Should be used only by static method ProcessModel.createFromBPMN_File()
	 */
	private ProcessModel() {
		this.pm = new models.spa.api.ProcessModel();
		this.dataAssoc = new ArrayList<DataAssociation>();
		this.bos = new ArrayList<BusinessObject>();
	}
	
	/*
	 * Instantiates a ProcessModel object by the given ID
	 * 
	 * >> Needs to SEARCH in SPA for a ProcessModel with the given ID <<
	 * >> This process model (already existing!) needs to be instantiated, not a new one! <<
	 */
	public ProcessModel(String id) throws ProcessModelNotFoundException {
		try {
			this.pm = models.spa.api.ProcessModel.getProcess(id);
			this.dataAssoc = new ArrayList<DataAssociation>();
			this.bos = new ArrayList<BusinessObject>();
			
			this.loadBusinessObjectDataAssociations();
		} catch (Exception e) {
			throw new ProcessModelNotFoundException();
		}
	}
	
	// Needed for ProcessInstance Constructor
	public ProcessModel(models.spa.api.ProcessModel pm) {
		this.pm = pm;
		this.dataAssoc = new ArrayList<DataAssociation>();
		this.bos = new ArrayList<BusinessObject>();
		
		this.loadBusinessObjectDataAssociations();
	}
	
	/*
	 * Returns the BPMN XML file locally stored by the static method ProcessModel.createFromBPMN_File()
	 */
	public File getBPMN_XML() {
		return new File(xmlPath + this.pm.getId() + ".bpmn");
	}
	
	public String getId(){
		return this.pm.getId();
	}
	
	/*
	 * Returns the name of this ProcessModel
	 */
	public String getName() {
		return this.pm.getName();
	}
	
	/*
	 * Returns a node by id from the spa process model
	 */
	public models.spa.api.process.buildingblock.Node getSPANodeById(String id){
		Set<models.spa.api.process.buildingblock.Node> nodes = this.pm.getNodes();
		
		for(models.spa.api.process.buildingblock.Node n : nodes){
			if( n.getId().equals(id) ){
				return n;
			}
		}
		
		return null;
	}
	
	public models.spa.api.ProcessModel getSPAProcessModel(){
		return this.pm;
	}
	
	public String getActionForActivity(String id){
		for(DataAssociation da : this.dataAssoc){
			if( da.activityId.equals(id) ){
				// Search for bo
				for(BusinessObject bo : this.bos){
					if( bo.getId().equals(da.boId) ){
						return bo.getAction();
					}
				}
			}
		}
		
		return null;
	}
	
	public List<BusinessObject> getBosForActivity(String id){
		List<BusinessObject> resultBos = new ArrayList<BusinessObject>();
		
		for(DataAssociation da : this.dataAssoc){
			if( da.activityId.equals(id) ){
				// Search for bo
				for(BusinessObject bo : this.bos){
					if( bo.getId().equals(da.boId) ){
						resultBos.add(bo);
					}
				}
			}
		}
		
		return resultBos;
	}
	
	/*
	 * Returns all process models stored in the SPA
	 */
	public static List<ProcessModel> getAll() {
		List<models.spa.api.ProcessModel> pms;
		List<ProcessModel> resultList = new ArrayList<ProcessModel>();
		
		try {
			pms = models.spa.api.ProcessModel.getAllProcesses();
			
			for( models.spa.api.ProcessModel pm : pms ){
				resultList.add(new ProcessModel(pm));
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return resultList;
	}
	
	/*
	 * Receives a BPMN XML file containing a process model with annotated business objects,
	 * stores this file as /processes/[ProcessModel:ID].bpmn
	 * and parses the XML to create a ProcessModel in the SPA.
	 * The created ProcessModel instance needs to be returned.
	 */
	public static ProcessModel createFromBPMN_File(File file) {
		// Init model and parser
		ProcessModel newProcessModel = new ProcessModel();
		ProcessParser pp = new ProcessParser(newProcessModel);
		
		// Parse model
		try {
			pp.parseXML(file);
		} catch (IncorrectNumberOfProcessModelsExeption e1) {
			e1.printStackTrace();
		}
		
		// Save to file
		File f = new File(xmlPath + newProcessModel.getId() + ".bpmn");
		
		// Check if file exists, otherwise write it
		if( !f.exists() ){
			try {
				Files.copy(file.toPath(), f.toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		// persist bos in spa model and database
		newProcessModel.addBusinessObjectsToSpaModel();
		newProcessModel.persistBusinessObjectDataAssociations();
		
		// save process model
		try {
			newProcessModel.getSPAProcessModel().delete();
			newProcessModel.getSPAProcessModel().store();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return newProcessModel;
	}
	
	private BusinessObject getBoById(String id){
		for(BusinessObject bo: this.bos){
			if ( bo.getId().equals(id) ){
				return bo;
			}
		}
		
		return null;
	}
	
	private void addBusinessObjectsToSpaModel(){
		for(DataAssociation da : this.dataAssoc){
			BusinessObject currentBo = this.getBoById(da.boId);
			
			models.spa.api.process.buildingblock.BusinessObject SPABo = new models.spa.api.process.buildingblock.BusinessObject(this.getSPAProcessModel());
			SPABo.setId(currentBo.getId());
			SPABo.setName(currentBo.getName());
			
			this.getSPANodeById(da.activityId).getBusinessObjects().add(SPABo);
		}
	}
	
	private void loadBusinessObjectDataAssociations(){
		Application.db.connect();
		String query = "SELECT * FROM DataAssociationBusinessObjects WHERE ProcessModelId = '%s'";
		
		ArrayList<String> args = new ArrayList<String>();
		
		args.add(this.getId());
		
		ResultSet rs = Application.db.exec(query, args, true);
		
		try {
			if(rs.first()){
				do{
					this.dataAssoc.add(new DataAssociation(rs.getString("ActivityId"), rs.getString("BoId")));
					
					BusinessObject bo = this.getBoById(rs.getString("BoId"));
					
					if( bo == null ){
						bo = new BusinessObject(rs.getString("BoId"));
						
						if( rs.getString("BoSAPId").length() > 0 ){
							bo.setSAPId(rs.getString("BoSAPId"));
						}
						if( rs.getString("BoAction").length() > 0 ){
							bo.setAction(rs.getString("BoAction"));
						}
						if( rs.getString("BoMin").length() > 0 ){
							bo.setMin(rs.getString("BoMin"));
						}
						if( rs.getString("BoMax").length() > 0 ){
							bo.setMax(rs.getString("BoMax"));
						}
						if( rs.getString("BoName").length() > 0 ){
							bo.setName(rs.getString("BoName"));
						}
						if( rs.getString("BoneededAttributes").length() > 0 ){
							bo.setNeededAttributes(rs.getString("BoneededAttributes").split(","));
						}
						
						this.bos.add(bo);
					}
				} while(rs.next());
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return;
		}
	}
	
	/* TABLE FORMAT CREATE TABLE DataAssociationBusinessObjects (
		ProcessModelId VARCHAR(128),
		ActivityId VARCHAR(128),
		BoId VARCHAR(128),
		BoSAPId VARCHAR(32),
		BoAction VARCHAR(32),
		BoMin VARCHAR(16),
		BoMax VARCHAR(16),
		BoName VARCHAR(128),
		BoneededAttributes VARCHAR(255),
		RID int(11) NOT NULL auto_increment, primary KEY (RID));
	*/
	private void persistBusinessObjectDataAssociations(){
		Application.db.connect();
		
		for(DataAssociation da : this.dataAssoc){
			BusinessObject currentBo = this.getBoById(da.boId);
			
			// Check if already in database
			String query = "SELECT * FROM DataAssociationBusinessObjects WHERE ProcessModelId = '%s' AND ActivityId = '%s' AND BoId = '%s'";
			ArrayList<String> args = new ArrayList<String>();
			
			args.add(this.getId());
			args.add(da.activityId);
			args.add(da.boId);
			
			ResultSet rs = Application.db.exec(query, args, true);
			
			try {
				args.clear();

				if(rs.first()){
					// Already exists
					query = "UPDATE DataAssociationBusinessObjects SET BoSAPId = '%s',BoAction = '%s',BoMin = '%s',BoMax = '%s',BoName = '%s',BoneededAttributes = '%s' WHERE ProcessModelId = '%s' AND ActivityId = '%s' AND BoId = '%s'";
					
					if( currentBo.getSAPId() != null ){
						args.add(currentBo.getSAPId());
					}
					else{
						args.add("");
					}
					
					if( currentBo.getAction() != null ){
						args.add(currentBo.getAction());
					}
					else{
						args.add("");
					}
					
					if( currentBo.getMinQuantity() != null ){
						args.add(currentBo.getMinQuantity());
					}
					else{
						args.add("");
					}
					
					if( currentBo.getMaxQuantity() != null ){
						args.add(currentBo.getMaxQuantity());
					}
					else{
						args.add("");
					}
					
					if( currentBo.getName() != null ){
						args.add(currentBo.getName());
					}
					else{
						args.add("");
					}
					
					if( currentBo.getNeededAttributes() != null ){
						args.add(this.join(currentBo.getNeededAttributes(), ","));
					}
					else{
						args.add("");
					}
					
					args.add(this.getId());
					args.add(da.activityId);
					args.add(da.boId);
				}
				else{
					// Don't exists
					query = "INSERT INTO DataAssociationBusinessObjects (ProcessModelId, ActivityId, BoId, BoSAPId, BoAction, BoMin, BoMax, BoName, BoneededAttributes) VALUES ('%s','%s','%s','%s','%s','%s','%s','%s','%s')";
				
					args.add(this.getId());
					args.add(da.activityId);
					args.add(da.boId);
					
					if( currentBo.getSAPId() != null ){
						args.add(currentBo.getSAPId());
					}
					else{
						args.add("");
					}
					
					if( currentBo.getAction() != null ){
						args.add(currentBo.getAction());
					}
					else{
						args.add("");
					}
					
					if( currentBo.getMinQuantity() != null ){
						args.add(currentBo.getMinQuantity());
					}
					else{
						args.add("");
					}
					
					if( currentBo.getMaxQuantity() != null ){
						args.add(currentBo.getMaxQuantity());
					}
					else{
						args.add("");
					}
					
					if( currentBo.getName() != null ){
						args.add(currentBo.getName());
					}
					else{
						args.add("");
					}
					
					if( currentBo.getNeededAttributes() != null ){
						args.add(this.join(currentBo.getNeededAttributes(), ","));
					}
					else{
						args.add("");
					}
				}
			} catch (SQLException e) {
				e.printStackTrace();
				return;
			}
			
			Application.db.exec(query, args, false);
		}
	}
	
	private String join(String[] s, String glue)
	{
		  int k = s.length;
		  if ( k == 0 )
		  {
		    return null;
		  }
		  StringBuilder out = new StringBuilder();
		  out.append( s[0] );
		  for ( int x=1; x < k; ++x )
		  {
		    out.append(glue).append(s[x]);
		  }
		  return out.toString();
	}
	
	private static String getUID() {
		String id = "";
		
		while (true) {
			id = UUID.randomUUID().toString();
			
			try {
				new ProcessModel(id);
			} catch (ProcessModelNotFoundException e) {
				break;
			}
		}
		return id;
	}
}