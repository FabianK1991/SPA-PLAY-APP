package controllers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import models.core.process.Activity;
import models.core.process.Event;
import models.core.process.Gateway;
import models.core.process.Node;
import models.core.process.ProcessModel;
import models.core.serverModels.businessObject.BusinessObject;
import models.core.serverModels.businessObject.BusinessObjectProperty;
import models.core.util.parsing.ProcessParser;
import models.util.http.Parameters;
import play.Logger;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.With;
import views.html.ajax_concat;
import views.html.process.activity_config;
import views.html.selectors.business_objects;
import views.html.process.activityConfigs.util.bo_property_changer;
import views.html.process.activityConfigs.util.bo_quantity_changer;
import views.html.process.activityConfigs.util.gateway_condition;

@With(ActionController.class)
public class ActivityDesigner extends Controller {
	
	public static Activity getActivity(String modelId, String activityId) {
		ProcessModel processModel = null;
    	Activity activity = null;
    	
    	try {
    		processModel = new ProcessModel(ProcessParser.nsm + modelId);
    		activity = new Activity(ProcessParser.nsm + activityId, processModel);
    	}
    	catch (Exception e) {
    		
    	}
        return activity;
	}
	
	public static Event getEvent(String modelId, String activityId) {
		ProcessModel processModel = null;
    	Event activity = null;
    	
    	try {
    		processModel = new ProcessModel(ProcessParser.nsm + modelId);
    		activity = new Event(ProcessParser.nsm + activityId, processModel);
    	}
    	catch (Exception e) {
    		
    	}
        return activity;
	}
	
    public static Result changeActivityName(String modelId, String activityId) {
    	Activity activity = getActivity(modelId, activityId);
    	
    	activity.setName(Parameters.get("activity_name"));
    	
    	return ok();
    }
    
    public static Result changeActivityType(String modelId, String activityId) {
		Activity activity = getActivity(modelId, activityId);
    	
    	activity.setType(Parameters.get("activity_type"));
    	
    	return ok(activity_config.render(activity));
    }
    
    public static Result changeBusinessObject(String modelId, String activityId) {
    	Activity activity = getActivity(modelId, activityId);
    	
    	BusinessObject bo = null;
    	
    	try {
			bo = new BusinessObject(Parameters.get("business_objects[]"));
			activity.setBusinessObject(bo);
			
			return ok(ajax_concat.render(bo_property_changer.render(activity), bo_quantity_changer.render(activity), null));
		} catch (Exception e) {
			return notFound();
		}
    }
    
    public static Result changeBO_Properties(String modelId, String activityId) {
    	Activity activity = getActivity(modelId, activityId);
    	
    	ArrayList<String> boProperties = null;

    	try {
    		Logger.info(Parameters.getAll("bo_properties[]").toString());
			boProperties = new ArrayList<String>(Arrays.asList(Parameters.getAll("bo_properties[]")));
			activity.setBO_Properties(boProperties);
			
			return ok();
		} catch (Exception e) {
			e.printStackTrace();
			return notFound();
		}
    }
    
    public static Result changeObjectAmount(String modelId, String activityId) {
    	Activity activity = getActivity(modelId, activityId);
    	
    	String minAmount = Parameters.get("min_amount");
    	String maxAmount = Parameters.get("max_amount");
    	
    	try {
    		activity.setObjectAmountMin(Integer.parseInt(minAmount));
    	}
    	catch(Exception e) {
    		
    	}
    	
    	try {
    		activity.setObjectAmountMax(Integer.parseInt(maxAmount));
    	}
    	catch(Exception e) {
    		
    	}
    	return ok();
    }
    
    public static Result changeGatewayOption(String modelId, String activityId) {
    	/*String conditionActivityId, String conditionPropertyId*/
    	
    	Node nextNode = getActivity(modelId, Parameters.get("next_node"));
    	
    	if (nextNode == null) {Logger.info("use event!!!!");
    		nextNode = getEvent(modelId, Parameters.get("next_node"));
    	}
    	Activity activity = getActivity(modelId, activityId);
    	Gateway nextGateway = activity.getNextGateway();
    	
    	HashMap<Node, HashMap<String, Object>> gatewayOptions = nextGateway.getOptions();

    	Logger.info(nextNode.toString());
    	Logger.info(gatewayOptions.toString());
    	
    	Iterator<Entry<Node, HashMap<String, Object>>> i = gatewayOptions.entrySet().iterator();
    	
    	Node optionKey = null;
    	
    	while(i.hasNext()) {
    		Entry<Node, HashMap<String, Object>> mapEntry = i.next();
    		
    		Node mapKey = mapEntry.getKey();
    		
    		Logger.info(nextNode.getRawId());
    		Logger.info(mapKey.getRawId());
    		
    		if (nextNode.equals(mapKey)) {
    			optionKey = mapKey;
    		}
    	}
    	
    	Logger.info(optionKey.toString());
    	
	    if (optionKey != null) {
	    	HashMap<String, Object>conditionMap = gatewayOptions.get(optionKey);
	    	
	    	Activity decisionActivity = getActivity(modelId, Parameters.get("decision_activity"));

	    	conditionMap.put("activity", decisionActivity);
	    	
	    	Status returnView = ok(gateway_condition.render(activity, nextNode, decisionActivity, conditionMap));
	    	
	    	try {
		    	String[] boProperties = Parameters.getAll("bo_properties[]");
		    	
		    	if (boProperties.length > 0) {
		    		if (decisionActivity.getBusinessObject().getPropertyNames().contains(boProperties[0])) {
		    			conditionMap.put("bo_prop", boProperties[0]);
		    		}
		    		else {
		    			conditionMap.put("bo_prop", "");
		    		}
					conditionMap.put("comparator", Parameters.get("gateway_comparator"));
					conditionMap.put("comp_value", Parameters.get("gateway_decision_value"));
		    	}
		    	Logger.info("set 1" + boProperties.length);
	    	}
	    	catch(Exception e) {
	    		returnView = ok(gateway_condition.render(activity, nextNode, decisionActivity, conditionMap));
	    	}
	    	Logger.info("****");
	    	Logger.info(conditionMap.toString());
	    	gatewayOptions.put(optionKey, conditionMap);
	    	
	    	Logger.info(gatewayOptions.toString());
	    	
	    	nextGateway.setOptions(gatewayOptions);
	    	
	    	return returnView;
	    }
    	return ok();
    }
}
