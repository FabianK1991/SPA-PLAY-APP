package models.core;

import java.util.HashMap;
import java.util.Set;

import models.spa.api.process.buildingblock.Flow;
import models.util.parsing.ProcessParser;
import play.Logger;


public class Gateway {
	models.spa.api.process.buildingblock.Gateway gateway;
	ProcessModel pm;
	
	/*
	 */
	public Gateway(String id, ProcessModel pm) {
		this.gateway = (models.spa.api.process.buildingblock.Gateway)pm.getSPANodeById(id);
		this.pm = pm;
	}
	
	public String getId() {
		return gateway.getId();
	}
	
	public String getRawId() {
		return this.getId().replace(ProcessParser.nsm, "");
	}
	/*
	 */
	public String getCondition() {
		return this.gateway.getName();
	}
	
	public String getType() {
		return this.gateway.type;
	}
	
	/*
	 */
	public HashMap<String,Activity> getOptions() {
		/*Matching: possible answer (e.g. credit card) => related next activity (e.g. payment with credit card)*/
		HashMap<String,Activity> result = new HashMap<String,Activity>();
		
		Set<Flow> nextFlows = this.gateway.getNextFlows();
		
		for(Flow f: nextFlows){
			String toId = f.getTo().getId();
			String key = f.getCondition();
			Logger.debug(key);
			result.put(key, new Activity(toId, this.pm));
		}
		
		return result;
	}
	
	public static boolean isGatewayType(String type) {
		return (models.spa.api.process.buildingblock.Gateway.GatewayType.valueOf(type) != null);
	}
}
