package models.core;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.xml.parsers.DocumentBuilderFactory;

import models.util.parsing.ProcessParser;

public class ProcessModel {
	models.spa.api.ProcessModel pm;
	
	private List<Activity> activities; 
	
	/*
	 * TODO
	 * Method to internally (PRIVATE method) create an empty ProcessModel
	 * Should be used only by static method ProcessModel.createFromBPMN_File()
	 */
	private ProcessModel() {
		this(null);
	}
	
	/*
	 * TODO
	 * Instantiates a ProcessModel object by the given ID
	 */
	public ProcessModel(String id) {
		this.pm = new models.spa.api.ProcessModel(id);
		this.activities = new ArrayList<Activity>();
	}
	
	/*
	 * TODO
	 * Returns the BPMN XML file locally stored by the static method ProcessModel.createFromBPMN_File()
	 */
	public File getBPMN_XML() {
		return null;
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
	
	public void addActivity(Activity act){
		// add it to the spa model
		this.pm.getNodes().add(act.getSPAActivity());
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
		ProcessParser pp = new ProcessParser(file);
		
		// TODO: store file
		
		return pp.getParsedModels().get(0);
	}
}
