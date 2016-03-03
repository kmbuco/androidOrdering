package com.copia.copiasalesmobile.services;

import android.app.Service;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.util.Pair;
import android.util.Log;

import com.copia.copiasalesmobile.SQLite.DatabaseConnectorSqlite;
import com.copia.copiasalesmobile.utilities.JSONParser;


import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mbuco on 2/18/16.
 */
public class ServiceSyncImages extends Service {

    JSONParser jParser = new JSONParser();
    public DatabaseConnectorSqlite dbConnector;

    public String sAllCategories = "";
    public String sCheckCopiaID = "";
    public String sSentIDs = "";
    public String sWriteSendProducts = "";

    //productsActivity JSONArray
    JSONArray products = null;
    JSONArray products2 = null;

    //JSON Node Products names
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_PRODUCTS = "products";
    private static final String TAG_IMAGE_ID = "image_id";
    private static final String TAG_PRODUCT_ID = "product_id";
    private static final String TAG_IMAGE= "image";
    private static final String TAG_DATE_TIME = "date_time";

    String url_get_images;

    private static final String TAG = ServiceSyncImages.class.getSimpleName();


    @Override
    public void onCreate() {
        Log.e("~~~~~~~~``oo``~~~~~~~~", "Service Created");
    }

    @Override
    public void onStart(Intent intent, int startId) {
        Log.e(TAG, "++++++++++++++++++++++++++++++++++++++" + "getWriteDatePro Called!");
        dbConnector = new DatabaseConnectorSqlite(ServiceSyncImages.this);

        dbConnector.open();
        //get last date synced for images
        Cursor result = dbConnector.getWriteDateProdImage();
        result.moveToFirst();
        int idWrite = result.getColumnIndex("date_time");

        if (result.getCount() > 0) {
            sWriteSendProducts = String.valueOf(result.getString(idWrite));
        } else {
            sWriteSendProducts = "1970-01-01 00:00:";
        }


        Log.e(TAG, "+++++++++Product Date+++++++" + sWriteSendProducts);

        result.close();
        dbConnector.close();
        new fetchImages().execute();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private class fetchImages extends AsyncTask<String,Void,String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.e("_>_>_>_>_>_>_>_>_>", "Sync products !");
        }

        @Override
        protected String doInBackground(String... params) {
            sWriteSendProducts = "'"+sWriteSendProducts+"'";

            url_get_images = "http://52.10.240.79/newSync/Sync_Images.php";
            try{
                Log.e("MainSync", "+++++++++Product Date+++++++" + sWriteSendProducts);



                List<NameValuePair> params2 = new ArrayList<NameValuePair>();
                //params2.add(new BasicNameValuePair("id", item));
                params2.add(new BasicNameValuePair("write_date", sWriteSendProducts));


                // getting JSON string from URL
                JSONObject json2 = jParser.getJSONFromUrl(url_get_images, "GET", params2);
                Log.d("MainSync", "Create Response for All Products: >> " + json2);

                if (json2.getString(TAG_SUCCESS) != null) {
                    int success2 = json2.getInt(TAG_SUCCESS);
                    if (success2 == 1) {
                        products2 = json2.getJSONArray(TAG_PRODUCTS);
                        for (int j = 0; j < products2.length(); j++) {
                            JSONObject c2 = products2.getJSONObject(j);

                            // Storing each json item in variable
                            final String strImageId = c2.getString(TAG_IMAGE_ID);
                            final String strProd_id = c2.getString(TAG_PRODUCT_ID);
                            final String strImg = c2.getString(TAG_IMAGE);
                            final String strDateTime = c2.getString(TAG_DATE_TIME);

                            dbConnector.insertImageTable(strImageId, strProd_id, strImg, strDateTime);
                        }
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(String image) {
            stopSelf();
        }

    }
    //on service destroy
    @Override
    public void onDestroy() {
        Log.e("_>_>_>_>_>_>_>_>_>", "Finished Syncing images!");
        Log.e("_>_>_>_>_>_>_>_>_>", "Finished Syncing images!");
        Log.e("_>_>_>_>_>_>_>_>_>", "Finished Syncing images!");
        Log.e("_>_>_>_>_>_>_>_>_>", "Finished Syncing images!");
        Log.e("_>_>_>_>_>_>_>_>_>", "Finished Syncing images!");
        Log.e("_>_>_>_>_>_>_>_>_>", "Finished Syncing images!");
        Log.e("_>_>_>_>_>_>_>_>_>", "Finished Syncing images!");
        Log.e("_>_>_>_>_>_>_>_>_>", "Finished Syncing images!");
        Log.e("_>_>_>_>_>_>_>_>_>", "Finished Syncing images!");
        Log.e("_>_>_>_>_>_>_>_>_>", "Finished Syncing images!");
        Log.e("_>_>_>_>_>_>_>_>_>", "Finished Syncing images!");
        Log.e("_>_>_>_>_>_>_>_>_>", "Finished Syncing images!");
        Log.e("_>_>_>_>_>_>_>_>_>", "Finished Syncing images!");
        Log.e("_>_>_>_>_>_>_>_>_>", "Finished Syncing images!");
        Log.e("_>_>_>_>_>_>_>_>_>", "Finished Syncing images!");
        Log.e("_>_>_>_>_>_>_>_>_>", "Finished Syncing images!");
        Log.e("_>_>_>_>_>_>_>_>_>", "Finished Syncing images!");
        Log.e("_>_>_>_>_>_>_>_>_>", "Finished Syncing images!");
        Log.e("_>_>_>_>_>_>_>_>_>", "Finished Syncing images!");
        Log.e("_>_>_>_>_>_>_>_>_>", "Finished Syncing images!");
        Log.e("_>_>_>_>_>_>_>_>_>", "Finished Syncing images!");
    }
}
