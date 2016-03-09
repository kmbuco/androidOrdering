package com.copia.copiasalesmobile.SQLite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.copia.copiasalesmobile.utilities.AgentSearchObject;
import com.copia.copiasalesmobile.utilities.ProdSearchObject;

/**
 * Created by mbuco on 2/11/16.
 */
public class DatabaseConnectorSqlite {
    private static String DB_NAME = "Sales_Copia_Mini_DB_1";
    private SQLiteDatabase database;
    private DatabaseOpenHelperSqlite dbOpenHelper;

    private static final String TAG = DatabaseConnectorSqlite.class.getSimpleName();

    public DatabaseConnectorSqlite(Context context) {
        dbOpenHelper = new DatabaseOpenHelperSqlite(context, DB_NAME, null, 3);
    }

    public void open() throws SQLException {
        //open database in reading/writing mode
        if (database == null)
            database = dbOpenHelper.getWritableDatabase();
    }

    public void close() {
        if (database != null)
            database.close();
        database = null;
    }

    public void insertAgentTable(String sUid, String sName, String sWriteDate, String sPhone, String experiment_id) {
        ContentValues newCon = new ContentValues();
        newCon.put("_id", sUid);
        newCon.put("agent_name", sName);
        newCon.put("write_date_", sWriteDate);
        newCon.put("agent_number", sPhone);
        newCon.put("experiment_id", experiment_id);

        open();
        database.insert("lite_agent", null, newCon);
        close();
    }

    public void updateAgentTable(String sUid, String sName, String sWriteDate, String sPhone, String experiment_id) {
        ContentValues editCon = new ContentValues();
        editCon.put("_id", sUid);
        editCon.put("agent_name", sName);
        editCon.put("write_date_", sWriteDate);
        editCon.put("agent_number", sPhone);
        editCon.put("experiment_id", experiment_id);

        open();
        database.update("lite_agent", editCon, "category_id_" + " = '" + sUid + "'", null);
        close();
    }


