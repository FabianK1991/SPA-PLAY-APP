package controllers;

import models.core.process.ProcessModel;
import play.mvc.Controller;
import play.mvc.With;
import play.mvc.Result;
import views.html.pages.login;
import views.html.pages.main;
import views.html.pages.add_model;
import views.html.pages.manage_models;
import views.html.pages.process_modeler;

@With(ActionController.class)
public class Page extends Controller {

    public static Result index() {
    	return ok(main.render());
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
    
    public static Result processModeler(String modelId) {
    	ProcessModel processModel = null;
    	
    	try {
    		processModel = new ProcessModel(modelId);
    	}
    	catch (Exception e) {
    		
    	}
        return ok(process_modeler.render(processModel));
    }

}
