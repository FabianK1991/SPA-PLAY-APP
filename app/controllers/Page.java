package controllers;

import java.io.File;
import java.net.URLEncoder;
import java.util.ArrayList;

import models.core.process.Activity;
import models.core.process.ProcessModel;
import models.core.serverModels.document.Document;
import models.core.serverModels.document.DocumentType;
import models.core.servers.DocumentServer;
import models.core.util.parsing.ProcessParser;
import models.ontology.OntologyHandler;
import models.ontology.OntologyManager;

import org.apache.http.client.utils.URIBuilder;
import org.apache.http.client.utils.URIUtils;
import org.eclipse.core.runtime.URIUtil;
import org.protege.editor.owl.model.util.URIUtilities;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import play.Logger;
import play.mvc.Controller;
import play.mvc.Http.MultipartFormData.FilePart;
import play.mvc.Result;
import play.mvc.With;
import play.utils.UriEncoding;
import views.html.pages.add_model;
import views.html.pages.login;
import views.html.pages.main;
import views.html.pages.manage_models;
import views.html.pages.process_executor;
import views.html.pages.process_modeler;
import views.html.process.activity_designer;

@With(ActionController.class)
public class Page extends Controller {

    public static Result index() {
    	return ok(main.render());
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
