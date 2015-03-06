package controllers;

import models.core.ProcessInstance;
import models.core.ProcessModel;
import models.core.exceptions.ProcessInstanceNotFoundException;
import models.core.exceptions.ProcessModelNotFoundException;
import models.util.http.Parameters;
import play.Logger;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Http.MultipartFormData;
import play.mvc.Http.MultipartFormData.FilePart;
import play.mvc.With;

@With(AuthCheck.class)
public class ProcessController extends Controller {
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
    		ProcessModel.createFromBPMN_File(filepart, Parameters.get("process_name"));
    		
    		return ok("File uploaded");
    	}
    	else {
    		flash("error", "Missing file");
    		return redirect(routes.Page.index());
    	}
    }
    
    public static Result startProcess() {
    	String processID = Parameters.get("process_model");
    	
    	ProcessModel processModel;
    	
		try {
			processModel = new ProcessModel(processID);
		} catch (ProcessModelNotFoundException e) {
			return notFound("Process Model not found! (ID: " + processID + ")");
		}
    	ProcessInstance.create(AuthController.getUser(), processModel);
    	
    	return ok("Process started");
    }
    
    public static Result setCurrentProcess() {
    	String processInstanceID = Parameters.get("process_instance");
    	
    	ProcessInstance processInstance;
    	
		try {
			processInstance = new ProcessInstance(processInstanceID);
		} catch (ProcessInstanceNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		AuthController.getUser().setCurrentProcess(processInstance);
    	
    	return ok("Process started");
    }
}
