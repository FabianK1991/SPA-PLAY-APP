package controllers;

import models.core.ProcessModel;
import models.core.exceptions.ProcessModelNotFoundException;
import play.mvc.Controller;
import play.mvc.Http.MultipartFormData;
import play.mvc.Http.MultipartFormData.FilePart;
import play.mvc.Result;
import views.html.login;
import views.html.main;

public class Page extends Controller {

    public static Result index() {
    	Application.db.connect();
    	
    	//Logger.debug("GetStarted!!");
    	
    	//ProcessModel pm = ProcessModel.createFromBPMN_File(new File("test.xml"));
    	//Activity a = new Activity(ProcessParser.nsm + "Task_3", pm);
    	
    	//a.getBusinessObjects().size();
    	//a.getAction();
    	
    	//System.out.println(a.getBusinessObjects().size());
    	
    	/*try{
    		// Works
    		//ProcessModel pm = new ProcessModel(ProcessParser.nsm + "Process_1");
    		
    		// Works
    		//ProcessInstance pi = ProcessInstance.create(new User("1", "Fabian"), pm);
    		//pi.getCurrentActivity();
    		//pi.getSPAProcessInstance().store();
    		
    		
    		// Works
    		//ProcessInstance pi2 = new ProcessInstance(pi.getId());
    	}catch(ProcessModelNotFoundException e){
    		Logger.debug("Model not found!");
    	}
    	catch(Exception e){
    		e.printStackTrace();
    	}*/
    	
    	//Logger.debug("Created bois!");
    	
    	if(AuthController.check()) {
    		return ok(main.render());
    	}
    	else {
    		return redirect("/login");
    	}
    }
    
    public static Result login(String email) {
        return ok(login.render(email));
    }
    
    public static Result xmlProcess(String processID) {
    	ProcessModel processModel;
		try {
			processModel = new ProcessModel(processID);
			
			return ok(processModel.getBPMN_XML());
		} catch (ProcessModelNotFoundException e) {
			return notFound();
		}
    }
    
    public static Result uploadProcess() {
    	MultipartFormData body = request().body().asMultipartFormData();
    	FilePart filepart = body.getFile("bpmn_file");
    	
    	if (filepart != null) {
    		ProcessModel.createFromBPMN_File(filepart);
    		
    		return ok("File uploaded");
    	}
    	else {
    		flash("error", "Missing file");
    		return redirect(routes.Page.index());
    	}
    }
    /*
    public static Result main() {
        return ok(main.render("Your new application is ready."));
    }*/

}
