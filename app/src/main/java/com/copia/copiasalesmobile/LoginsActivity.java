package com.copia.copiasalesmobile;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.copia.copiasalesmobile.SQLite.DatabaseConnectorSqlite;
import com.copia.copiasalesmobile.utilities.Agent;
import com.copia.copiasalesmobile.utilities.AlertDialogManager;
import com.copia.copiasalesmobile.utilities.AlertDialogManagerCancelSettings;
import com.copia.copiasalesmobile.utilities.UserFunctions;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

public class LoginsActivity extends AppCompatActivity {

    private EditText inputUsername;
    private EditText inputPassword;
    SharedPreferences sharedpreferences;
    DatabaseConnectorSqlite database;
    private ProgressDialog mProgressDialog;
    public static final String LOGINPREFERENCES = "LoginPrefs" ;
    public static final String MyPREFERENCES = "MyPrefs" ;

    // Alert Dialog Manager
    private AlertDialogManagerCancelSettings alertDialogManagerCancelSettings;
    private AlertDialogManager alertDialogManager;

    private String mStrUserName, mStrPassword;
    DatabaseConnectorSqlite dbConn;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logins);

        database = new DatabaseConnectorSqlite(LoginsActivity.this);
        dbConn = new DatabaseConnectorSqlite(LoginsActivity.this);
        // Session Manager
        //mSessionManager = new SessionManager(getApplicationContext());

        alertDialogManager = new AlertDialogManager();
        alertDialogManagerCancelSettings = new AlertDialogManagerCancelSettings();

        // Importing all assets like buttons, text fields
        inputUsername = (EditText) findViewById(R.id.loginUsername);
        inputPassword = (EditText) findViewById(R.id.loginPassword);

        Button btnLogin = (Button) findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (inputUsername.getText().length() != 0 && inputPassword.getText().length() != 0) {

                    mStrUserName = inputUsername.getText().toString().trim();
                    mStrPassword = inputPassword.getText().toString();
                    sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedpreferences.edit();

                    /*editor.putString("UserName", null);
                    editor.putString("Password", null);
                    editor.commit();*/


                    //Check shared preference if user is logged in
                    String  userName = sharedpreferences.getString("UserName", null);
                    String  password = sharedpreferences.getString("Password", null);
                    //check from database
                    ArrayList<Agent> AgentLogin= new ArrayList<>();
                    AgentLogin = database.getAgentLoginDetails();
                    String Logged = "0";

                    if(AgentLogin.size()>0){
                        //encode input
                        String pass = mStrPassword;
                        String encrData = "";
                        String encData = "";
                        try{
                            byte[] data = pass.getBytes("UTF-8");
                            encData = Base64.encodeToString(data, Base64.DEFAULT);
                        }catch(UnsupportedEncodingException e){

                        }

                        String user ="";
                        String psWrd = "";

                        for(Agent agent:AgentLogin){
                            user = agent.getUser_name();
                            psWrd = agent.getPassword();
                            Log.e("Pass: ", psWrd +" Username is: "+user);
                            Log.e("Password: ", encData +" Username of agent: "+mStrUserName + " Not Encrypted pass: "+mStrPassword);
                        }

                        ArrayList<Agent> agntLst = dbConn.getAgentLoginDetails();
                        String strPass = "";
                        if (agntLst.size()>0){
                            strPass = agntLst.get(0).getPassword();
                            user = agntLst.get(0).getUser_name();
                        }


                        //
                        if (user.equals(mStrUserName) && strPass.equals(encData)) {
                            Log.e("Logging Status", "Passed the login");
                            UserFunctions userFunction = new UserFunctions();

                            //for user sessions
                            //DatabaseHandler db = new DatabaseHandler(getApplicationContext());
                            //JSONObject json = userFunction.loginUser(mStrUserName, mStrPassword);
                            // Clear all previous data in database
                            userFunction.logoutUser(getApplicationContext());
                            String KEY_UID = "uid";
                            String KEY_USERNAME = "username";

                            /*db.addUser(password,
                                    userName);*/

                            //mSessionManager.createLoginSession("1", mStrPassword);

                            Intent dd = new Intent(getApplicationContext(), HomeScreen.class);
                            dd.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(dd);
                        }else {
                            // check for Internet status
                            if (isOnline()) {
                                new Login().execute();
                            } else {
                                // Internet connection is not present
                                // Ask user to connect to Internet
                                alertDialogManagerCancelSettings.showAlertDialog(LoginsActivity.this,
                                        "No Internet Connection",
                                        "E-Catalogue requires a network connection to login, \n" +
                                                "Please confirm data connection is on and you have sufficient credit.", false);
                            }
                        }
                    }else if(userName != null && password != null) {
                        Log.e("String Username: ", userName + " " + mStrUserName);
                        Log.e("String password: ", password + " " + mStrPassword);

                        //encode input
                        String pass = mStrPassword;
                        byte[] byteArray;
                        String encData = "";
                        try{
                            byte[] data = pass.getBytes("UTF-8");
                            encData = Base64.encodeToString(data, Base64.DEFAULT);
                        }catch(UnsupportedEncodingException e){

                        }


                        if (userName.trim().equals(mStrUserName) && password.equals(encData)) {
                            Log.e("Logging Status", "Passed the login");
                            UserFunctions userFunction = new UserFunctions();
                            //DatabaseHandler db = new DatabaseHandler(getApplicationContext());
                            //JSONObject json = userFunction.loginUser(mStrUserName, mStrPassword);
                            // Clear all previous data in database
                            userFunction
                                    .logoutUser(getApplicationContext());
                            String KEY_UID = "uid";
                            String KEY_USERNAME = "username";
                            /*db.addUser(password,
                                    userName);*/

                            //mSessionManager.createLoginSession("1", mStrPassword);

                            Intent dd = new Intent(getApplicationContext(), HomeScreen.class);
                            dd.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(dd);

                        }
                        else {
                            // check for Internet status
                            if (isOnline()) {
                                new Login().execute();
                            } else {
                                // Internet connection is not present
                                // Ask user to connect to Internet
                                alertDialogManagerCancelSettings.showAlertDialog(LoginsActivity.this,
                                        "No Internet Connection",
                                        "E-Catalogue requires a network connection to login, \n" +
                                                "Please confirm data connection is on and you have sufficient credit.", false);
                            }
                        }
                    }else {
                        // check for Internet status
                        if (isOnline()) {
                            new Login().execute();
                        } else {
                            // Internet connection is not present
                            // Ask user to connect to Internet
                            alertDialogManagerCancelSettings.showAlertDialog(LoginsActivity.this,
                                    "No Internet Connection",
                                    "E-Catalogue requires a network connection to login, \n" +
                                            "Please confirm data connection is on and you have sufficient credit.", false);
                        }
                    }
                } else {
                    alertDialogManager.showAlertDialog(LoginsActivity.this,
                            "Login failed...",
                            "Please make sure you have entered the required fields. Thank you :-)", false);
                }
            }

        });

    }

    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }

    /**
     * Login AsyncTask class that performs app validation.
     */
    class Login extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressDialog = new ProgressDialog(LoginsActivity.this);
            mProgressDialog.setMessage("Authenticating...Please Wait!");
            mProgressDialog.setIndeterminate(false);
            mProgressDialog.setCancelable(true);
            mProgressDialog.show();
        }

        @Override
        protected String doInBackground(String... args) {

            UserFunctions userFunction = new UserFunctions();
            Log.e("Password is: ","Them password " + mStrPassword);
            Log.e("Username is: ", "Them username " + mStrUserName);
            JSONObject json = userFunction.loginUser(mStrUserName, mStrPassword);
            // check for login response
            try {
                String KEY_SUCCESS = "success";

                if (json.getString(KEY_SUCCESS) != null) {
                    // loginErrorMsg.setText("");
                    String res = json.getString(KEY_SUCCESS);

                    if (Integer.parseInt(res) == 1) {
                        // user successfully logged in
                        // Store user details in SQLite Database
                        //DatabaseHandler db = new DatabaseHandler(getApplicationContext());
                        JSONObject json_user = json.getJSONObject("user");

                        // Clear all previous data in database
                        userFunction
                                .logoutUser(getApplicationContext());
                        String KEY_UID = "uid";
                        String KEY_USERNAME = "username";
                        String KEY_PARTNER_ID="partner_id";

                        String user_id = json.getString(KEY_UID);
                        String partner_id = json.getString(KEY_PARTNER_ID);
                        /*db.addUser(json.getString(KEY_UID),
                                json_user.getString(KEY_USERNAME));*/

                        String pass = mStrPassword;
                        byte[] byteArray;
                        String encData = "";
                        try{
                            byte[] data = pass.getBytes("UTF-8");
                            encData = Base64.encodeToString(data, Base64.DEFAULT);
                        }catch(UnsupportedEncodingException e){

                        }
                        Log.e("The user is: ", json_user.getString(KEY_USERNAME)+ " " +user_id+ " " +partner_id);
                        database.insertLoginAgent(json_user.getString(KEY_USERNAME),encData,json.getString("experiment"),user_id,partner_id);

                        Intent dd = new Intent(getApplicationContext(), HomeScreen.class);
                        dd.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(dd);

                        /*String source = "password";
                        byte[] byteArray;
                        try {
                            byteArray = source.getBytes("UTF-16");
                            System.out.println(new String(Base64.decode(Base64.encode(byteArray,
                                    Base64.DEFAULT), Base64.DEFAULT)));
                        } catch (UnsupportedEncodingException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }*/



                        /*editor.putString("UserName", json_user.getString(KEY_USERNAME));
                        editor.putString("Password", encData);
                        editor.commit();*/


                        //enter the user details into the login database... this allows for offline login and usertype
                        // eg. wholesale or retail identification



                       // mSessionManager.createLoginSession("1", mStrPassword);

                        // Launch CheckDriver
                        //Intent intent = new Intent(getApplicationContext(), CheckUserStatus.class);
                        //getApplicationContext().startService(intent);
                        finish();
                    } else {
                        // Error in login
                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                alertDialogManager.showAlertDialog(LoginsActivity.this,
                                        "Login Error",
                                        "Incorrect Username/Password. Please Try Again!", false);
                            }
                        });
                    }
                } else {
                    alertDialogManager.showAlertDialog(LoginsActivity.this,
                            "Login Error",
                            "Failed to Login. Please Try Again!", false);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String file_url) {
            // dismiss the dialog once done
            SharedPreferences prefs= PreferenceManager.getDefaultSharedPreferences(LoginsActivity.this.getApplicationContext());
            SharedPreferences.Editor editor=prefs.edit();
            editor.putBoolean("is_new_user",true);
            editor.commit();
            mProgressDialog.dismiss();
        }
    }

    public boolean isOnline() {

        Runtime runtime = Runtime.getRuntime();
        try {

            Process ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8");
            int exitValue = ipProcess.waitFor();
            return (exitValue == 0);

        } catch (IOException e)          { e.printStackTrace(); }
        catch (InterruptedException e) { e.printStackTrace(); }

        return false;
    }


}
