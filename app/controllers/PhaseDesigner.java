package controllers;

import models.core.process.Activity;
import models.core.process.Phase;
import models.core.process.ProcessModel;
import models.core.util.parsing.ProcessParser;
import models.util.http.Parameters;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.With;
import views.html.process.phase_list;

@With(ActionController.class)
public class PhaseDesigner extends Controller {
	
    public static Result createPhase(String modelId) {
    	ProcessModel processModel = null;
    	
    	try {
    		processModel = new ProcessModel(ProcessParser.nsm + modelId);
    		processModel.addPhase(Parameters.get("phase_name"), processModel.getNumPhases());
    	}
    	catch (Exception e) {
    		
    	}
    	return ok(phase_list.render(processModel));
    }
    
    public static Result deletePhase(String phaseId) {
    	try {
    		Phase phase = new Phase(phaseId);
    		phase.delete();
    		
        	return ok();
    	}
    	catch(Exception e) {
    		return notFound();
    	}
    }
    
    public static Result addActivity(String phaseId, String activityId) {
    	try {
    		Phase phase = new Phase(phaseId);
    		Activity activity = new Activity(ProcessParser.nsm + activityId, phase.getProcessModel());
    		
    		phase.addActivity(activity);
    		
        	return ok();
    	}
    	catch(Exception e) {
    		return notFound();
    	}
    }
    
    public static Result deleteActivity(String phaseId, String activityId) {
    	try {
    		Phase phase = new Phase(phaseId);
    		Activity activity = new Activity(ProcessParser.nsm + activityId, phase.getProcessModel());

    		phase.removeActivity(activity);
    		
        	return ok();
    	}
    	catch(Exception e) {
    		return notFound();
    	}
    }
}
