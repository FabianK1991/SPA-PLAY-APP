package controllers;

import java.util.ArrayList;
import java.util.Arrays;

import models.core.process.Activity;
import models.core.process.ProcessModel;
import models.core.serverModels.businessObject.BusinessObject;
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
}
