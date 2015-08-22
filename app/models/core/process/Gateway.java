package models.core.process;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import controllers.ActivityDesigner;
import play.Logger;
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
			this.pm.pm.store();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/*
	 */
	public void setOptions(HashMap<models.core.process.Node, HashMap<String, Object>> gatewayOptions) {
		Set<Flow> nextFlows = this.gateway.getNextFlows();
		
		for(Flow f: nextFlows){
			Iterator<Entry<Node, HashMap<String, Object>>> it = gatewayOptions.entrySet().iterator();
			
		    while (it.hasNext()) {
		        Entry<Node, HashMap<String, Object>> pair = it.next();
		        //System.out.println(pair.getKey() + " = " + pair.getValue());

		        if( (pair.getKey()).getId().equals(f.getTo().getId()) ){
		        	HashMap<String, Object> optionMap = pair.getValue();
		        	
		        	String conditionString = optionMap.get("activity") + "|" + optionMap.get("bo_prop") + "|" + optionMap.get("comparator") + "|" + optionMap.get("comp_value");
		        	
		        	f.setCondition(conditionString);
		        	break;
		        }
		    }
		}
		
		try {
			// Update it
			this.pm.getSPAProcessModel().store();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/*
	 */
	public HashMap<models.core.process.Node, HashMap<String, Object>> getOptions() {
		/*Matching: possible answer (e.g. credit card) => related next activity (e.g. payment with credit card)*/
		HashMap<models.core.process.Node, HashMap<String, Object>> result = new HashMap<models.core.process.Node, HashMap<String, Object>>();
		
		Set<Flow> nextFlows = this.gateway.getNextFlows();

		for(Flow f: nextFlows){
			String toId = f.getTo().getId();
			String condition = f.getCondition();
			
			/*
			Set<String> set = f.getTo().getKeywords();
			
			for(String s: set){
				if(s.contains("condition:")){
					result.put(s.substring(10), new Activity(toId, this.pm));
					break;
				}
			}*/
			models.core.process.Node activity = null;
			
			try {
				activity = new Activity(toId, this.pm);
			}
			catch(Exception e) {
				activity = new Event(toId, this.pm);
			}
			String[] condParts = condition.split("|");
			
			HashMap<String, Object> conditionMap = new HashMap<String, Object>();
			
			conditionMap.put("activity", null);
			conditionMap.put("bo_prop", "");
			conditionMap.put("comparator", "");
			conditionMap.put("comp_value", 0);
			
			try {
				conditionMap.put("activity", new Activity(ProcessParser.nsm + condParts[0], this.pm));
				conditionMap.put("activity_prop", condParts[1]);
				conditionMap.put("comparator", condParts[2]);
				conditionMap.put("comp_value", condParts[3]);
			}
			catch(Exception e) {
				
			}
			
			result.put(activity, conditionMap);
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
