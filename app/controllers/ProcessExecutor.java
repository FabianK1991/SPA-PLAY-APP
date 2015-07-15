package controllers;

import java.util.ArrayList;

import models.core.exceptions.ProcessInstanceNotFoundException;
import models.core.exceptions.ProcessModelNotFoundException;
import models.core.process.Activity;
import models.core.process.ProcessInstance;
import models.core.process.ProcessModel;
import models.core.util.parsing.ProcessParser;
import models.util.http.Parameters;
import play.Logger;
import play.mvc.Controller;
import play.mvc.Http.MultipartFormData;
import play.mvc.Http.MultipartFormData.FilePart;
import play.mvc.Result;
import play.mvc.With;
import play.twirl.api.Html;
import views.html.pages.main;
import views.html.process.activity_instances;
import views.html.process.process_execution;
import views.html.pages.process_executor;
import views.html.ajax_concat;

@With(ActionController.class)
public class ProcessExecutor extends Controller {
	public static Result xmlProcess(String processID) {
    	ProcessModel processModel;
		try {
			processModel = new ProcessModel(ProcessParser.nsm + processID);
			
			return ok(processModel.getBPMN_XML());
		} catch (ProcessModelNotFoundException e) {
			return notFound();
		}
    }
    
    public static Result uploadProcess() {
    	MultipartFormData body = request().body().asMultipartFormData();
    	FilePart filepart = body.getFile("bpmn_file");
    	
    	if (filepart != null) {Logger.debug(Parameters.get("process_name"));
    		ProcessModel.createFromBPMN_File(filepart, Parameters.get("process_name"));
    		
    		return redirect(routes.Page.manageProcessModels());
    	}
    	else {
    		flash("error", "Missing file");
    		return redirect(routes.Page.index());
    	}
    }
    
    public static Result startProcess(String processModelId) {
    	ProcessModel processModel;
    	
		try {
			processModel = new ProcessModel(ProcessParser.nsm + processModelId);
		} catch (ProcessModelNotFoundException e) {
			return notFound("Process Model not found! (ID: " + processModelId + ")");
		}
    	AuthController.getUser().createProcessInstance(processModel);
    	
    	return ok(ajax_concat.render(process_executor.render(), null, routes.Page.processExecutor().url() + "#current-process"));
    }
    
    public static Result setCurrentProcess(String processInstanceId) {
    	ProcessInstance processInstance;
    	
		try {
			processInstance = new ProcessInstance(ProcessParser.nsmi + processInstanceId);
			AuthController.getUser().setCurrentProcessInstance(processInstance);
			
			return ok(ajax_concat.render(process_executor.render(), null, routes.Page.processExecutor().url() + "#current-process"));
		} catch (ProcessInstanceNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
			return notFound("Process Instance not found! (ID: " + processInstanceId + ")");
		}
    }
    
    public static Result performActivity(String processInstanceId, String activityId) {
    	
		try {
			ProcessInstance processInstance = new ProcessInstance(ProcessParser.nsmi + processInstanceId);
			
			Activity activity = new Activity(ProcessParser.nsm + activityId, processInstance.getProcessModel());
		    
			processInstance.setCurrentActivities(activity.getNextActivities());
			
			return ok(process_execution.render(processInstance));
		} catch (ProcessInstanceNotFoundException e1) {
			return notFound("Process Instance not found! (ID: " + processInstanceId + ")");
		}
    }
    
    public static Result performGatewayAction() {
    	String processInstanceID = Parameters.get("process_instance");
    	String[] nextActivities = Parameters.get("next_activity").split(",");
    	Logger.info(nextActivities.toString());
		try {
			ProcessInstance processInstance = new ProcessInstance(processInstanceID);
	    	
			ArrayList<Activity> newCurrentActivities = new ArrayList<Activity>();
			
			for (String nextActivityId : nextActivities) {
				newCurrentActivities.add(new Activity(ProcessParser.nsm + nextActivityId, processInstance.getProcessModel()));
			}
			processInstance.setCurrentActivities(newCurrentActivities);
			
			return ok(activity_instances.render(processInstance));
		} catch (ProcessInstanceNotFoundException e) {
			e.printStackTrace();
			return notFound("Process Instance not found! (ID: " + processInstanceID + ")");
		}
    }
}
