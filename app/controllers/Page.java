package controllers;

import models.core.ProcessModel;
import models.core.exceptions.ProcessModelNotFoundException;
import models.util.http.Parameters;
import play.Logger;
import play.mvc.Controller;
import play.mvc.With;
import play.mvc.Http.MultipartFormData;
import play.mvc.Http.MultipartFormData.FilePart;
import play.mvc.Result;
import views.html.login;
import views.html.main;

@With(AuthCheck.class)
public class Page extends Controller {

    public static Result index() {
    	return ok(main.render());
    }
    
    public static Result login(String email) {
        return ok(login.render(email));
    }
    
    

}
