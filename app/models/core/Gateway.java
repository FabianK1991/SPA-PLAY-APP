package models.core;

import java.util.Hashtable;
import java.util.Set;

import models.spa.api.process.buildingblock.Flow;


public class Gateway {
	models.spa.api.process.buildingblock.Gateway gateway;
	ProcessModel pm;
	
	/*
	 */
	public Gateway(String id, ProcessModel pm) {
		this.gateway = (models.spa.api.process.buildingblock.Gateway)pm.getSPANodeById(id);
		this.pm = pm;
	}
	
	/*
	 */
	public String getCondition() {
		return this.gateway.getName();
	}
	
	/*
	 */
	public Hashtable<String,Activity> getOptions() {
		/*Matching: possible answer (e.g. credit card) => related next activity (e.g. payment with credit card)*/
		Hashtable<String,Activity> result = new Hashtable<String,Activity>();
		
		Set<Flow> nextFlows = this.gateway.getNextFlows();
		
		for(Flow f: nextFlows){
			String toId = f.getTo().getId();
			String key = f.getCondition();
			
			result.put(key, new Activity(toId, this.pm));
		}
		
		return result;
	}
}
