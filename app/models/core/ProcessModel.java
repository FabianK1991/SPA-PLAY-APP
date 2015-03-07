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

import org.apache.commons.io.FileUtils;

import play.mvc.Http.MultipartFormData.FilePart;
import controllers.Application;
import models.core.exceptions.ActivityInstanceNotFoundException;
import models.core.exceptions.IncorrectNumberOfProcessModelsExeption;
import models.core.exceptions.ProcessInstanceNotFoundException;
import models.core.exceptions.ProcessModelNotFoundException;
import models.util.parsing.ProcessParser;

public class ProcessModel {
	models.spa.api.ProcessModel pm;
	private static final String xmlPath = "data/processes/";
	
	// Maps activities to bos
	/* TABLE FORMAT CREATE TABLE Data_Association_Business_Objects (
		process_model_ref VARCHAR(128),
		activity_ref VARCHAR(128),
		bo_ref VARCHAR(128),
		business_object VARCHAR(32),
		action VARCHAR(32),
		min VARCHAR(16),
		max VARCHAR(16),
		BoName VARCHAR(128),
		req_attributes VARCHAR(255),
		RID int(11) NOT NULL auto_increment, primary KEY (RID));
	*/
	public List<DataAssociation> dataAssoc;
	public List<BusinessObject> bos;
	private String id;
	
	/*
	 * Method to internally (PRIVATE method) create an empty ProcessModel
	 * Should be used only by static method ProcessModel.createFromBPMN_File()
	 */
	private ProcessModel() {
		this.pm = new models.spa.api.ProcessModel();
		this.pm.setId(ProcessParser.nsm + getUID());
		
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
			this.id = id;
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
		return new File(getXML_Filename());
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
	
	public void setName(String name) {
		this.pm.setName(name);
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
	
	public static ProcessModel createFromBPMN_File(FilePart filepart) {
		String fileName = filepart.getFilename();
		
		return createFromBPMN_File(filepart, fileName);
	}
	
	/*
	 * Receives a BPMN XML file containing a process model with annotated business objects,
	 * stores this file as /processes/[ProcessModel:ID].bpmn
	 * and parses the XML to create a ProcessModel in the SPA.
	 * The created ProcessModel instance needs to be returned.
	 */
	public static ProcessModel createFromBPMN_File(FilePart filepart, String name) {
		String fileName = filepart.getFilename();
	    String contentType = filepart.getContentType();
	    File file = filepart.getFile();
	    
		// Init model and parser
		ProcessModel newProcessModel = new ProcessModel();
		ProcessParser pp = new ProcessParser(newProcessModel);
		
		newProcessModel.setName(name);
		
		// Parse model
		try {
			pp.parseXML(file);
		} catch (IncorrectNumberOfProcessModelsExeption e1) {
			e1.printStackTrace();
		}
		
		// Save to file
		File f = new File(newProcessModel.getXML_Filename());
		
		// Check if file exists, otherwise write it
		if( !f.exists() ){
			try {
				FileUtils.moveFile(file, f);
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
	
	private String getXML_Filename() {
		return xmlPath + this.getId().replaceAll("(" + ProcessParser.nsm + "|[^A-Za-z0-9-_])", "")+ ".bpmn";
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
		String query = "SELECT * FROM data_association_business_objects WHERE process_model_ref = '%s'";
		
		ArrayList<String> args = new ArrayList<String>();
		
		args.add(this.getId());
		
		ResultSet rs = Application.db.exec(query, args, true);
		
		try {
			if(rs.first()){
				do{
					this.dataAssoc.add(new DataAssociation(rs.getString("activity_ref"), rs.getString("bo_ref")));
					
					BusinessObject bo = this.getBoById(rs.getString("bo_ref"));
					
					if( bo == null ){
						bo = new BusinessObject(rs.getString("bo_ref"));
						
						if( rs.getString("business_object").length() > 0 ){
							bo.setSAPId(rs.getString("business_object"));
						}
						if( rs.getString("action").length() > 0 ){
							bo.setAction(rs.getString("action"));
						}
						if( rs.getString("min").length() > 0 ){
							bo.setMin(rs.getString("min"));
						}
						if( rs.getString("max").length() > 0 ){
							bo.setMax(rs.getString("max"));
						}
						if( rs.getString("BoName").length() > 0 ){
							bo.setName(rs.getString("BoName"));
						}
						if( rs.getString("req_attributes").length() > 0 ){
							bo.setNeededAttributes(rs.getString("req_attributes").split(","));
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
	
	/* TABLE FORMAT CREATE TABLE data_association_business_objects (
		process_model_ref VARCHAR(128),
		activity_ref VARCHAR(128),
		bo_ref VARCHAR(128),
		business_object VARCHAR(32),
		action VARCHAR(32),
		min VARCHAR(16),
		max VARCHAR(16),
		BoName VARCHAR(128),
		req_attributes VARCHAR(255),
		RID int(11) NOT NULL auto_increment, primary KEY (RID));
	*/
	private void persistBusinessObjectDataAssociations(){
		Application.db.connect();
		
		for(DataAssociation da : this.dataAssoc){
			BusinessObject currentBo = this.getBoById(da.boId);
			
			// Check if already in database
			String query = "SELECT * FROM data_association_business_objects WHERE process_model_ref = '%s' AND activity_ref = '%s' AND bo_ref = '%s'";
			ArrayList<String> args = new ArrayList<String>();
			
			args.add(this.getId());
			args.add(da.activityId);
			args.add(da.boId);
			
			ResultSet rs = Application.db.exec(query, args, true);
			
			try {
				args.clear();

				if(rs.first()){
					// Already exists
					query = "UPDATE data_association_business_objects SET business_object = '%s',action = '%s',min = '%s',max = '%s',BoName = '%s',req_attributes = '%s' WHERE process_model_ref = '%s' AND activity_ref = '%s' AND bo_ref = '%s'";
					
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
					query = "INSERT INTO data_association_business_objects (process_model_ref, activity_ref, bo_ref, business_object, action, min, max, BoName, req_attributes) VALUES ('%s','%s','%s','%s','%s','%s','%s','%s','%s')";
				
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