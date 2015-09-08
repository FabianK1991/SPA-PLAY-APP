package controllers;

import models.core.process.Activity;
import models.core.process.ProcessModel;
import models.core.util.parsing.ProcessParser;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.With;
import views.html.pages.add_model;
import views.html.pages.login;
import views.html.pages.main;
import views.html.pages.manage_models;
import views.html.pages.process_executor;
import views.html.pages.process_modeler;
import views.html.pages.manager_dashboard;
import views.html.process.activity_designer;

@With(ActionController.class)
public class Page extends Controller {

    public static Result index() {
    	return processExecutor();
    }
    
    public static Result processExecutor() {
    	return ok(process_executor.render());
    }
    
    public static Result login(String email) {
        return ok(login.render(email));
    }
    
    public static Result addProcessModel() {
        return ok(add_model.render());
    }
    
    public static Result manageProcessModels() {
        return ok(manage_models.render());
    }
    
    public static Result managerDashboard() {
    	return ok(manager_dashboard.render());
    }
    
    public static Result processModeler(String modelId) {
    	ProcessModel processModel = null;
    	
    	try {
    		processModel = new ProcessModel(ProcessParser.nsm + modelId);
    	}
    	catch (Exception e) {
    		
    	}
        return ok(process_modeler.render(processModel));
    }
    
    public static Result activityDesigner(String modelId, String activityId) {
    	ProcessModel processModel = null;
    	Activity activity = null;
    	
    	try {
    		processModel = new ProcessModel(ProcessParser.nsm + modelId);
    		activity = new Activity(ProcessParser.nsm + activityId, processModel);
    	}
    	catch (Exception e) {
    		
    	}
        return ok(activity_designer.render(activity));
    }


}
