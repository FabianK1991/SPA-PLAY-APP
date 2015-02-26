package controllers;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import models.util.db.DBHandler;
import play.mvc.*;

import views.html.*;

public class Application extends Controller {
	
	public static DBHandler db = new DBHandler();

	/*
	 * TODO
	 */
    public static Result index() {
    	if(AuthController.check()) {
    		return ok(index.render("Your new application is ready."));
    	}
    	else {
    		return redirect("/auth");
    	}
        
    }

    public static String sha1(String input) {
        MessageDigest mDigest = null;
		try {
			mDigest = MessageDigest.getInstance("SHA1");
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        byte[] result = mDigest.digest(input.getBytes());
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < result.length; i++) {
            sb.append(Integer.toString((result[i] & 0xff) + 0x100, 16).substring(1));
        }
         
        return sb.toString();
    }
}
