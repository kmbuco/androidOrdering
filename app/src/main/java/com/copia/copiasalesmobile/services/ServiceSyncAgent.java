package com.copia.copiasalesmobile.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.copia.copiasalesmobile.SQLite.DatabaseConnectorSqlite;
import com.copia.copiasalesmobile.odooConn.OpOdoo;
import com.copia.copiasalesmobile.utilities.Agent;

import java.util.ArrayList;

/**
 * Created by mbuco on 2/15/16.
 */
public class ServiceSyncAgent extends Service {
    DatabaseConnectorSqlite dbConn;
    String write_date;
    ArrayList<Agent> agents;
    //add broadcast
    private static final String CLASS_NAME = ServiceSyncAgent.class.getSimpleName();
    private Context ctx;


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        ctx = getApplicationContext();
        return null;
    }

    @Override
    public void onCreate() {
        Log.e("~~~~~~~~``oo``~~~~~~~~", "Service Created");
    }

    @Override
    public void onStart(Intent intent, int startId) {
        // Perform your long running operations here.
        Log.e("~~~~~~~~``oo``~~~~~~~~", "Service Started");

        dbConn = new DatabaseConnectorSqlite(getApplicationContext());
        new getWriteDate().execute();
    }

    private class getWriteDate extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        @Override
        protected String doInBackground(String... params) {
            write_date = dbConn.getWriteDateAgent();
            return write_date;
        }
        @Override
        protected void onPostExecute(String result){
            super.onPostExecute(result);
            if (write_date != null && !write_date.isEmpty()) {
                // Do Something
                Log.e("Get the results", result);
            }else{
                write_date = "1970-01-01 00:00:00";
            }

            new getAgent().execute();
        }
    }


    private class getAgent extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        @Override
        protected String doInBackground(String... params) {
            OpOdoo operation = new OpOdoo();
            agents = operation.getAllAgents(write_date);
            for(Agent ag:agents){
                //String sUid, String sName, String sWriteDate, String sPhone
                if(ag.getWrite_date()!= null){
                    Log.e("Get them date: ",ag.getWrite_date());
                }
                dbConn.insertAgentTable(ag.getId(),ag.getName(),ag.getWrite_date(), ag.getPhone(),ag.getExperiment_id());
                Log.e("Agent id: ", ag.getId());
            }
            return null;
        }
        @Override
        protected void onPostExecute(String result){
            super.onPostExecute(result);
            Log.e("The Sync is done:" ,"All agents synced: ");
            ProgressMessage prog = new ProgressMessage();
            prog.sendMessage();
        }
    }

    private class ProgressMessage{
        // Send an Intent with an action named "custom-event-name". The Intent sent should
        // be received by the ReceiverActivity.
        private void sendMessage() {
            Log.d("sender", "Broadcasting message");
            Intent intent = new Intent("custom-event-name");
            // You can also include some extra data.
            intent.putExtra("message", "This is my message!");
            LocalBroadcastManager.getInstance(ctx).sendBroadcast(intent);
        }
    }


}
