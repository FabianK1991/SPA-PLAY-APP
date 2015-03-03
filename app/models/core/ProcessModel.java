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
	private static final String xmlPath = "/public/processes/";
	
	/*
	 * TODO
	 * Method to internally (PRIVATE method) create an empty ProcessModel
	 * Should be used only by static method ProcessModel.createFromBPMN_File()
	 */
	private ProcessModel() {
		this.pm = new models.spa.api.ProcessModel(getUID());
		
		// save to repository
		try {
			//this.pm.store();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
	
	/*
	 * TODO
	 * Returns all process models stored in the SPA
	 */
	public static List<ProcessModel> getAll() {
		return null;
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
		
		File f = new File(xmlPath + newProcessModel.getId() + ".bpmn");
		
		// Check if file exists, otherwise write it
		if( !f.exists() ){
			try {
				Files.copy(file.toPath(), f.toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
			} catch (IOException e) {
				e.printStackTrace();
			}
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
