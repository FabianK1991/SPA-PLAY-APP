package models.core;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import javax.xml.parsers.DocumentBuilderFactory;

import models.core.exceptions.ActivityInstanceNotFoundException;
import models.core.exceptions.ProcessInstanceNotFoundException;
import models.core.exceptions.ProcessModelNotFoundException;
import models.util.parsing.ProcessParser;

public class ProcessModel {
	models.spa.api.ProcessModel pm;
	private static final String xmlPath = "public/processes/";
	
	// Maps activities to bos
	// TODO: Need to be parsed again (or get it from a db) if Model is retrieved by SPA 
	public List<DataAssociation> dataAssoc;
	public List<BusinessObject> bos;
	
	/*
	 * TODO
	 * Method to internally (PRIVATE method) create an empty ProcessModel
	 * Should be used only by static method ProcessModel.createFromBPMN_File()
	 */
	private ProcessModel() {
		this.pm = new models.spa.api.ProcessModel();
		this.dataAssoc = new ArrayList<DataAssociation>();
		this.bos = new ArrayList<BusinessObject>();
	}
	
	/*
	 * TODO
	 * Instantiates a ProcessModel object by the given ID
	 * 
	 * >> Needs to SEARCH in SPA for a ProcessModel with the given ID <<
	 * >> This process model (already existing!) needs to be instantiated, not a new one! <<
	 */
	public ProcessModel(String id) throws ProcessModelNotFoundException {
		try {
			this.pm = models.spa.api.ProcessModel.getProcess(id);
		} catch (Exception e) {
			throw new ProcessModelNotFoundException();
		}
	}
	
	// Needed for ProcessInstance Constructor
	public ProcessModel(models.spa.api.ProcessModel pm) {
		this.pm = pm;
	}
	
	/*
	 * TODO
	 * Returns the BPMN XML file locally stored by the static method ProcessModel.createFromBPMN_File()
	 */
	public File getBPMN_XML() {
		return new File(xmlPath + this.pm.getId() + ".bpmn");
	}
	
	public String getId(){
		return this.pm.getId();
	}
	
	/*
	 * TODO
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
	 * TODO
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
	 * TODO
	 * Receives a BPMN XML file containing a process model with annotated business objects,
	 * stores this file as /processes/[ProcessModel:ID].bpmn
	 * and parses the XML to create a ProcessModel in the SPA.
	 * The created ProcessModel instance needs to be returned.
	 */
	public static ProcessModel createFromBPMN_File(File file) {
		ProcessModel newProcessModel = new ProcessModel();
		
		ProcessParser pp = new ProcessParser(file, newProcessModel);
		
		/*File f = new File(xmlPath + newProcessModel.getId() + ".bpmn");
		
		// Check if file exists, otherwise write it
		if( !f.exists() ){
			try {
				Files.copy(file.toPath(), f.toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}*/
		
		// save process model
		try {
			newProcessModel.getSPAProcessModel().delete();
			newProcessModel.getSPAProcessModel().store();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return newProcessModel;
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
