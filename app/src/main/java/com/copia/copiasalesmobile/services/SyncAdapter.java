package com.copia.copiasalesmobile.services;

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SyncResult;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;

import com.copia.copiasalesmobile.SQLite.DatabaseConnectorSqlite;
import com.copia.copiasalesmobile.utilities.MyApp;
import com.copia.copiasalesmobile.utilities.productLine;

import java.util.ArrayList;

/**
 * Created by mbuco on 3/21/16.
 */
public class SyncAdapter extends AbstractThreadedSyncAdapter {
    // Global variables
    // Define a variable to contain a content resolver instance
    ContentResolver mContentResolver;

    public SyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        /*
         * If your app uses a content resolver, get an instance of it
         * from the incoming Context
         */
        mContentResolver = context.getContentResolver();
    }

    public SyncAdapter(Context context, boolean autoInitialize, boolean allowParallelSyncs) {
        super(context, autoInitialize, allowParallelSyncs);
        /*
         * If your app uses a content resolver, get an instance of it
         * from the incoming Context
         */
        mContentResolver = context.getContentResolver();

    }

    @Override
    public void onPerformSync(Account account, Bundle bundle, String s, ContentProviderClient contentProviderClient, SyncResult syncResult) {
        Context context = MyApp.getContext();
        DatabaseConnectorSqlite dbConnect = new DatabaseConnectorSqlite(context);
        Cursor cursor  = dbConnect.getAllPendingOrders();
        ArrayList<String> orderIds = new ArrayList();
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                String orderId = cursor.getString(cursor.getColumnIndex("_id"));
                String cust_phone_ = cursor.getString(cursor.getColumnIndex("cust_phone_"));
                Log.e("Order ID is: ", orderId);
                orderIds.add(orderId);
                //get orderLines for each order id code and quantity
                ArrayList<productLine> prodLines = new ArrayList<>();
                    Cursor cursorOrderLines = dbConnect.getAllOrderTableLines(orderId);
                    if (cursorOrderLines != null) {
                        if (cursor.moveToFirst()) {
                            //"code_", "name_",  "price_", "quantity_","comm_", "total_","copia_product_id_"
                            productLine prodLine = new productLine();
                            String code = cursor.getString(cursor.getColumnIndex("code_"));
                            String name = cursor.getString(cursor.getColumnIndex("name_"));
                            String quantity = cursor.getString(cursor.getColumnIndex("quantity_"));
                            String copia_product_id = cursor.getString(cursor.getColumnIndex("copia_product_id_"));
                            prodLine.setQuantity_(quantity);
                            prodLine.setCode_(copia_product_id);
                            Log.e("Order ID is: ", code);
                            prodLines.add(prodLine);
                        }
                    }

            }
        }
        cursor.close();
    }
}
