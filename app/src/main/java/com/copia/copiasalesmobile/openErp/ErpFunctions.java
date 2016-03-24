package com.copia.copiasalesmobile.openErp;

import android.util.Log;

import com.copia.copiasalesmobile.SQLite.DatabaseConnectorSqlite;
import com.copia.copiasalesmobile.utilities.Order;
import com.copia.copiasalesmobile.utilities.getOrders;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Vector;

/**
 * Created by mbuco on 3/14/16.
 */
public class ErpFunctions {
    public int placeOrder(String phone, DatabaseConnectorSqlite dbconnector,String agentId,String productIds,String quantities){
        int _shop_id = 0;
        int _user_id = 0;
        String mode = "ecatalog";
        Boolean layaway = false;
        Object  [] prodDetails;
        Object  [] custDetails;
        Object  [] agentDetails;
        Operation op = new Operation("",0,dbconnector);
        Integer int_year, int_month, int_date, int_hour, int_min, int_sec;
        int orderId = 0;
        String tableName;

        //CHECK IF CUSTOMER EXISTS
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


        //check if the customer exists
        //searchIDs(ArrayList<Object[]> listparam, String tableName)
        Object[] ObjectParam = { "name", "=", phone };
        ArrayList<Object[]> listparam = new ArrayList<Object[]>();
        listparam.add(ObjectParam);
        tableName= "res.partner";
        ArrayList custIds = op.search(listparam, tableName);
        int custId;
        //if does not exist create agent
        if (custIds.size() == 0){
            HashMap<String, Object> vals = new HashMap<String, Object>();
            vals.put("name", phone);
            vals.put("phone", phone);
            vals.put("mobile", phone);
            vals.put("customer", "True");
            custId = op.create(vals, "res.partner");
        }else{
            custId = (int) custIds.get(0);
        }
        //read the customer details and put it in an object
        Object[] paramCustomer = {};
        ArrayList custList = new ArrayList();
        tableName = "res.partner";
        custList.add(custId);
        custDetails = op.read(paramCustomer,custList,tableName);


        //read the agent details.
        //read the agent details.
        Object[] paramAgent = { "name","phone","id","experiment_id","write_date","create_date","write_uid","create_uid","property_delivery_carrier","shop_id","user_id"};
        ArrayList agentList = new ArrayList();
        tableName = "res.partner";
        agentList.add(agentId);
        agentDetails = op.read(paramAgent, agentList, tableName);
        HashMap globalMap = new HashMap();
        for (Object object : agentDetails) {
            HashMap hash = (HashMap)object;
            if (!hash.get("shop_id").equals(false)){
                Object[] objArr = (Object[]) hash.get("shop_id");
                _shop_id = (int) objArr[0];
            }
            if (!hash.get("user_id").equals(false)){
                Object[] objArruser = (Object[]) hash.get("user_id");
                _user_id = (int) objArruser[0];
            }

            globalMap.put("name", hash.get("name"));
            if(hash.get("experiment_id").toString() != "false"){
                Object[] ob = (Object[]) hash.get("experiment_id");
            }
        }


        //read the products and prepare them
        //read the prod details and put the products in an array
        Log.e("The prod Ids: ", productIds);
        String [] prodIds = productIds.split(",");
        int y = prodIds.length;
        Object[] params = { "name","list_price","id","taxes_id","weight","property_product_pricelist"};
        tableName = "product.product";
        ArrayList prodList = new ArrayList();
        for (String id: prodIds){
            ArrayList ids = new ArrayList();
            ids.add(id);
            prodDetails =op.read(params, ids, tableName);
            Log.e("HHHHHHHHHHHHHHHHHH","HEHEHEHEHEHEHEHEHHEHEH");
            Log.e("Them Prod Details:", prodDetails[0].toString());
            prodList.add(prodDetails[0]);
        }

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

            HashMap hash = (HashMap)obj;
            String prod_name = (String) hash.get("name");
            Double prod_price = (Double) hash.get("list_price");
            Object[] prod_objTax = (Object[]) hash.get("taxes_id"); //an object array
            Double prod_weight = (Double) hash.get("weight");
            int prod_id  = (int) hash.get("id");
            int prod_tax_id = (int) prod_objTax[0];

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

            Vector<Object> linearg = new Vector<>();
            linearg.add(0);
            linearg.add(0);
            linearg.add(linked_vals);
            Log.e("Them Linear Args",linearg.toString());
            n.put("n"+a,linearg);
            linearArr1.add(linearg);
            a++;
        }
        Object[] linearArr = (Object[]) linearArr1.toArray();

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


            //Log.e("Expected delivery : ", expected_delivery_date + " Current Date : " + date_time);


            Log.e("The order details: ", "The cust id: " + custId + " Vendor partner id: " + agentId + " date_time: " + date_time + " copia_date_order: " + expected_delivery_date
                    + " order_line " + linearArr + " Mode " + mode + " is layaway" + layaway + " Shop id " + _shop_id + " user id: " + _user_id);
            vals.put("pricelist_id", 1);
            vals.put("partner_id", custId);
            vals.put("partner_shipping_id",custId);
            vals.put("partner_invoice_id",custId);
            vals.put("vendor_partner_id",agentId);
            vals.put("date_order",date_time);
            vals.put("copia_date_order",date_time);
            //vals.put("copia_date_order",expected_delivery_date);
            vals.put("date_delivery",date_time);
            //vals.put("date_delivery",expected_delivery_date);
            vals.put("order_line",linearArr);
            vals.put("mode",mode);
            vals.put("islayaway",layaway);
            vals.put("carrier_id",0);
            vals.put("shop_id",_shop_id);
            vals.put("user_id",_user_id);


            op.create(vals,"sale.order");

            //change this
            dbconnector.updateOrderTable(order.getOrder_id(),
                    order.getCust_phone_(),
                    order.getDate_time_(),
                    order.getExpected_delivery_date_()
                    , "1","");
        }catch(Exception ex){
            ex.printStackTrace();
        }

        return orderId;
    }
}