    public String getWriteDateAgent() {
        String DATABASE_TABLE = "lite_agent";
        try {
            String sDate = null;
            // select query
            String sql = "";
            sql += "SELECT write_date_ FROM " + DATABASE_TABLE;
            sql += " ORDER BY " + "write_date_" + " DESC";

            open();
            Cursor cursor = database.rawQuery(sql, null);
            Log.e("!!!!!!!!!!!!!!!!!!!!!!!", cursor + " " + sql);
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    sDate = cursor.getString(0);
                    Log.e("Date is: ", sDate);
                }
            }
            cursor.close();
            close();
            return sDate;

        } catch (Exception e) {
            return null;
        }
    }

    public Cursor getWriteDateProd() {
        String DATABASE_TABLE = "products_sync_lite";
        try {
            String sDate = null;
            //select query
            String sql = "";
            sql += "SELECT write_date_ FROM " + DATABASE_TABLE;
            sql += " ORDER BY " + "write_date_" + " desc LIMIT 1";

            open();
            Cursor cursor = database.rawQuery(sql, null);

            Log.e("!!!!!!!!!!!!!!!!!!!!!!!", cursor + " " + sql);
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    sDate = cursor.getString(0);
                }
            }
            close();
            return cursor;

        } catch (Exception e) {
            return null;
        }
    }

    public Cursor getWriteDateProdImage() {
        String DATABASE_TABLE = "product_image";
        try {
            String sDate = null;
            // select query
            String sql = "";
            sql += "SELECT date_time FROM " + DATABASE_TABLE;
            sql += " ORDER BY " + "date_time" + " desc LIMIT 1";

            open();
            Cursor cursor = database.rawQuery(sql, null);
            Cursor result;
            Log.e("!!!!!!!!!!!!!!!!!!!!!!!", cursor + " " + sql);
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    sDate = cursor.getString(0);
                    result = cursor;
                }
            }
            return cursor;

        } catch (Exception e) {
            return null;
        }
    }

    public void insertImageTable(String strImageId, String strProd_id, String strImg, String strDateTime) {
        ContentValues newCon = new ContentValues();
        newCon.put("_id", strImageId);
        newCon.put("product_id_", strProd_id);
        newCon.put("image", strImg);
        newCon.put("date_time", strDateTime);

        open();
        database.insert("product_image", null, newCon);
        close();
    }

    public String checkSyncCopiaID(String strCopiaID) {
        String DATABASE_TABLE = "products_sync_lite";
        try {
            String arrData = null;
            // select query
            String sql = "";
            sql += "SELECT copia_product_id_ FROM " + DATABASE_TABLE;
            sql += " WHERE copia_product_id_ = '" + strCopiaID + "'";

            open();
            Cursor cursor = database.rawQuery(sql, null);
            //Log.e("!!!!!!!!!!!!!!!!!!!!!!!" , cursor + " " + sql);
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    do {
                        arrData = cursor.getString(0);
                    } while (cursor.moveToNext());
                }
            }
            cursor.close();
            close();
            return arrData;

        } catch (Exception e) {
            return null;
        }
    }

    public void updateSyncProductTable(String sCode, String sName, String sPrice, String sComm,
                                       String sCatID, String sCreate, String sWrite, String sCopiaID, String sImage, String sDesc, String sType, String sVolume) {
        ContentValues editCon = new ContentValues();
        editCon.put("code_", sCode);
        editCon.put("name_", sName);
        editCon.put("price_", sPrice);
        editCon.put("comm_", sComm);
        editCon.put("write_date_", sCatID);
        editCon.put("create_date_", sCreate);
        editCon.put("write_date_", sWrite);
        editCon.put("copia_product_id_", sCopiaID);
        editCon.put("image_id_", sImage);
        editCon.put("desc_", sDesc);
        editCon.put("sales_volume", sVolume);
        if (sType == ""){
            sType = "Retail Only";
        }
        editCon.put("prod_type", sType);

        open();
        database.update("products_sync_lite", editCon, "copia_product_id_" + " = '" + sCopiaID + "'", null);
        close();
    }

    public void insertSyncProductTable(String sCode, String sName, String sPrice, String sComm, String sCatID,
                                       String sCreate, String sWrite, String sCopiaID, String sImage, String sDesc,String sType,String sVolumes) {
        ContentValues newCon = new ContentValues();
        newCon.put("code_", sCode);
        newCon.put("name_", sName);
        newCon.put("price_", sPrice);
        newCon.put("comm_", sComm);
        newCon.put("cat_id_", sCatID);
        newCon.put("create_date_", sCreate);
        newCon.put("write_date_", sWrite);
        newCon.put("copia_product_id_", sCopiaID);
        newCon.put("image_id_", sImage);
        newCon.put("desc_", sDesc);
        newCon.put("sales_volume", sVolumes);

        if (!sType.contains("wholesale")){
            sType = "Retail Only";
        }
        Log.e("Prod Type Logged.","+++++++"+sType);
        newCon.put("prod_type", sType);

        open();
        database.insert("products_sync_lite", null, newCon);
        close();
    }

    public Cursor getAllCopiaID() {
        return database.query("categories_lite", new String[]{"category_id_"},
                null, null, null, null, "category_id_");
    }

    public String readId(String code){
        String Id = "";
        String DATABASE_TABLE = "products_sync_lite";
        String FIELD_NAME = "name_";
        String FIELD_CODE = "code_";
        String FIELD_TYPE = "prod_type";
        String sql = "";
        sql += "SELECT copia_product_id_ FROM " + DATABASE_TABLE ;
        sql += "WHERE " + FIELD_CODE + " = " +code;

        open();

        // execute the query
        Cursor cursor = database.rawQuery(sql, null);

        if (cursor.moveToFirst()) {
            Id = cursor.getString(cursor
                    .getColumnIndex("copia_product_id_"));
        }

        return Id;
    }

    /***
     *
     * Search Products
     *
     ***/
    public ProdSearchObject[] readData(String searchTerm) {

        String DATABASE_TABLE = "products_sync_lite";
        String FIELD_NAME = "name_";
        String FIELD_CODE = "code_";
        String FIELD_TYPE = "prod_type";

        //select if wholesale only
        //String type = getAgentLoginDetails().get(0).getUser_type();
        String type  = "Wholesale";
        Log.e("Agent type is ", type);

        // select query
        String sql = "";
        if (type.contains("Wholesale")){
            sql += "SELECT _id, code_, name_, price_, comm_, image_id_, cat_id_, copia_product_id_, desc_, prod_type FROM " + DATABASE_TABLE;
            sql += " WHERE " + FIELD_NAME + " LIKE '" + searchTerm + "%'"
                    //sql += " WHERE " + FIELD_NAME + " LIKE '" + searchTerm + "%'"
                    + " OR " + FIELD_NAME + " LIKE '%" + " " + searchTerm + "%'"
                    + " OR " + FIELD_CODE + " LIKE '" + searchTerm + "%'";
            sql += " ORDER BY " + FIELD_NAME + " DESC";
            sql += " LIMIT 30";
        }else if(type.contains("Retail Only")){

            Log.e("Retail now: ", type);
            sql += "SELECT _id, code_, name_, price_, comm_, image_id_, cat_id_, copia_product_id_, desc_, prod_type FROM " + DATABASE_TABLE;
            sql += " WHERE (" + FIELD_NAME + " LIKE '" + searchTerm + "%'"
                    //sql += " WHERE " + FIELD_NAME + " LIKE '" + searchTerm + "%'"
                    + " OR " + FIELD_NAME + " LIKE '%" + " " + searchTerm + "%'"
                    + " OR " + FIELD_CODE + " LIKE '" + searchTerm + "%'"
                    + " AND " +FIELD_TYPE+ " = 'Retail Only')";
            sql += " ORDER BY " + FIELD_NAME + " DESC";
            sql += " LIMIT 30";
        }


        open();

        // execute the query
        Cursor cursor = database.rawQuery(sql, null);
        int recCount = cursor.getCount();
        Log.e("Test count","The count is: "+  recCount);
        ProdSearchObject[] ObjectItemData = new ProdSearchObject[recCount];
        int x = 0;

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                ProdSearchObject item = new ProdSearchObject();

                String retail = cursor.getString(9);

                Log.e ("Product retail type: ", retail);

                item.idno = cursor.getString(cursor
                        .getColumnIndex("_id"));

                item.productcode = cursor.getString(cursor
                        .getColumnIndex("code_"));

                item.productname = cursor.getString(cursor
                        .getColumnIndex("name_"));

                item.productprice = cursor.getString(cursor
                        .getColumnIndex("price_"));

                item.productcomm = cursor.getString(cursor
                        .getColumnIndex("comm_"));

                // select image
                String FIELD_IMAGE_ID = "_id";
                String ImageCompareId = cursor.getString(cursor
                        .getColumnIndex("image_id_"));

                String sqlImg = "";
                sqlImg += "SELECT image FROM product_image";
                sqlImg += " WHERE " + FIELD_IMAGE_ID + " = '" + ImageCompareId + "'";
                Cursor result = database.rawQuery(sqlImg, null);
                if(result.moveToFirst()) {
                    item.productimg = result.getString(0).trim();
                    //Log.e("Product name is: ", result.getString(0));
                }else{
                    item.productimg = cursor.getString(cursor
                            .getColumnIndex("image_id_"));
                }

                item.productcopiaID = cursor.getString(cursor
                        .getColumnIndex("copia_product_id_"));

                item.productdesc = cursor.getString(cursor
                        .getColumnIndex("desc_"));


                ObjectItemData[x] = item;
                x++;

            } while (cursor.moveToNext());
        }

        cursor.close();
        close();

        return ObjectItemData;
    }


    /***
     *
     * Search Products
     *
     ***/
    public AgentSearchObject[] readAgentData(String searchTerm) {

        String DATABASE_TABLE = "lite_agent";
        String FIELD_NAME = "agent_name";
        String FIELD_PHONE = "agent_number";
        String FIELD_AGENT_ID = "_id";

        //select if wholesale only
        //String type = getAgentLoginDetails().get(0).getUser_type();
        // select query
        String sql = "";


            sql += "SELECT " + FIELD_NAME + ", " + FIELD_AGENT_ID + ", " + FIELD_PHONE + " FROM " + DATABASE_TABLE;
            sql += " WHERE (" + FIELD_NAME + " LIKE '%" + searchTerm + "%'"
                    //sql += " WHERE " + FIELD_NAME + " LIKE '" + searchTerm + "%'"
                    + " OR " + FIELD_PHONE + " LIKE '%" + searchTerm + "%')";
            sql += " ORDER BY " + FIELD_NAME + " DESC";
            sql += " LIMIT 30";


        Log.e("the Order sql : ", sql);

        open();

        // execute the query
        Cursor cursor = database.rawQuery(sql, null);
        int recCount = cursor.getCount();
        Log.e("Test count","The count is: "+  recCount);
        AgentSearchObject[] ObjectItemData = new AgentSearchObject[recCount];
        int x = 0;

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                AgentSearchObject item = new AgentSearchObject();

                item.agentname = cursor.getString(cursor
                        .getColumnIndex(FIELD_NAME));

                item.agentphone = cursor.getString(cursor
                        .getColumnIndex(FIELD_PHONE));

                item.agentId = cursor.getString(cursor.getColumnIndex(FIELD_AGENT_ID));

                ObjectItemData[x] = item;
                x++;

            } while (cursor.moveToNext());
        }

        cursor.close();
        close();

        return ObjectItemData;
    }


    public Cursor getOrder(String sCPhone) {
        return database.query("order_table", new String[]{"_id", "cust_phone_", "date_time_", "type_","expected_delivery_date_","order_status_"}, "cust_phone_" + " = '" + sCPhone + "'", null, null, null, null);
    }

    //Order operations
    public Cursor getOrderID(String sCPhone) {
        return database.query("order_table", new String[]{"_id"}, "cust_phone_" + " = '" + sCPhone + "'", null, null, null, null);
    }


    public Cursor getAllOrderTableLines(String sOrderID) {

        return database.query("order_table_lines", new String[]{"code_", "name_",  "price_", "quantity_","comm_", "total_","copia_product_id_"}, "order_id_" + " = '" + sOrderID + "'",
                null, null, null, "_id");
    }

    public Cursor checkCodeExists(String sCode, String sOrderID) {
        return database.query("order_table_lines", null, "code_" + " = '" + sCode + "'" +
                " AND " + "order_id_" + " = '" + sOrderID + "'", null, null, null, null);
    }

    public Cursor getOneItemTableLines(long id) {
        return database.query("order_table_lines", null, "_id=" + id, null, null, null, null);
    }

    public Cursor getOneItem(String sCode) {
        return database.query("order_table_lines", null, "code_" + " = '" + sCode + "'", null, null, null, null);
    }

    public Cursor getOneItemImage(String sCode) {
        return database.query("products_lite", new String[]{"image_"}, "code_" + " = '" + sCode + "'", null, null, null, null);
    }

