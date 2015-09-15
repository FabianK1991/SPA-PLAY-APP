package controllers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import models.core.exceptions.ProcessInstanceNotFoundException;
import models.core.exceptions.ProcessModelNotFoundException;
import models.core.process.Activity;
import models.core.process.ActivityInstance;
import models.core.process.Node;
import models.core.process.ProcessInstance;
import models.core.process.ProcessModel;
import models.core.serverModels.businessObject.BusinessObject;
import models.core.serverModels.businessObject.BusinessObjectInstance;
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

			Activity activity = null;
			ActivityInstance activityInstance = null;
			
			try {
				activity = new Activity(ProcessParser.nsm + activityId, processInstance.getProcessModel());
				activityInstance = processInstance.getCurrentActivities().get(0);
			}
			catch(Exception e) {
		    	
		    }
			List<Object> outputs = new ArrayList<Object>();
			
			Logger.info("Current Activity: " + activityInstance.getActivity().getRawId());
			
			if (activity.getType().equals("bo_select")) {Logger.info("123");
				String[] selectedBOs = Parameters.getAll("selected_bos[]");
				BusinessObject bo = activity.getBusinessObject();
				
				for (int i = 0; i < selectedBOs.length; i++) {
					Logger.info("get BO " + bo.getName());
					Logger.info("get BO " + selectedBOs[i]);
					outputs.add(BusinessObjectInstance.getBySAPId(bo, selectedBOs[i]));
				}
				
				if (outputs.size() < activity.getObjectAmountMin() || (outputs.size() > activity.getObjectAmountMax() && activity.getObjectAmountMax() > 0)) {
					return notFound("Please select the correct amount of business objects!");
				}
			}
			else if (activityInstance.getActivity().getType().equals("def_question")) {
				outputs.add(Parameters.get("output_value"));
			}
			Logger.info(outputs.toString());
			
			activityInstance.setOutputs(outputs);
			
			List<Activity> nextActivities = activity.getNextActivities();
			
			Logger.info("Num next Activities: " + nextActivities.size());
			
			if (nextActivities.size() > 0) {
				
				//TODO: wait for FIX of getNextActivities(), then remove complete if clause + internal block
				/*if (nextActivities.size() > 1 && activity.getRawId().equals("Task_3")) {
					for (int i = 0; i < nextActivities.size(); i++) {
						if (nextActivities.get(i).getRawId().equals("Task_4")) {
							nextActivities.remove(i);
						}
					}
				}*/
				processInstance.setCurrentActivities(nextActivities);
				
				for (int i = 0; i < nextActivities.size(); i++) {
					Activity nextActivity = nextActivities.get(i);
					Logger.info("Next Activity: " + nextActivity.getRawId());
					
					if (nextActivity != null && nextActivity.getType().equals("gateway_decision")) {
						HashMap<Node, HashMap<String, Object>> gatewayOptions = nextActivity.getNextGateway().getOptions();
						
						for (Node nextNode : gatewayOptions.keySet()) {
							HashMap<String, Object> gatewayOption = gatewayOptions.get(nextNode);
							
							Activity decisionActivity = (Activity) gatewayOption.get("activity");
							String boProp = (String) gatewayOption.get("bo_prop");
							String comparator = (String) gatewayOption.get("comparator");
							String compValue = (String) gatewayOption.get("comp_value");
							
							List<Object> decisionOutputs = processInstance.getActivityInstance(decisionActivity).getOutputs();
							
							String decisionOutput;
							
							try {
								decisionOutput = ((BusinessObjectInstance)decisionOutputs.get(0)).getPropertyValue(boProp);
							}
							catch (Exception e) {
								if (decisionOutputs.size() > 0) {
									decisionOutput = (String)decisionOutputs.get(0);
								}
								else {
									decisionOutput = "";
								}
								
							}
							boolean decision = false;
							
							if (comparator.equals("=") && compValue.equals(decisionOutput)) {Logger.info("ja");
								decision = true;
							}
							if (comparator.equals("!=") && compValue.equals(decisionOutput) == false) {
								decision = true;
							}
							if (comparator.equals(">=") && Float.parseFloat(decisionOutput) >= Float.parseFloat(compValue)) {
								decision = true;
							}
							if (comparator.equals(">") && Float.parseFloat(decisionOutput) > Float.parseFloat(compValue)) {
								decision = true;
							}
							if (comparator.equals("<=") && Float.parseFloat(decisionOutput) <= Float.parseFloat(compValue)) {
								decision = true;
							}
							if (comparator.equals("<") && Float.parseFloat(decisionOutput) < Float.parseFloat(compValue)) {
								decision = true;
							}
							Logger.info("*** make gateway decision ***");
							Logger.info(decision + "");
							Logger.info(decisionOutput + "");
							Logger.info(comparator + "");
							Logger.info(compValue + "");
							
							if (decision) {
								if (nextNode instanceof Activity) {
									Activity gatewayNextActivity = (Activity)nextNode;
									Logger.info(gatewayNextActivity.getName());
									
									ArrayList<Activity> gatewayDecisionActivities = new ArrayList<Activity>();
									
									gatewayDecisionActivities.add(gatewayNextActivity);
									
									processInstance.setCurrentActivities(gatewayDecisionActivities);
									
									//recursive gateway check call!
								}
								else {
									if (true) {//check if Event == End of Process
										processInstance.terminate();
										return notFound("Process terminated!");
									}
								}
							}
						}
					}
				}
				return ok(process_execution.render(processInstance));
			}
			else {
				processInstance.terminate();
				return notFound("Process terminated!");
			}
		} catch (ProcessInstanceNotFoundException e1) {
			
		}
		return notFound("Process Instance not found! (ID: " + processInstanceId + ")");
    }
    
    public static Result performGatewayAction() {
    	String processInstanceID = Parameters.get("process_instance");
    	String[] nextActivities = Parameters.get("next_activity").split(",");
    	Logger.info(nextActivities.toString());
		try {
			ProcessInstance processInstance = new ProcessInstance(processInstanceID);
	    	
			ArrayList<Activity> newCurrentActivities = new ArrayList<Activity>();
			
			for (String nextActivityId : nextActivities) {
				try {
				newCurrentActivities.add(new Activity(ProcessParser.nsm + nextActivityId, processInstance.getProcessModel()));
				}
				catch(Exception e) {
					
				}
			}
			processInstance.setCurrentActivities(newCurrentActivities);
			
			return ok(activity_instances.render(processInstance));
		} catch (ProcessInstanceNotFoundException e) {
			e.printStackTrace();
			return notFound("Process Instance not found! (ID: " + processInstanceID + ")");
		}
    }
}
