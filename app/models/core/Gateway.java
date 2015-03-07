package models.core;

import java.util.HashMap;
import java.util.Set;

import models.spa.api.process.buildingblock.Flow;
import models.util.parsing.ProcessParser;


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
	
	public String getType() {
		return this.gateway.type;
	}
	
	/*
	 */
	public String getCondition() {
		return this.gateway.getName();
	}
	
	/*
	 */
	public HashMap<String,Activity> getOptions() {
		/*Matching: possible answer (e.g. credit card) => related next activity (e.g. payment with credit card)*/
		HashMap<String,Activity> result = new HashMap<String,Activity>();
		
		Set<Flow> nextFlows = this.gateway.getNextFlows();
		
		for(Flow f: nextFlows){
			String toId = f.getTo().getId();
			
			Set<String> set = f.getTo().getKeywords();
			
			for(String s: set){
				if(s.contains("condition:")){
					result.put(s.substring(10), new Activity(toId, this.pm));
					break;
				}
			}
		}
		
		return result;
	}
	
	public static boolean isGatewayType(String type) {
		return (models.spa.api.process.buildingblock.Gateway.GatewayType.valueOf(type) != null);
	}
}
