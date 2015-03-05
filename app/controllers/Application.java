package controllers;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import models.util.db.DBHandler;
import models.util.db.SAPServerSimulator;
import play.mvc.*;
import play.Logger;
import views.html.*;

public class Application extends Controller {
	
	public static DBHandler db = new DBHandler();
	public static SAPServerSimulator sss = new SAPServerSimulator();

    public static String sha1(String input) {
    	if (input == null) {
    		return "";
    	}
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
