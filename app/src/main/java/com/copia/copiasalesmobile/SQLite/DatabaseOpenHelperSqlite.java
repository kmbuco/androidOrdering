package com.copia.copiasalesmobile.SQLite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by mbuco on 2/11/16.
 */
public class DatabaseOpenHelperSqlite extends SQLiteOpenHelper {

    public DatabaseOpenHelperSqlite(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createAgents= "CREATE TABLE lite_agent(_id, agent_name, agent_number, write_date_,experiment_id);";
        String createUser= "CREATE TABLE lite_user(_id, user_name, password, write_date_);";
        String createProductImage= "CREATE TABLE product_image(_id, product_id_, image, date_time);";
        String createSyncProductLite = "CREATE TABLE products_sync_lite (_id integer primary key autoincrement, code_, name_, price_, comm_, cat_id_, create_date_, write_date_, copia_product_id_ INTEGER, image_id_, desc_,prod_type, sales_volume);";
        String createOrderTable = "CREATE TABLE order_table (_id integer primary key autoincrement, cust_phone_, date_time_, type_, expected_delivery_date_, order_status_);";
        String createOrderTableLines = "CREATE TABLE order_table_lines (_id integer primary key autoincrement, code_, name_, price_, comm_, quantity_, total_, date_time_, order_id_, desc_, copia_product_id_);";
        String createCommonTable = "CREATE TABLE common_table (_id integer primary key autoincrement, copia_product_id_, count_);";




        db.execSQL(createAgents);
        db.execSQL(createUser);
        db.execSQL(createSyncProductLite);
        db.execSQL(createProductImage);
        db.execSQL(createOrderTable);
        db.execSQL(createOrderTableLines);
        db.execSQL(createCommonTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + "lite_agent");
        db.execSQL("DROP TABLE IF EXISTS " + "lite_user");
        db.execSQL("DROP TABLE IF EXISTS " + "product_image");
        db.execSQL("DROP TABLE IF EXISTS " + "products_sync_lite");
        db.execSQL("DROP TABLE IF EXISTS " + "order_table");
        db.execSQL("DROP TABLE IF EXISTS " + "order_table_lines");
        db.execSQL("DROP TABLE IF EXISTS " + "common_table");


        // Create tables again
        onCreate(db);
    }

}
