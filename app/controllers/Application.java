package controllers;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import models.core.servers.BusinessObjectServer;
import models.util.db.DBHandler;
import play.mvc.Controller;
import play.mvc.Result;

public class Application extends Controller {
	
	public static DBHandler db = new DBHandler();
	public static BusinessObjectServer sss = new BusinessObjectServer();

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
