package controllers;

import models.core.process.Activity;
import models.core.process.ProcessModel;
import models.core.serverModels.businessObject.BusinessObject;
import models.core.util.parsing.ProcessParser;
import models.util.http.Parameters;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.With;
import views.html.process.activity_config;
import views.html.selectors.business_objects;

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
			bo = new BusinessObject(Parameters.get("business_object"));
			activity.setBusinessObject(bo);
			
			return ok(business_objects.render(bo, false));
		} catch (Exception e) {
			return notFound();
		}
    }
    
    public static Result changeBO_Properties(String modelId, String activityId) {
    	return ok();
    }
    
    public static Result changeObjectAmount(String modelId, String activityId) {
    	Activity activity = getActivity(modelId, activityId);
    	
    	activity.setObjectAmountMin(Integer.parseInt(Parameters.get("min_amount")));
    	activity.setObjectAmountMax(Integer.parseInt(Parameters.get("max_amount")));
    	
    	return ok();
    }
}
