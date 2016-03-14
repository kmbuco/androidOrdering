package com.copia.copiasalesmobile.utilities;

import android.database.Cursor;
import android.util.Log;

import com.copia.copiasalesmobile.SQLite.DatabaseConnectorSqlite;

/**
 * Created by mbuco on 3/9/16.
 */
public class getOrders {



    public Order  getOrder(DatabaseConnectorSqlite dbconnector,String phone) {

        //orders phone are stored as 0722677889 but sent to erp as +254711677889
        if(phone.contains("+254")){
            phone = phone.replace("+254","0");
        }

        Log.e("the phone is:", phone);
        dbconnector.open();
        Cursor result = dbconnector.getOrder(phone);

        Order order = new Order();
        if (result.moveToFirst()) {


            do {
                order.setDate_time_(result.getString(result
                        .getColumnIndex("date_time_")));
                order.setExpected_delivery_date_(result.getString(result
                        .getColumnIndex("expected_delivery_date_")));
                order.setCust_phone_(result.getString(result
                        .getColumnIndex("cust_phone_")));
                order.setOrder_id(result.getString(result
                        .getColumnIndex("_id")));
                order.setOrder_status_(result.getString(result
                        .getColumnIndex("order_status_")));

                Log.e("The order date: ",order.getDate_time_());
            }while(result.moveToNext());
        }
        dbconnector.close();
        return order;
    }
}
