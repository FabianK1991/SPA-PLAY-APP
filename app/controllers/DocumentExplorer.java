package controllers;

import models.core.process.Activity;
import models.core.process.Phase;
import models.core.process.ProcessModel;
import models.core.serverModels.businessObject.BusinessObject;
import models.core.serverModels.businessObject.BusinessObjectInstance;
import models.core.util.parsing.ProcessParser;
import models.util.http.Parameters;
import play.Logger;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.With;
import views.html.documents.explorer;

@With(ActionController.class)
public class DocumentExplorer extends Controller {
	
    public static Result getDocument(String businessObjectId, String businessObjectInstanceId) {
    	BusinessObject businessObject = null;
    	BusinessObjectInstance businessObjectInstance = null;
    	
    	try {
    		businessObject = new BusinessObject(businessObjectId);
    		businessObjectInstance = BusinessObjectInstance.getBySAPId(businessObject, businessObjectInstanceId);
        	Logger.info(businessObjectInstance.getRelatedDocuments().size() + "");
    	}
    	catch (Exception e) {
    		e.printStackTrace();
    	}
    	return ok(explorer.render(businessObjectInstance));
    }
}
