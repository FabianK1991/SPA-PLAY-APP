package controllers;

import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.With;

@With(ActionController.class)
public class ActivityDesigner extends Controller {
	
    public static Result changeActivityName(String activityId) {
    	return ok();
    }
    
    public static Result changeActivityType(String activityId) {
    	return ok();
    }
    
    public static Result changeBusinessObject(String activityId) {
    	return ok();
    }
    
    public static Result changeBO_Properties(String activityId) {
    	return ok();
    }
    
    public static Result changeObjectAmount(String activityId) {
    	return ok();
    }
}
