package controllers;

import java.io.File;

import models.core.Activity;
import models.core.ProcessInstance;
import models.core.ProcessModel;
import models.core.exceptions.ProcessModelNotFoundException;
import models.util.parsing.ProcessParser;
import models.util.sessions.User;
import play.Logger;
import play.mvc.*;
import views.html.*;

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
    		return ok(index.render("Your new application is ready."));
    	}
    	else {
    		return redirect("/login");
    	}
    }
    
    public static Result login(String email) {
        return ok(login.render(email));
    }
    /*
    public static Result main() {
        return ok(main.render("Your new application is ready."));
    }*/

}
