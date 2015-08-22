package models.core.process;

import models.core.util.parsing.ProcessParser;

public class Event extends Node {
	private models.spa.api.process.buildingblock.Event event;
	private ProcessModel pm;

	public Event(String id, ProcessModel pm){
		this.event = (models.spa.api.process.buildingblock.Event)pm.getSPANodeById(id);
		this.pm = pm;
	}
	
	public String getId() {
		return event.getId();
	}
	
	public String getRawId() {
		return this.getId().replace(ProcessParser.nsm, "");
	}
	
	public String getName() {
		return "[End of Process]";
	}
}
