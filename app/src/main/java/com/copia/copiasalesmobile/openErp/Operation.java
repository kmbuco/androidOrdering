package com.copia.copiasalesmobile.openErp;

import android.util.Log;

import com.copia.copiasalesmobile.SQLite.DatabaseConnectorSqlite;

import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

/**
 * Created by mbuco on 3/14/16.
 * will define various open erp functions
 * read, write, search, delete, update
 */
public class Operation {
    String mode = "ecatalog";
    XmlRpcClient client;
    static final String db = "copiaERP";
    static final String password = "@dm1n2014_cop1a_erp";
    DatabaseConnectorSqlite dbconnector;
    int port;
    String ip;

    //must pass the database connector string
    public Operation(String ip, int port, DatabaseConnectorSqlite dbconnector){
        this.dbconnector = dbconnector;

        this.port = port;
        if(ip.equals("")){
            this.ip = "52.89.125.104";
        }else{
            this.ip = ip;
        }
        if(port == 0){
            this.port = 8069;
        }else{
            this.port = port;
        }

        //initialize the client
        client = new XmlRpcClient();
        XmlRpcClientConfigImpl clientConfig = new XmlRpcClientConfigImpl();
        clientConfig.setEnabledForExtensions(true);
        try {
            clientConfig.setServerURL(new URL("http", this.ip, this.port, "/xmlrpc/object"));
        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        client.setConfig(clientConfig);
    }


    /***********************search*****************************/
    //takes list of parameters in the for of list of objetcs eg
    // ArrayList<Object[]> listparam = new ArrayList<>();
    //Object[] exampleObjectParam = { "agent", "=", "True" };
    //listparam.add(exampleObjectParam);
    //returns an arraylist of ids
    public ArrayList search(ArrayList<Object[]> listparam, String tableName){
        ArrayList listIds = new ArrayList();
        try{

            Vector<Object> arg2 = new Vector<Object>();
            arg2.add(db);
            arg2.add(1); //assumes the first user id is admin
            arg2.add(password); // The Database password
            arg2.add(tableName);
            arg2.add("search");
            arg2.add(listparam);

            Log.e("The search Arguments: ", arg2.toString());

            Object[] ids = (Object[]) client.execute("execute", arg2);

            //if you want to you can print out the results here
            for (Object obj : ids) {
                int a = Integer.parseInt(obj.toString());
                listIds.add(a);
                //System.out.println(a);
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return listIds;
    }

    /***************read********************/
    //object reading params
    //Object[] params = { "name","list_price","id","taxes_id","weight","property_product_pricelist"};
    //list ids is arraylist of IDs
    //eg.listIds = new ArrayList<>();
    public Object [] read(Object[] params,ArrayList listIds, String tableName){
        Object[] hm = null;
        try{
            Vector<Object> arg = new Vector<Object>();
            arg.add(db);
            arg.add(1); //assumes the first user id is admin
            arg.add(password); // The Database password
            arg.add(tableName);
            arg.add("read");
            arg.add(listIds);
            arg.add(params);
            Log.e("Debug Read Arguments: ", arg.toString());
            hm = (Object[]) client.execute("execute", arg);
        }catch(Exception e){
            e.printStackTrace();
        }
        return hm;
    }


    /*********************************create***************************************/
    public int create(HashMap<String, Object> vals, String tableName){
        int id = 0;
        Object res = null;
        Object[] params = new Object[]{db, 1, password, tableName, "create", vals};
        try{
            Log.e("The output is: ", params.toString());
            res = client.execute("execute", params);
            id = Integer.parseInt(res.toString());

            Log.e("the order id is: ", res.toString());
        }catch(Exception e){
            e.printStackTrace();
        }
        return id;
        //HashMap<String, Object> vals = new HashMap<String, Object>();
    }



}
