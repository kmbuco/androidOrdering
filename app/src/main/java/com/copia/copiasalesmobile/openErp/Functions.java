package com.copia.copiasalesmobile.openErp;

import android.database.Cursor;
import android.util.Log;

import com.copia.copiasalesmobile.SQLite.DatabaseConnectorSqlite;
import com.copia.copiasalesmobile.utilities.Order;
import com.copia.copiasalesmobile.utilities.getOrders;

import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Vector;

/**
 * Created by mbuco on 2/29/16.
 */
public class Functions {

    DatabaseConnectorSqlite dbconnector;

    static final String db = "copiaERP";
    static final String host = "http://52.89.125.104";
    static final int port = 8069;
    static final String password = "@dm1n2014_cop1a_erp";
    String phone;
    public Integer int_year, int_month, int_date, int_hour, int_min, int_sec;
    XmlRpcClient client;
    XmlRpcClient client1;
    String mode = "ecatalog";
    Boolean layaway = false;

    //initialize global functions.
    public Functions(DatabaseConnectorSqlite dbconnector){
        this.dbconnector = dbconnector;
        client = new XmlRpcClient();
        client1 = new XmlRpcClient();
        XmlRpcClientConfigImpl clientConfig = new XmlRpcClientConfigImpl();
        XmlRpcClientConfigImpl clientConfig1 = new XmlRpcClientConfigImpl();
        clientConfig.setEnabledForExtensions(true);
        clientConfig1.setEnabledForExtensions(true);
        try {
            clientConfig.setServerURL(new URL("http", "52.89.125.104", 8069, "/xmlrpc/object"));
            clientConfig1.setServerURL(new URL("http", "52.89.125.104", 8069, "/xmlrpc/common"));
        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        client.setConfig(clientConfig);
        client1.setConfig(clientConfig1);
    }

    public int Login(String username, String password){
        int login = 0;
        Object[] ObjectParam = { "login", "=", username};
        Object[] ObjectParam1 = { "password", "=", password };


        ArrayList<Object[]> listparam = new ArrayList<Object[]>();
        listparam.add(ObjectParam);
        //listparam.add(ObjectParam1);
        String tableName= "res.users";
        ArrayList ids = searchIDs(listparam,tableName);

        //int 1->successful login int 0->incorrect username 2->incorrect password
        if(ids.size() > 0){
            login = 1;
        }else if(ids.size() <= 0){
            login = 0;
            return login;
        }

        //object reading params
        //Object[] params = { "name","list_price","id","taxes_id","weight","property_product_pricelist"};
        //list ids is arraylist of IDs
        //eg.listIds = new ArrayList<>();
        //public Object [] read(Object[] params,ArrayList listIds, String tableName){


        /*String experiment_id;
        String active;
        String partner_id;
        String password_crypt = "";

        Object[] params = {"experiment_id","active","partner_id","password"};
        ArrayList listId = new ArrayList();
        listId.add(ids.get(0));
        listparam.add(ObjectParam1);
        tableName= "res.users";
        Object[] objRead = read(params,listId,tableName);
        ArrayList idpass = searchIDs(listparam, tableName);
        if(idpass.size()<0){
            login = 1;
        }else if(idpass.size()<=0){
            login = 2;
        }*/


        /*for (Object object : objRead) {
            HashMap hash = (HashMap)object;
            hash.get("experiment_id");
            hash.get("active");
            hash.get("partner_id");
            password_crypt = (String) hash.get("password");
        }
        Log.e("The password","password: "+password_crypt );*/

        Log.e("Login: ", Integer.toString(login) );
        return login;
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
                for (Object obj : ids) {
                    int a = Integer.parseInt(obj.toString());
                    listIds.add(a);
                    Log.e("The search Ids: ",Integer.toString(a));
            }


            //if you want to you can print out the results here

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
            Log.e("The output is: ", params.toString());
            res = client.execute("execute", params);
            id = Integer.parseInt(res.toString());
        }catch(Exception e){
            e.printStackTrace();
        }
        return id;
        //HashMap<String, Object> vals = new HashMap<String, Object>();
    }

