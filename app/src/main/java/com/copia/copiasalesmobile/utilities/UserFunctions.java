package com.copia.copiasalesmobile.utilities;

import android.content.Context;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;



public class UserFunctions {
	
	private JSONParser2 jsonParser;
	//private static String strLoginLink = "http://54.201.157.222/eCat/loginWithType.php";
	private static String strLoginLink = "http://52.10.240.79/newSync/loginWithCrypt.php";
	//test server
	//private static String strLoginLink = "http://52.89.125.104/newSync/loginWithCrypt.php";

	private static String login_tag = "login";
	
	// constructor
	public UserFunctions(){
		jsonParser = new JSONParser2();
	}
	
	/**
	 * function make Login Request
	 * @param username
	 * @param password
	 * */
	public JSONObject loginUser(String username, String password){
		// Building Parameters
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("tag", login_tag));
		params.add(new BasicNameValuePair("username", username));
		params.add(new BasicNameValuePair("password", password));
		
		JSONObject json = jsonParser.getJSONFromUrl(strLoginLink, "POST", params);
		// return json
	   //Log.e("Awwooohh", json.toString());
		return json;
	}
	
	/**
	 * Function get Login status for sessions
	 * */
	public boolean isUserLoggedIn(Context context){
		/*DatabaseHandler db = new DatabaseHandler(context);
		int count = db.getRowCount();
		if(count > 0){
			// user logged in
			return true;
		}*/
		return false;
	}
	
	/**
	 * Function to logout user
	 * Reset Database
	 * */
	public boolean logoutUser(Context context){
		/*DatabaseHandler db = new DatabaseHandler(context);
		db.resetTables();*/
		return true;
	}
	
}
