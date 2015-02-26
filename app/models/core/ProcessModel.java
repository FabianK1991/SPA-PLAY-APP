package models.core;

import java.io.File;
import java.util.List;

public class ProcessModel {
	/*
	 * TODO
	 * Method to internally (PRIVATE method) create an empty ProcessModel
	 * Should be used only by static method ProcessModel.createFromBPMN_File()
	 */
	private ProcessModel() {
		
	}
	
	/*
	 * TODO
	 * Instantiates a ProcessModel object by the given ID
	 */
	public ProcessModel(String id) {
		
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
		return "";
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
		return null;
	}
}
