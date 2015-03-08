package models.util.http;

import java.util.Map;
import play.mvc.Controller;

public class Parameters {
	
	private static Map<String,String[]> requestData = null;
	
	/**
	 * Gets the Parameters for a given key.
	 * @author Christian
	 * @param key The key of the part of the POST.
	 * @return The parameters of the key.
	 */
	public static String get(String key){
		if (requestData == null) {
			requestData = Controller.request().body().asFormUrlEncoded();
		}
		if (requestData == null) {
			requestData = Controller.request().body().asMultipartFormData().asFormUrlEncoded();
		}
		
		if(requestData.containsKey(key)){
			return requestData.get(key)[0];
		}
		return "";
	}
	
	public static void clearRequestData() {
		requestData = null;
	}
}