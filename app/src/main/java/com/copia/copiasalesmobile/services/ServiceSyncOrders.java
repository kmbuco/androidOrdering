package com.copia.copiasalesmobile.services;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.copia.copiasalesmobile.SQLite.DatabaseConnectorSqlite;
import com.copia.copiasalesmobile.openErp.Functions;
import com.copia.copiasalesmobile.utilities.OrderProdLines;
import com.copia.copiasalesmobile.utilities.productLine;

import java.util.ArrayList;

/**
 * Created by mbuco on 3/22/16.
 */
public class ServiceSyncOrders extends Service {
    DatabaseConnectorSqlite dbConn;
    ArrayList<OrderProdLines> ordProdLine;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        Log.e("~~~~~~~~``oo``~~~~~~~~", "Service Created");
    }

    @Override
    public void onStart(Intent intent, int startId) {
        Log.e("~~~~~~~~``oo``~~~~~~~~", "Service Started: ");
        ordProdLine = new ArrayList<>();
        dbConn = new DatabaseConnectorSqlite(getApplicationContext());
        sendOrder send = new sendOrder();
        send.execute();
    }

    private class sendOrder extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {
            ordProdLine = dbConn.getAllSyncOrders();
            String productIds;
            String agentIds;
            String phone;
            String quantities;
            String date_delivery;
            String quantity = "";
            String prodId = "";
            String local_order_id = "";

            for(OrderProdLines ord: ordProdLine){
                //String productIds, String agentIds, String phone, String quantities,String date_delivery


                phone = ord.getScust_phone_();
                date_delivery = ord.getSexpected_delivery_date_();
                agentIds = ord.getAgent_id_();
                int x = 0;
                for(productLine prdLine:ord.getArrProdLines()){
                    if(x==0){
                        prodId = prdLine.getCopia_product_id_();
                        quantity = prdLine.getQuantity_();
                    }else{
                        prodId += prdLine.getCopia_product_id_();
                        quantity += prdLine.getQuantity_();
                    }
                }
                Log.e("Order Deatails: ", "Prod Id :"+prodId + " agent ID "+agentIds+ " phone : "+phone+ " quantity "+ quantity + " Delivery : "+date_delivery);
                Functions func = new Functions(dbConn);
                int order_id = func.createOrder(prodId,agentIds, phone, quantity, date_delivery,local_order_id);
                Log.e("The Order Id:", Integer.toString(order_id));
            }
            return null;
        }
    }

    //on service destroy
    @Override
    public void onDestroy() {
        Log.e("_>_>_>_>_>_>_>_>_>", "Finished Syncing Orders !");
    }
}