//             public void deleteOrderTable()
//             {
//              open();
//              database.delete("order_table", null, null);
//              close();
//             }
//
//             public void deleteOrderLinesTable()
//             {
//              open();
//              database.delete("order_table_lines", null, null);
//              close();
//             }

    public Cursor deleteOrderTable() {
        database.delete("order_table", null, null);
        return null;
    }

    public Cursor deleteOrderLinesTable() {
        database.delete("order_table_lines", null, null);
        return null;
    }

    public void deleteOrder(String sOrderID) {
        open();
        database.delete("order_table", "_id=" + sOrderID, null);
        close();
    }

    public void deleteOrderLines(String sOrderID) {
        open();
        database.delete("order_table_lines", "order_id_" + " = '" + sOrderID + "'", null);
        //database.update("order_table", editCon, "_id" +" = '"+sOrderID+"'", null);
        close();
    }

    public void deleteOrderLinesOneItem(long id) {
        open();
        database.delete("order_table_lines", "_id=" + id, null);
        close();
    }

    public void deleteOrderLinesOneItemUsingCode(String sCode, String sOrderID) {
        open();
        database.delete("order_table_lines", "code_" + " = '" + sCode + "'" +
                " AND " + "order_id_" + " = '" + sOrderID + "'", null);
        close();
    }

    public Cursor getAllOrders() {
        return database.query("order_table", new String[]{"_id", "cust_phone_", "date_time_", "type_","expected_delivery_date_","order_status_"}, null,
                null, null, null, "_id");
    }

    public Cursor getAllPendingOrders() {
        return database.query("order_table", new String[]{"_id", "cust_phone_", "date_time_", "type_","expected_delivery_date_","order_status_"}, "order_status_" + " = 0",
                null, null, null, "_id");
    }
    public Cursor getSentOrders() {
        return database.query("order_table", new String[]{"_id", "cust_phone_", "date_time_", "type_","expected_delivery_date_","order_status_"}, "order_status_" + " = 1",
                null, null, null, "_id");
    }
    public Cursor getSyncOrders() {
        return database.query("order_table", new String[]{"_id", "cust_phone_", "date_time_", "type_","expected_delivery_date_","order_status_"}, "order_status_" + " = 2",
                null, null, null, "_id");
    }



    public Cursor checkPhoneExists(String sCPhone) {
        return database.query("order_table", null, "cust_phone_" + " = '" + sCPhone + "'", null, null, null, null);
    }

    /****
     * Order Table (Order id & Customer phone)
     *
     * @param sDateTime
     */

    public void insertOrderTable(String sPhone, String sDateTime, String sType,String sDeliveryDate,String sStatus) {
        ContentValues newCon = new ContentValues();
        newCon.put("cust_phone_", sPhone);
        newCon.put("date_time_", sDateTime);
        newCon.put("type_", sType);
        newCon.put("expected_delivery_date_", sDeliveryDate);
        newCon.put("order_status_", sStatus);

        open();
        database.insert("order_table", null, newCon);
        close();
    }

    public void updateOrderTable(String sOrderID, String sPhone, String sDateTime,String sDeliveryDate,String sStatus) {
        ContentValues editCon = new ContentValues();
        editCon.put("cust_phone_", sPhone);
        editCon.put("date_time_", sDateTime);
        editCon.put("expected_delivery_date_",sDeliveryDate);
        editCon.put("order_status_", sStatus);

        open();
        //database.update("order_table", editCon, "_id" +" = '"+sOrderID+"'", null);
        database.update("order_table", editCon, "_id=" + sOrderID, null);
        close();
    }

    public void insertOrderTableLines(String sCode, String sName, String sComm, String sPrice,
                                      String sQuantity, String sTotal, String sDateTime, String sOrderId, String sDesc, String sCopiaID) {
        ContentValues newCon = new ContentValues();
        newCon.put("code_", sCode);
        newCon.put("name_", sName);
        newCon.put("comm_", sComm);
        newCon.put("price_", sPrice);
        newCon.put("quantity_", sQuantity);
        newCon.put("total_", sTotal);
        newCon.put("date_time_", sDateTime);
        newCon.put("order_id_", sOrderId);
        newCon.put("desc_", sDesc);
        newCon.put("copia_product_id_", sCopiaID);

        open();
        database.insert("order_table_lines", null, newCon);
        close();
    }

    public void updateOrderTableLines(long id, String sCode, String sName, String sComm, String sPrice,
                                      String sQuantity, String sTotal, String sDateTime, String sOrderId, String sDesc, String sCopiaID) {
        ContentValues editCon = new ContentValues();
        editCon.put("code_", sCode);
        editCon.put("name_", sName);
        editCon.put("comm_", sComm);
        editCon.put("price_", sPrice);
        editCon.put("quantity_", sQuantity);
        editCon.put("total_", sTotal);
        editCon.put("date_time_", sDateTime);
        editCon.put("order_id_", sOrderId);
        editCon.put("desc_", sDesc);
        editCon.put("copia_product_id_", sCopiaID);

        open();
        database.update("order_table_lines", editCon, "_id=" + id, null);
        close();
    }

    public void updateOrderTableLinesUsingCode(String sCode, String sName, String sComm, String sPrice,
                                               String sQuantity, String sTotal, String sDateTime, String sOrderId) {
        ContentValues editCon = new ContentValues();
        editCon.put("code_", sCode);
        editCon.put("name_", sName);
        editCon.put("comm_", sComm);
        editCon.put("price_", sPrice);
        editCon.put("quantity_", sQuantity);
        editCon.put("total_", sTotal);
        editCon.put("date_time_", sDateTime);
        editCon.put("order_id_", sOrderId);
        ;

        open();
        database.update("order_table_lines", editCon, "code_" + " = '" + sCode + "'" +
                " AND " + "order_id_" + " = '" + sOrderId + "'", null);
        close();
    }
    public Cursor checkCommonCopiaIDExists(String sCopiaID) {
        return database.query("common_table", null, "copia_product_id_" + " = '" + sCopiaID + "'", null, null, null, null);
    }
    public Cursor getValueCount(String sCopiaID) {
        return database.query("common_table", new String[]{"count_"}, "copia_product_id_" + " = '" + sCopiaID + "'", null, null, null, null);
    }
    public void insertCommonTable(String sCopiaID, int intCount) {
        ContentValues newCon = new ContentValues();
        newCon.put("copia_product_id_", sCopiaID);
        newCon.put("count_", intCount);

        open();
        database.insert("common_table", null, newCon);

        System.out.println("********Value inserted = " + newCon);
        close();
    }
    public void updateCommonTable(String sCopiaID, int intUpdate) {
        ContentValues editCon = new ContentValues();
        editCon.put("copia_product_id_ ", sCopiaID);
        editCon.put("count_ ", intUpdate);

        open();
        database.update("common_table", editCon, "copia_product_id_" + " = '" + sCopiaID + "'", null);
        System.out.println("********Value Updated = " + editCon);
        close();
    }

}
