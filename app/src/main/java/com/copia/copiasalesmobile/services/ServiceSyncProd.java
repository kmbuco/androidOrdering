package com.copia.copiasalesmobile.services;

import android.app.Service;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.annotation.Nullable;
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
public class ServiceSyncProd extends Service {
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
    private static final String TAG_CODE = "code";
    private static final String TAG_NAME2 = "name";
    private static final String TAG_COMM = "percentage_commission";
    private static final String TAG_DESC = "description";
    private static final String TAG_PRICE = "price";
    private static final String TAG_CAT_ID = "cat_id";
    private static final String TAG_CREATE_DATE = "create_date";
    private static final String TAG_WRITE_DATE = "write_date";
    private static final String TAG_COPIA_ID = "copia_product_id";
    private static final String TAG_IMAGE = "image_id";
    private static final String TAG_TYPE = "prod_type";
    private static final String TAG_VOLUME = "sales_volume";


    private static final String TAG = ServiceSyncProd.class.getSimpleName();

    @Override
    public void onCreate() {
        Log.e("~~~~~~~~``oo``~~~~~~~~", "Service Created");
    }

    @Override
    public void onStart(Intent intent, int startId) {
        //start the service get the dates created
        Log.e(TAG, "++++++++++++++++++++++++++++++++++++++" + "getWriteDatePro Called!");
        dbConnector = new DatabaseConnectorSqlite(getApplicationContext());
        //
        dbConnector.open();
        Cursor result = dbConnector.getWriteDateProd();

        result.moveToFirst();
        int idWrite = result.getColumnIndex("write_date_");
        //int idWrite = Integer.getInteger(result);

//        Log.e("The write date is: ", result.getString(idWrite));

        if (result.getCount() > 0) {
            sWriteSendProducts = String.valueOf(result.getString(idWrite));
        } else {
            sWriteSendProducts = "1970-01-01 00:00:00";
        }


        Log.e(TAG, "+++++++++Product Date+++++++" + sWriteSendProducts);

        result.close();
        dbConnector.close();
        new fetchProducts(sSentIDs).execute(sSentIDs);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    //on service destroy
    @Override
    public void onDestroy() {
        Log.e("_>_>_>_>_>_>_>_>_>", "Finished Syncing !");
        Log.e("_>_>_>_>_>_>_>_>_>", "Finished Syncing !");
        Log.e("_>_>_>_>_>_>_>_>_>", "Finished Syncing !");
        Log.e("_>_>_>_>_>_>_>_>_>", "Finished Syncing !");
        Log.e("_>_>_>_>_>_>_>_>_>", "Finished Syncing !");
        Log.e("_>_>_>_>_>_>_>_>_>", "Finished Syncing !");
        Log.e("_>_>_>_>_>_>_>_>_>", "Finished Syncing !");
        Log.e("_>_>_>_>_>_>_>_>_>", "Finished Syncing !");
        Log.e("_>_>_>_>_>_>_>_>_>", "Finished Syncing !");
        Log.e("_>_>_>_>_>_>_>_>_>", "Finished Syncing !");
        Log.e("_>_>_>_>_>_>_>_>_>", "Finished Syncing !");
        Log.e("_>_>_>_>_>_>_>_>_>", "Finished Syncing !");
        Log.e("_>_>_>_>_>_>_>_>_>", "Finished Syncing !");
        Log.e("_>_>_>_>_>_>_>_>_>", "Finished Syncing !");
        Log.e("_>_>_>_>_>_>_>_>_>", "Finished Syncing !");
        Log.e("_>_>_>_>_>_>_>_>_>", "Finished Syncing !");
        Log.e("_>_>_>_>_>_>_>_>_>", "Finished Syncing !");
        Log.e("_>_>_>_>_>_>_>_>_>", "Finished Syncing !");
        Log.e("_>_>_>_>_>_>_>_>_>", "Finished Syncing !");
        Log.e("_>_>_>_>_>_>_>_>_>", "Finished Syncing !");
        Log.e("_>_>_>_>_>_>_>_>_>", "Finished Syncing !");
        Intent srv = new Intent(getApplicationContext(), ServiceSyncImages.class);
        getApplicationContext().startService(srv);
    }
    public String fetchCopiaIDs() {


        Cursor cursor = dbConnector.getAllCopiaID();
        JSONArray resultSet = new JSONArray();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            int totalColumn = cursor.getColumnCount();
            JSONObject rowObject = new JSONObject();
            for (int i = 0; i < totalColumn; i++) {
                if (cursor.getColumnName(i) != null) {
                    try {
                        if (cursor.getString(i) != null) {
                            //Log.d("TAG_COPIA_IDs", cursor.getString(i));
                            Log.e(TAG, "+++++++++Copia ID+++++++" + cursor.getString(i));
                            rowObject.put(cursor.getColumnName(i),
                                    cursor.getString(i));
                        } else {
                            rowObject.put(cursor.getColumnName(i), "");
                        }
                    } catch (Exception e) {
                        //Log.d("TAG_COPIA_IDs", e.getMessage());
                    }
                }
            }
            resultSet.put(rowObject);
            cursor.moveToNext();
        }
        //Log.d("TAG_COPIA_IDs", resultSet.toString());

        dbConnector.close();

        sAllCategories = resultSet.toString();

        JSONArray arr = null;
        try {
            arr = new JSONArray(sAllCategories);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (arr != null) {
            for (int i = 0; i < arr.length(); i++) {
                JSONObject c = null;
                try {
                    c = arr.getJSONObject(i);
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                String sArrCopiaID = null;
                try {
                    if (c != null) {
                        sArrCopiaID = c.getString("category_id_");
                    }
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                sSentIDs += sArrCopiaID + ",";
                Log.e(TAG,"+++++++++" + sSentIDs);
            }

        }
        try {

        } catch (Exception e) {
            e.printStackTrace();
        }
        return sSentIDs;
    }

    public class fetchProducts extends AsyncTask<String,Void,String> {
        String Syncid;
        public fetchProducts(String idSync){
            Syncid = idSync;
        }


        //private String url_get_categories = "http://54.201.157.222/eCat/getAllCategoriesSync.php";
        private String url_get_products;
        // getting JSON string from URL




        @Override
        protected String doInBackground(String... params) {
            //Sync without images
            //if(sWriteSendProducts == "1970-01-01 00:00:00"){
            url_get_products = "http://52.10.240.79/newSync/SyncProducts.php";
            /*}else{
                url_get_products = "http://54.201.157.222/eCat/fetchProductsReadySync.php";
            }*/
            try {
                Syncid = params[0];
                String[] itemId;
                String ItemId;
                itemId = Syncid.split(",");
                int len = itemId.length;
                Log.e(TAG, "+++++++++Copia length+++++++" + itemId.length+" "+itemId[0]);

                //for (String item:itemId) {
                sWriteSendProducts = "'"+sWriteSendProducts+"'";

                Log.e("MainSync", "+++++++++Product Date+++++++" + sWriteSendProducts);
                Log.d("MainSync", "Create Response for Sync Id: >> " + Syncid);

                List<NameValuePair> params2 = new ArrayList<NameValuePair>();
                //params2.add(new BasicNameValuePair("id", item));
                params2.add(new BasicNameValuePair("write_date", sWriteSendProducts));




                // getting JSON string from URL
                JSONObject json2 = jParser.getJSONFromUrl(url_get_products, "GET", params2);
                Log.e("MainSync", "Create Response for All Products: >> " + json2);

                if (json2.getString(TAG_SUCCESS) != null) {
                    int success2 = json2.getInt(TAG_SUCCESS);
                    if (success2 == 1) {
                        // Results found
                        // Getting Array of Results
                        products2 = json2.getJSONArray(TAG_PRODUCTS);

                        // looping through All Products2
                        List<String> copiaIDList = new ArrayList<String>();
                        for (int j = 0; j < products2.length(); j++) {
                            JSONObject c2 = products2.getJSONObject(j);

                            // Storing each json item in variable
                            final String strCode = c2.getString(TAG_CODE);
                            final String strName2 = c2.getString(TAG_NAME2);
                            final String strPrice = c2.getString(TAG_PRICE);
                            final String strComm = c2.getString(TAG_COMM);
                            final String strCID = c2.getString(TAG_CAT_ID);
                            final String strCreate = c2.getString(TAG_CREATE_DATE);
                            final String strWrite = c2.getString(TAG_WRITE_DATE);
                            final String strCopiaID = c2.getString(TAG_COPIA_ID);
                            final String strImage = c2.getString(TAG_IMAGE);
                            final String strDesc = c2.getString(TAG_DESC);
                            final String strProdType = c2.getString(TAG_TYPE);
                            final String strVolume = c2.getString(TAG_VOLUME);

                            sCheckCopiaID = dbConnector.checkSyncCopiaID(strCopiaID);

                            if (sCheckCopiaID != null && strCopiaID.equals(sCheckCopiaID)) {
                                //Log.e("_>_>_>_>_>_>_>_>_>", "Product Update Called!");
                                //Log.e("_>_>_>_>_>_>_>_>_>", strCode + " " + strName2 + " " + strPrice + " " + strComm + " " + strCID + " " +
                                //        strCreate + " " + strWrite + " " + strCopiaID + " " + strImage + " " + strDesc);
                                Log.e("Prod Type.","++++++++++++++++++#########++++++++++++++++++"+strProdType);
                                Log.e("Prod Volumes.","++++++++++++++++++#########++++++++++++++++++"+strVolume);
                                dbConnector.updateSyncProductTable(strCode, strName2, strPrice, strComm, strCID,
                                        strCreate, strWrite, strCopiaID, strImage, strDesc, strProdType, strVolume);
                            } else {
                                //Log.e("_>_>_>_>_>_>_>_>_>", "Product Insert Called!");
                                //Log.e("_>_>_>_>_>_>_>_>_>", strCode + " " + strName2 + " " + strPrice + " " + strComm + " " + strCID + " " +
                                //        strCreate + " " + strWrite + " " + strCopiaID + " " + strImage + " " + strDesc);
                                Log.e("Prod Type.","++++++++++++++++++#########++++++++++++++++++"+strProdType);
                                Log.e("Prod Volumes.","++++++++++++++++++#########++++++++++++++++++"+strVolume);
                                dbConnector.insertSyncProductTable(strCode, strName2, strPrice, strComm, strCID,
                                        strCreate, strWrite, strCopiaID, strImage, strDesc,strProdType,strVolume);

                            }

                        /*dbConnector.insertProductTable(strCode, strName2, strPrice, strComm, strCID,
                                strCreate, strWrite, strCopiaID, strImage, strDesc);*/
                            copiaIDList.add(strCopiaID);

                        }
                        //dbConnector.deleteNonExistingProducts(copiaIDList);

                    } else {

                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }catch(Exception ex){
                ex.printStackTrace();
            }
            return Syncid;
        }
        protected void onPostExecute(String file_url) {
            stopSelf();
        }


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.e("_>_>_>_>_>_>_>_>_>", "Sync products !");
        }

    }



}
