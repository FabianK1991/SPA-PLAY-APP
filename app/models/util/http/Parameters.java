package models.util.http;

import java.util.Map;

import play.Logger;
import play.mvc.Controller;

public class Parameters {
	
	private static Map<String,String[]> requestData = null;
	
	/**
	 * Gets the Parameters for a given key.
	 * @author Christian
	 * @param key The key of the part of the POST.
	 * @return The parameters of the key.
	 */
	public static String get(String key, int num){
		String[] data = getAll(key);
		
		return data[num];
	}
	
	public static String get(String key){
		return get(key, 0);
	}
	
	public static String[] getAll(String key) {
		try {
			if (requestData == null) {
				requestData = Controller.request().body().asFormUrlEncoded();
			}
			if (requestData == null) {
				requestData = Controller.request().body().asMultipartFormData().asFormUrlEncoded();
			}
			if(requestData.containsKey(key)){
				return requestData.get(key);
			}
		}
		catch(Exception e) {
			
		}
		return new String[0];
	}
	
	public static void clearRequestData() {
		requestData = null;
	}
}