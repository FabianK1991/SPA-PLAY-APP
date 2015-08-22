package models.core.process;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import models.core.util.parsing.ProcessParser;
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
	
	public void setCondition(String condition) {
		this.gateway.setName(condition);
		
		// Update it in spa
		try {
			this.pm.pm.update();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/*
	 */
	public void setOptions(HashMap<Activity, String> options) {
		Set<Flow> nextFlows = this.gateway.getNextFlows();
		
		for(Flow f: nextFlows){
			Iterator it = options.entrySet().iterator();
			
		    while (it.hasNext()) {
		        Map.Entry pair = (Map.Entry)it.next();
		        //System.out.println(pair.getKey() + " = " + pair.getValue());

		        if( ((Activity)pair.getKey()).getId().equals(f.getTo().getId()) ){
		        	f.setCondition((String)pair.getValue());
		        	break;
		        }
		    }
		}
		
		try {
			// Update it
			this.pm.getSPAProcessModel().update();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/*
	 */
	public HashMap<Activity, String> getOptions() {
		/*Matching: possible answer (e.g. credit card) => related next activity (e.g. payment with credit card)*/
		HashMap<Activity, String> result = new HashMap<Activity, String>();
		
		Set<Flow> nextFlows = this.gateway.getNextFlows();
		
		for(Flow f: nextFlows){
			String toId = f.getTo().getId();
			String key = f.getCondition();
			
			/*
			Set<String> set = f.getTo().getKeywords();
			
			for(String s: set){
				if(s.contains("condition:")){
					result.put(s.substring(10), new Activity(toId, this.pm));
					break;
				}
			}*/
			result.put(new Activity(toId, this.pm), key);
		}
		
		return result;
	}
	
	public static boolean isGatewayType(String type) {
		try {
			return (models.spa.api.process.buildingblock.Gateway.GatewayType.valueOf(type) != null);
		}
		catch(IllegalArgumentException e) {
			return false;
		}
	}
}