    //place an
    public int createOrder(String productIds, String agentIds, String phone, String quantities,String date_delivery, String sOrderId,String ref){
        Object  [] prodDetails;
        Object  [] custDetails;
        Object  [] agentDetails;
        int order_id = 0;
        this.phone = phone;
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
            Log.e("HHHHHHHHHHHHHHHHHH","HEHEHEHEHEHEHEHEHHEHEH");
            Log.e("Them Prod Details:", prodDetails[0].toString());
            prodList.add(prodDetails[0]);
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

        //create the product Order lines
        Object res = null;
        LinkedHashMap<String, Object> linked_vals = new LinkedHashMap<String, Object>();//use linkedHashMap to maintain Order

        linked_vals = new LinkedHashMap<String, Object>();

        //do the iteration for the products here
        //vals.put("ref", "MGA");
        String[] quant = quantities.split(",");
        int x = 0;

        Vector<Object> linearArr1 = new Vector<>();
        Vector vec;
        String bufferCreator="{";
        int z = prodList.size();
        Map<String, Vector<Object>> n = new HashMap<String, Vector<Object>>();
        int a = 1;
        for(Object obj:prodList){
            Log.e("the objects",obj.toString());
            linked_vals = new LinkedHashMap<String, Object>();

            HashMap hash = (HashMap)obj;
            String prod_name = (String) hash.get("name");
            Double prod_price = (Double) hash.get("list_price");
            Object[] prod_objTax = (Object[]) hash.get("taxes_id"); //an object array
            Double prod_weight = (Double) hash.get("weight");
            int prod_id  = (int) hash.get("id");
//            int prod_tax_id = (int) prod_objTax[0];

            //do the iteration for the products here
            //vals.put("ref", "MGA");
            int Op1 = 6;
            int Op2 = 0;

            Vector<Object> taxArgs = new Vector<Object>();
            taxArgs.add(Op1);
            taxArgs.add(Op2);
            taxArgs.add(prod_objTax);

            Object[] taxObj = {taxArgs};

            String product_uom_qty = quant[x];
            x++;
            linked_vals.put("product_id", prod_id);
            linked_vals.put("name", prod_name);
            linked_vals.put("price_unit", prod_price);
            linked_vals.put("tax_id",taxObj);
            linked_vals.put("weight", prod_weight);
            linked_vals.put("product_uom_qty", product_uom_qty);

            Log.e("Them Prod>>>>>","**********************************************************************");
            Log.e("Them Prod>>>>>","**********************************************************************");
            Log.e("Them Prod>>>>>","**********************************************************************");
            Log.e("Them Prod>>>>>","**********************************************************************");
            Log.e("Them Prod>>>>>","product_id " + prod_id +" name " +prod_name+ " prod_price "+prod_price + " taxObj " +taxObj+ "prod_weight" + prod_weight + "product_uom_qty" +product_uom_qty);

            Vector<Object> linearg = new Vector<>();
            linearg.add(0);
            linearg.add(0);
            linearg.add(linked_vals);
            Log.e("Them Linear Args",linearg.toString());
            linearArr1.add(a-1,linearg);
            n.put("n"+a,linearg);
            //linearArr1.add(linearg);
            a++;
        }

        //Vector<Object>[] vect = (Vector[]) linearArr1.toArray();
        //Log.e("The vec : ", vect.toString());

        //Vector[] linearArr = linearArr1.toArray();

        Object[] linearArr = (Object[]) linearArr1.toArray();


        /********************************************************************************* The expected output *********************************************************/
        /**************************************[0, 0, {product_id=0, name=Ken Tomato Sauce - dozen, price_unit=null, weight=null, product_uom_qty=2.00}]************/

        /*********************************************[Ljava.util.Vector;@2ff5659e**************************/
				    /*/|\because it is an array of objects*/
        Log.e("The ArrayList : ",linearArr.toString());
        System.out.println("The ArrayList" + linearArr1.toString());

        getOrders getorder = new getOrders();
        Order order = getorder.getOrder(dbconnector, phone);

        String date_time = order.getDate_time_();
        String expected_delivery_date = order.getExpected_delivery_date_();
        final Calendar c = Calendar.getInstance();
        int_year = c.get(Calendar.YEAR);
        int_month = c.get(Calendar.MONTH);
        int_date = c.get(Calendar.DAY_OF_MONTH);
        date_time = new StringBuffer().append(int_year).append("-").append(int_month+1).append("-").append(int_date).toString();

        try{
            HashMap<String, Object> vals = new HashMap<String, Object>();


            Log.e("Expected delivery : ", expected_delivery_date + " Current Date : " + date_time);


            Log.e("The order details: ", "The cust id: " + custId + " Vendor partner id: " + agentIds + " date_time: " + date_time + " copia_date_order: " + expected_delivery_date
                    + " order_line " + linearArr + " Mode " + mode + " is layaway" + layaway + " Shop id " + _shop_id + " user id: " + _user_id);
            vals.put("pricelist_id", 1);
            vals.put("partner_id", custId);
            vals.put("partner_shipping_id",custId);
            vals.put("partner_invoice_id",custId);
            vals.put("vendor_partner_id",agentIds);
            vals.put("date_order",date_time);
            vals.put("copia_date_order",date_time);
            //vals.put("copia_date_order",expected_delivery_date);
            vals.put("date_delivery",expected_delivery_date);
            vals.put("client_order_ref",ref);
            //vals.put("date_delivery",expected_delivery_date);
            vals.put("order_line",linearArr);
            vals.put("mode",mode);
            vals.put("islayaway",layaway);
            vals.put("carrier_id",0);
            vals.put("shop_id", 1);
            vals.put("user_id", _user_id);




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
            Log.e("The args are : "," Them args is >>>>>>>>>>>>>>>>"+ args);
            order_id = (int)client.execute("execute", args);
            Log.e("Result : "," Them result is >>>>>>>>>>>>>>>>"+ order_id);
            Log.e("Update : "," Order ID "+ order_id+ " Cust Phone: "+phone + " Order_Date_time "+order.getDate_time_()+ " order expected delivery date: " +order.getExpected_delivery_date_()
            +" Agent id: "+agentIds);

            // get the order before updating the table
            //2 sync orders 0 not sent orders 1 sent orders
            dbconnector.updateOrderTable(sOrderId,
                    phone,
                    date_time,
                    date_delivery
                    , "1",agentIds);
        }catch(Exception ex){
            ex.printStackTrace();
        }

        return order_id;
    }

}
