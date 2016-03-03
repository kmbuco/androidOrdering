package com.copia.copiasalesmobile.openErp;

import android.util.Log;

import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Vector;

/**
 * Created by mbuco on 2/29/16.
 */
public class Functions {


    static final String db = "copiaERP";
    static final String host = "http://52.89.125.104";
    static final int port = 8069;
    static final String password = "@dm1n2014_cop1a_erp";
    XmlRpcClient client;
    String mode = "SMS";
    Boolean layaway = false;

    //initialize global functions.
    public Functions(){
        client = new XmlRpcClient();
        XmlRpcClientConfigImpl clientConfig = new XmlRpcClientConfigImpl();
        clientConfig.setEnabledForExtensions(true);
        try {
            clientConfig.setServerURL(new URL("http", "52.89.125.104", 8069, "/xmlrpc/object"));
        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        client.setConfig(clientConfig);
    }

    //takes list of parameters in the for of list of objetcs eg
    // ArrayList<Object[]> listparam = new ArrayList<>();
    //Object[] exampleObjectParam = { "agent", "=", "True" };
    //listparam.add(exampleObjectParam);
    public ArrayList searchIDs(ArrayList<Object[]> listparam, String tableName){
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

            Log.e("Arguments for read : ", arg.toString());
            hm = (Object[]) client.execute("execute", arg);
        }catch(Exception e){
            e.printStackTrace();
        }
        return hm;

    }
    public int create(HashMap<String, Object> vals, String tableName){
        int id = 0;
        Object res = null;
        Object[] params = new Object[]{db, 1, password, tableName, "create", vals};
        try{
            Log.e("The output is: ",params.toString());
            res = client.execute("execute", params);
            id = Integer.parseInt(res.toString());
        }catch(Exception e){
            e.printStackTrace();
        }
        return id;
        //HashMap<String, Object> vals = new HashMap<String, Object>();
    }

