package controllers;

import play.Logger;
import play.mvc.*;
import views.html.*;

public class Page extends Controller {

    public static Result index() {Application.db.connect();
        return ok(index.render("Your new application is ready."));
    }
    
    public static Result login() {
        return ok(login.render("Your new application is ready."));
    }
    /*
    public static Result main() {
        return ok(main.render("Your new application is ready."));
    }*/

}
