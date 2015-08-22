package models.core.process;

import play.Logger;

public abstract class Node {
	ProcessModel pm;
	
	public String getRawId() {
		return "";
	}
	
	public String getId() {
		return "";
	}
	
	public String getName() {
		return "";
	}
	
	public ProcessModel getModel() {
		return this.pm;
	}
	
	public boolean equals(Node otherNode) {
		if (otherNode != null /*&& this.pm.getRawId().equals(otherNode.getModel().getRawId())*/ && this.getRawId().equals(otherNode.getRawId())) {
			return true;
		}
		else {
			return false;
		}
	}
}
