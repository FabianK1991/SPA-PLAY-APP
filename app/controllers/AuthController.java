package controllers;

import java.util.Date;

import models.core.exceptions.ProcessModelNotFoundException;
import models.core.process.ProcessModel;
import models.util.db.DBHandler;
import models.util.http.Parameters;
import models.util.session.Session;
import models.util.session.User;
import play.Logger;
import play.mvc.Controller;
import play.mvc.Http.Cookie;
import play.mvc.Result;
import play.mvc.With;
import views.html.pages.main;
import views.html.ajax_concat;
import views.html.auth_header;

@With(ActionController.class)
public class AuthController extends Controller {
	
	private static Session s;
	
	public static Session getSession(){
		return AuthController.s;
	}
	
	public static User getUser(){
		return AuthController.s.getUser();
	}
	
	/**
	 * Checks if the session is still valid.
	 * @author Christian
	 * @return true if session still valid, false otherwise.
	 */
	public static boolean check(){
		Cookie sessKey = request().cookies().get("sesskey");
		Cookie sessId = request().cookies().get("sessid");
		
		if (sessKey != null) {
			AuthController.s = new Session(sessId.value());
			Long time = new Date().getTime();
			
			if (sessKey.value().equals(Application.sha1(AuthController.s.getKey())) && time - AuthController.s.getUpdate().getTime()<1000*60*20) {
				AuthController.s.setUpdate(new Date());
				Application.db.update(AuthController.s, "update" , DBHandler.format.format(time));
				
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Logs the user into the system.
	 * @author Christian
	 * @return "1" if login was successful, "0" if Login failed.
	 */
	public static Result login(){
		if(!AuthController.check()){
			User u = new User();
			u.init(Parameters.get("email"));
			Logger.debug(Parameters.get("email"));
			
			if(u.createSession(Parameters.get("passwd"))){
				AuthController.s = u.getSession();
				
				return ok(ajax_concat.render(main.render(), auth_header.render(1), routes.Page.index().url()));
			}
		}
		return badRequest("Login failed!");
	}
	
	/**
	 * Logs the user out of the system.
	 * @author Christian
	 * @return A redirect to the Login-Screen.
	 */
	public static Result logout(){
		AuthController.s = new Session(request().cookies().get("sessid").value());
		AuthController.s.getUser().setSession(null);
		Application.db.update(AuthController.s.getUser(), "session", "");
		Application.db.delete(AuthController.s);
		response().discardCookie("sessid");
		response().discardCookie("sesskey");
		
		return redirect("/login");
	}
}