    //place an
    public void createOrder(String productIds, String agentIds, String phone, String quantities){
        Object  [] prodDetails;
        Object  [] custDetails;
        Object  [] agentDetails;
        int _shop_id = 0;
        int _user_id = 0;

        //change phone to start with +254
        if(phone.contains("+254")){
            //phone is good
        }else if (phone.contains("254")){
            //add +
            phone = "+"+ phone;
        }else if(phone.substring(0,1).equals("0")){
            //remove 0 add +254
            int end  = phone.length();
            phone = phone.substring(1);
            phone = "+254" + phone;
            Log.e("the phone is: ", phone);
        }else{
            //not a valid number
        }
        Log.e("phone test ",phone.substring(0,1));
        Log.e("phone test 1 ",phone.substring(1,1));
        Log.e("phone test 2 ",phone.substring(1,2));
        Log.e("The phone is: ", phone);

        //read the prod details and put the products in an array
        Log.e("The prod Ids: ", productIds);
        String [] prodIds = productIds.split(",");
        int y = prodIds.length;
        Object[] params = { "name","list_price","id","taxes_id","weight","property_product_pricelist"};
        String tableName = "product.product";
        ArrayList prodList = new ArrayList();
        for (String id: prodIds){
            ArrayList ids = new ArrayList();
            ids.add(id);
            prodDetails =read(params, ids, tableName);
            prodList.add(prodDetails);
        }



        //read the agent details.
        Object[] paramAgent = { "name","phone","id","experiment_id","write_date","create_date","write_uid","create_uid","property_delivery_carrier","shop_id","user_id"};

        ArrayList agentList = new ArrayList();
        tableName = "res.partner";
        agentList.add(agentIds);
        agentDetails = read(paramAgent, agentList,tableName);
        HashMap globalMap = new HashMap();
        for (Object object : agentDetails) {
            HashMap hash = (HashMap)object;
            if (!hash.get("shop_id").equals(false)){
                Object[] objArr = (Object[]) hash.get("shop_id");
                _shop_id = (int) objArr[0];
                //System.out.println("Shop id is : "+ objArr[0]);
            }
            if (!hash.get("user_id").equals(false)){
                Object[] objArruser = (Object[]) hash.get("user_id");
                _user_id = (int) objArruser[0];
                //System.out.println("Shop id is : "+ objArruser[0]);
            }

            globalMap.put("name", hash.get("name"));
            //System.out.println(hash.values());
            //System.out.println(object.toString());
            /*System.out.println(hash.get("name"));
            System.out.println(hash.get("phone"));
            System.out.println(hash.get("id"));*/
            if(hash.get("experiment_id").toString() != "false"){
                Object[] ob = (Object[]) hash.get("experiment_id");

                //System.out.println(ob[0].toString());
                //System.out.println((Boolean) hash.get("experiment_id"));
            }

        }


        //check if the customer exists
        //searchIDs(ArrayList<Object[]> listparam, String tableName)
        Object[] ObjectParam = { "name", "=", phone };
        ArrayList<Object[]> listparam = new ArrayList<Object[]>();
        listparam.add(ObjectParam);
        tableName= "res.partner";
        ArrayList custIds = searchIDs(listparam, tableName);
        int custId;
        if (custIds.size() == 0){
            //create new agent
            HashMap<String, Object> vals = new HashMap<String, Object>();
            vals.put("name", phone);
            vals.put("phone", phone);
            vals.put("mobile", phone);
            vals.put("customer", "True");
            custId = create(vals,"res.partner");
        }else{
            custId = (int) custIds.get(0);
        }

        //read the customer details and put it in an object
        Object[] paramCustomer = {};
        ArrayList custList = new ArrayList();
        tableName = "res.partner";
        custList.add(custId);
        custDetails = read(paramCustomer,custList,tableName);


        //create the product order lines
        Object res = null;
        LinkedHashMap<String, Object> linked_vals = new LinkedHashMap<String, Object>();//use linkedHashMap to maintain order

        linked_vals = new LinkedHashMap<String, Object>();

        //do the iteration for the products here
        //vals.put("ref", "MGA");
        String[] quant = quantities.split(",");
        int x = 0;

        List<Vector> linearArr1 = null;
        String bufferCreator="{";
        for(Object prod:prodList){
            HashMap hash = (HashMap)prod;
            String prod_name = (String) hash.get("name");
            Double prod_price = (Double) hash.get("list_price");
            Object[] prod_objTax = (Object[]) hash.get("taxes_id"); //an object array
            Double prod_weight = (Double) hash.get("weight");
            int prod_id  = (int) hash.get("id");
            int prod_tax_id = (int) prod_objTax[0];

            String product_uom_qty = quant[x];
            x++;
            linked_vals.put("product_id", prod_id);
            linked_vals.put("name", prod_name);
            linked_vals.put("price_unit", prod_price);
            linked_vals.put("tax_id",prod_objTax);
            linked_vals.put("weight", prod_weight);
            linked_vals.put("product_uom_qty", product_uom_qty);

            Vector<Object> linearg = new Vector<Object>();
            linearg.add(0);
            linearg.add(0);
            linearg.add(linked_vals);
            linearArr1.add(linearg);
        }
        Vector[] linearArr = (Vector[]) linearArr1.toArray();

        /********************************************************************************* The expected output *********************************************************/
        /**************************************[0, 0, {product_id=0, name=Ken Tomato Sauce - dozen, price_unit=null, weight=null, product_uom_qty=2.00}]************/

        /*********************************************[Ljava.util.Vector;@2ff5659e**************************/
				    /*/|\because it is an array of objects*/
        System.out.println(linearArr);

        try{
            HashMap<String, Object> vals = new HashMap<String, Object>();





            vals.put("pricelist_id", 1);
            vals.put("partner_id", custId);
            vals.put("partner_shipping_id",custId);
            vals.put("partner_invoice_id",custId);
            vals.put("vendor_partner_id",agentIds);
            /*vals.put("date_order",date_order);
            vals.put("copia_date_order",copia_date_order);
            vals.put("date_delivery",date_delivery);*/
            vals.put("order_line",linearArr);
            vals.put("mode",mode);
            vals.put("islayaway",layaway);
            vals.put("carrier_id",0);
            vals.put("shop_id",_shop_id);
            vals.put("user_id",_user_id);




            Vector<Object> args = new Vector<Object>();
            args.add(db);
            args.add(1);
            args.add(password);
            args.add("sale.order");
            args.add("create");
            args.add(vals);
            System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>check out the args<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
            System.out.println(args);
            int result = 0;
            result = (int)client.execute("execute", args);
            System.out.println(" Them result is >>>>>>>>>>>>>>>>"+ result);
        }catch(Exception ex){
            ex.printStackTrace();
        }



    }

}
