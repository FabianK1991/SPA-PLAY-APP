package controllers;

import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.With;

@With(ActionController.class)
public class PhaseDesigner extends Controller {
	
    public static Result createPhase(String processModelId) {
    	return ok();
    }
    
    public static Result deletePhase(String processModelId) {
    	return ok();
    }
}
